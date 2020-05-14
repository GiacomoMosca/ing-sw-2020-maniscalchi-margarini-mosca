package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.PlayerController;
import it.polimi.ingsw.exceptions.GameEndedException;
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


    public Server(int port) {
        this.running = new AtomicBoolean(true);
        this.port = port;
        this.gameControllers = new ArrayList<GameController>();
        this.players = new ArrayList<VirtualView>();
    }

    public void start() {
        ServerSocket socket;
        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Cannot open server socket. ");
            System.exit(1);
            return;
        }

        while (running.get()) {
            try {
                Socket client = socket.accept();
                new Thread(() -> newPlayerWorker(client)).start();
            } catch (IOException e) {
                System.out.println("Connection dropped. ");
            }
        }
    }

    private void newPlayerWorker(Socket client) {
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
                        // System.out.println(player.getId() + " joined");
                        break;
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
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
                // System.out.println(player.getId() + " rip");
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
        } catch (IOException | ClassNotFoundException e) {
            removePlayer(player);
        }
    }

    private void newRoom(VirtualView player) throws IOException, ClassNotFoundException {
        int playerNum = player.choosePlayersNumber();
        String gameName;
        boolean taken = false;
        while (true) {
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
                if (!taken) {
                    GameController gameController = new GameController(player, playerNum, gameName);
                    gameControllers.add(gameController);
                    player.displayMessage("Waiting for players...");
                    break;
                }
            }
        }
    }

    private void joinRoom(VirtualView player) throws IOException, ClassNotFoundException {
        GameController gameController = null;
        boolean spectator = false;
        String gameRoom;
        try {
            while (true) {
                ArrayList<Game> gameRooms = new ArrayList<Game>();
                for (GameController game : gameControllers) {
                    gameRooms.add(game.getGame());
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
                    if (gameController.checkPlayersNumber()) {
                        if (player.chooseYesNo("The game room is full. Do you want to join as a spectator?"))
                            spectator = true;
                        else continue;
                    }
                    break;
                }
            }
            player.displayMessage("Joining room " + gameController.getGame().getName() + "...");
            if (spectator) {
                gameController.addSpectator(player);
            } else {
                gameController.addPlayer(player);
                if (gameController.checkPlayersNumber()) {
                    GameController finalGameController = gameController;
                    new Thread(() -> gameWorker(finalGameController)).start();
                } else player.displayMessage("Waiting for players...");
            }
        } catch (GameEndedException e) {
            player.displayMessage("The room doesn't exist anymore. ");
            if (gameController != null) removeGame(gameController);
            new Thread(() -> playerLobby(player)).start();
        }
    }

    private void gameWorker(GameController gameController) {
        gameController.gameSetUp();
        removeGame(gameController);
    }

    private void removeGame(GameController gameController) {
        if (gameController.isRunning() || !gameControllers.contains(gameController)) return;
        gameControllers.remove(gameController);
        for (PlayerController controller : gameController.getAllControllers()) {
            new Thread(() -> playerLobby(controller.getClient())).start();
        }
    }

    private void removePlayer(VirtualView player) {
        if (player.isInGame() && player.getPlayerController().getGame().isSetup()) {
            GameController gameController = player.getPlayerController().getGame();
            gameController.handleDisconnection(player.getPlayerController());
            if (!gameController.isRunning()) removeGame(gameController);
        }
        players.remove(player);
        try {
            player.getSocket().close();
        } catch (IOException e) {
            //
        }
    }

}
