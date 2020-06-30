package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.PlayerController;
import it.polimi.ingsw.exceptions.GameEndedException;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.view.VirtualView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Server instance that handles games and connections with players.
 */
public class Server {

    private final AtomicBoolean running;
    private final int port;
    private final ArrayList<GameController> gameControllers;
    private final ArrayList<VirtualView> players;
    private ServerSocket socket;
    private Logger logger;


    /**
     * Server constructor.
     *
     * @param port the port the Server Socket will be connected to
     */
    public Server(int port) {
        this.running = new AtomicBoolean(true);
        this.port = port;
        this.gameControllers = new ArrayList<GameController>();
        this.players = new ArrayList<VirtualView>();
    }

    /**
     * Starts the server by:
     * <ul>
     *     <li>creating a new Logger;
     *     <li>creating a new ServerSocket at the previously specified port, which will be used to accept the requests of connections from the clients;
     *     <li>creating a new Socket instance for each client connecting to the server, establishing the communication channel between that client and the server;
     *     <li>starting a new Thread for each client connecting to the server.
     * </ul>
     */
    public void start() {
        try {
            logger = new Logger();
        } catch (IOException e) {
            e.printStackTrace();
            stop();
            System.exit(1);
            return;
        }
        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            logger.logError("Cannot open server socket. ");
            stop();
            System.exit(1);
            return;
        }

        logger.log("started");

