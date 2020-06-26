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

public class Server {

    private final AtomicBoolean running;
    private final int port;
    private final ArrayList<GameController> gameControllers;
    private final ArrayList<VirtualView> players;
    private Logger logger;


    public Server(int port) {
        this.running = new AtomicBoolean(true);
        this.port = port;
        this.gameControllers = new ArrayList<GameController>();
        this.players = new ArrayList<VirtualView>();
    }

    public void start() {
        try {
            logger = new Logger();
        } catch (IOException e) {
            e.printStackTrace();
            stop();
            System.exit(1);
            return;
        }
        ServerSocket socket;
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
    }

    private void stop() {
        logger.close();
    }

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

    private void playerLobby(VirtualView player) {
        try {
            boolean newGame = player.chooseStartJoin();
            if (newGame) newRoom(player);
            else joinRoom(player);
        } catch (IOException | InterruptedException e) {
            removePlayer(player);
        }
    }

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
        gameControllers.add(gameController);
        logger.log("new game " + gameName + " created");
        try {
            gameController.broadcastGameInfo("playerJoined");
        } catch (IOExceptionFromController e) {
            gameController.handleDisconnection(e.getController());
        }
    }

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

    private void startGame(GameController gameController) throws IOException, InterruptedException {
        gameController.getControllers().get(0).getClient().notifyGameStarting();
        logger.log("game " + gameController.getGame().getName() + " started");
        new Thread(() -> gameWorker(gameController)).start();
    }

    private void gameWorker(GameController gameController) {
        gameController.gameSetUp();
        removeGame(gameController);
    }

    private void removeGame(GameController gameController) {
        if (gameController.isRunning() || !gameControllers.contains(gameController)) return;
        gameControllers.remove(gameController);
        logger.log("game " + gameController.getGame().getName() + " ended");
        for (PlayerController controller : gameController.getControllers()) {
            if (controller == null) continue;
            new Thread(() -> playerLobby(controller.getClient())).start();
        }
    }

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
