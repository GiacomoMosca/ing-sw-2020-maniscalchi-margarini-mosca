package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.PlayerController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.view.VirtualView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private final int port;
    private final ArrayList<GameController> gameControllers;
    private final ArrayList<VirtualView> players;
    private boolean running;


    public Server(int port) {
        this.port = port;
        this.gameControllers = new ArrayList<GameController>();
        this.players = new ArrayList<VirtualView>();
        this.running = true;
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

        while (running) {
            try {
                Socket client = socket.accept();
                new Thread(() -> newPlayerWorker(client)).start();
            } catch (IOException e) {
                System.out.println("Connection dropped. ");
            }
        }
    }

    private void newPlayerWorker(Socket client) {
        ObjectOutputStream outputStream = null;
        ObjectInputStream inputStream = null;
        VirtualView player = null;
        try {
            outputStream = new ObjectOutputStream(client.getOutputStream());
            inputStream = new ObjectInputStream(client.getInputStream());
            player = new VirtualView(client, inputStream, outputStream);

            boolean taken = false;
            String nickname = null;
            while (true) {
                nickname = player.chooseNickname(taken);
                synchronized (players) {
                    for (VirtualView p : players) {
                        if (nickname == p.getId()) {
                            taken = true;
                            break;
                        }
                    }
                    if (!taken) {
                        players.add(player);
                        break;
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            players.remove(player);
        }
        playerLobby(player);
    }

    private void playerLobby(VirtualView player) {
        try {
            boolean newGame = player.chooseStartJoin();
            if (newGame) newRoom(player);
            else joinRoom(player);
        } catch (IOException | ClassNotFoundException e) {
            players.remove(player);
        }
    }

    private void newRoom(VirtualView player) {
        try {
            int playerNum = player.choosePlayersNumber();
            String gameName = null;
            boolean taken = false;
            while (true) {
                gameName = player.chooseGameName(taken);
                synchronized (gameControllers) {
                    taken = false;
                    for (GameController gameController : gameControllers) {
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
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void joinRoom(VirtualView player) {
        try {
            GameController gameController = null;
            boolean spectator = false;
            int gameRoom = -1;
            while (true) {
                ArrayList<Game> gameRooms = new ArrayList<Game>();
                for (GameController game : gameControllers) {
                    gameRooms.add(game.getGame());
                }
                gameRoom = player.chooseGameRoom(gameRooms);
                if (gameRoom != 0) {
                    gameController = gameControllers.get(gameRoom - 1);
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
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void gameWorker(GameController gameController) {
        gameController.gameSetUp();
        gameControllers.remove(gameController);
        for (PlayerController controller : gameController.getAllControllers()) {
            new Thread(() -> playerLobby(controller.getClient())).start();
        }
    }

}