        while (running.get()) {
            try {
                Socket client = socket.accept();
                new Thread(() -> newPlayerWorker(client)).start();
            } catch (IOException e) {
                //
            }
        }
        stop();
    }

    /**
     * Closes the logger, notifies all players that the server is closing and closes the socket.
     */
    private void stop() {
        logger.log("stopped");
        logger.close();
        for (VirtualView player : players) {
            try {
                player.serverClosed();
            } catch (IOException e) {
                // no need to handle disconnection, server is closing
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            //
        }
    }

    /**
     * This method starts on a new Thread for each client connecting to the server.
     * Creates a new VirtualView for the client, providing the Socket and the associated ObjectInputStream and ObjectOutputStream.
     * Asks the client to choose his nickname (which can't be duplicated and must be no longer that 12 characters).
     * Starts a new Thread which will permanently check if the Player is still connected.
     *
     * @param client the Socket instance created for this client
     */
    private void newPlayerWorker(Socket client) {
        logger.log("new connection accepted");
        VirtualView player = null;
        try {
            player = new VirtualView(client, new ObjectInputStream(client.getInputStream()), new ObjectOutputStream(client.getOutputStream()));

            boolean taken = false;
            String nickname;
            while (true) {
                nickname = player.chooseNickname(taken);
                taken = false;
                synchronized (players) {
                    if (nickname.startsWith("/")) taken = true;
                    else for (VirtualView p : players) {
                        if (nickname.equals(p.getId())) {
                            taken = true;
                            break;
                        }
                    }
                    if (!taken) {
                        players.add(player);
                        logger.log("new player " + nickname + " joined");
                        break;
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            removePlayer(player);
        }
        VirtualView finalPlayer = player;
        new Thread(() -> checkAlive(finalPlayer)).start();
        playerLobby(finalPlayer);
    }

    /**
     * Continuously suspends the Thread execution for 5 seconds and then checks if the Player is still connected by calling the checkAlive method.
     *
     * @param player the VirtualView associated to the Player whose aliveness needs to be checked
     */
    private void checkAlive(VirtualView player) {
        while (true) {
            try {
                Thread.sleep(5 * 1000);
                player.checkAlive();
            } catch (InterruptedException e) {
                //
            } catch (IOException e) {
                removePlayer(player);
                return;
            }
        }
    }

    /**
     * Handles the choice of the Player to create a new Game or to join an existing one.
     *
     * @param player the VirtualView representing the Player who previously connected to the server
     */
    private void playerLobby(VirtualView player) {
        try {
            boolean newGame = player.chooseStartJoin();
            if (newGame) newRoom(player);
            else joinRoom(player);
        } catch (IOException | InterruptedException e) {
            removePlayer(player);
        }
    }

    /**
     * Handles the creation of a new Game room.
     * Asks to the Player to choose the Game name, not allowing duplicates.
     * When a new Game room is created, creates its GameController.
     *
     * @param player the VirtualView representing the Player who is creating the Game room
     * @throws IOException          when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException when the thread handling the communication is waiting and it is interrupted before or during its activity
     */
    private void newRoom(VirtualView player) throws IOException, InterruptedException {
        String gameName;
        boolean taken = false;
        do {
            gameName = player.chooseGameName(taken);
            synchronized (gameControllers) {
                taken = false;
                if (gameName.startsWith("/")) taken = true;
                else for (GameController gameController : gameControllers) {
                    if (gameName.equals(gameController.getGame().getName())) {
                        taken = true;
                        break;
                    }
                }
            }
        } while (taken);
        int playerNum = player.choosePlayersNumber();
        GameController gameController = new GameController(player, playerNum, gameName);
        gameController.setLogger(logger);
        gameControllers.add(gameController);
        logger.log("new game " + gameName + " created");
        try {
            gameController.broadcastGameInfo("playerJoined");
            player.displayMessage("Waiting for players...");
        } catch (IOExceptionFromController e) {
            gameController.handleDisconnection(e.getController());
        }
    }

    /**
     * Allows a Player to join an existing Game. The Player can:
     * <ul>
     *     <li>choose a Game room to join;
     *     <li>refresh the list of the currently active Game rooms;
     *     <li>go back to the previous screen.
     * </ul>
     * The Game in the Game Room starts when the expected number of participants joined.
     *
     * @param player the VirtualView representing the Player who is joining the Game room
     * @throws IOException          when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException when the thread handling the communication is waiting and it is interrupted before or during its activity
     */
    private void joinRoom(VirtualView player) throws IOException, InterruptedException {
        GameController gameController = null;
        String gameRoom;
        try {
            while (true) {
                ArrayList<Game> gameRooms = new ArrayList<Game>();
                for (GameController game : gameControllers) {
                    if (!game.checkPlayersNumber()) gameRooms.add(game.getGame());
                }
                gameRoom = player.chooseGameRoom(gameRooms);
                if (gameRoom.equals("/back")) {
                    new Thread(() -> playerLobby(player)).start();
                    return;
                }
                if (!gameRoom.equals("/refresh")) {
                    for (GameController game : gameControllers) {
                        if (game.getGame().getName().equals(gameRoom)) {
                            gameController = game;
                            break;
                        }
                    }
                    if (gameController == null) throw new GameEndedException("game ended");
                    break;
                }
            }
            logger.log(player.getId() + " joined " + gameController.getGame().getName());
            gameController.addPlayer(player);
            if (gameController.checkPlayersNumber()) {
                startGame(gameController);
            } else
                player.displayMessage("Waiting for players...");
        } catch (GameEndedException e) {
            player.displayMessage("The room doesn't exist anymore. ");
            if (gameController != null) removeGame(gameController);
            new Thread(() -> playerLobby(player)).start();
        }
    }

    /**
     * Notifies all the connected clients that the Game is starting.
     * Starts a new Thread which will handle the setup and the execution of the Game.
     *
     * @param gameController the GameController for this Game
     * @throws IOException          when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException when the thread handling the communication is waiting and it is interrupted before or during its activity
     */
    private void startGame(GameController gameController) throws IOException, InterruptedException {
        gameController.getControllers().get(0).getClient().notifyGameStarting();
        logger.log("game " + gameController.getGame().getName() + " started");
        new Thread(() -> gameWorker(gameController)).start();
    }

    /**
     * Gives control to the gameController, to handle the setup and the execution of the associated Game.
     * When the Game ends, calls removeGame so that it can be removed from the list of the active Games.
     *
     * @param gameController the gameController of the Game
     */
    private void gameWorker(GameController gameController) {
        gameController.gameSetUp();
        removeGame(gameController);
    }

    /**
     * When a Game ends, removes the associated gameController from the list of the active gameControllers.
     * If a Player doesn't disconnect after the Game ends, allows him to start a new Game or joining an existing one by redirecting him to the lobby.
     *
     * @param gameController the gameController of the Game to be removed
     */
    private void removeGame(GameController gameController) {
        if (gameController.isRunning() || !gameControllers.contains(gameController)) return;
        gameControllers.remove(gameController);
        logger.log("game " + gameController.getGame().getName() + " ended");
        for (PlayerController controller : gameController.getControllers()) {
            if (controller == null) continue;
            new Thread(() -> playerLobby(controller.getClient())).start();
        }
    }

    /**
     * Removes a Player when he disconnected.
     *
     * @param player the VirtualView representing the Player to be removed
     */
    private void removePlayer(VirtualView player) {
        if (player == null || !players.contains(player)) return;
        if (player.isInGame() && player.getPlayerController().getGame().isSetup()) {
            GameController gameController = player.getPlayerController().getGame();
            gameController.handleDisconnection(player.getPlayerController());
            if (!gameController.isRunning()) removeGame(gameController);
        }
        players.remove(player);
        logger.log(player.getId() + " disconnected");
        try {
            player.getSocket().close();
        } catch (IOException e) {
            //
        }
    }

}
