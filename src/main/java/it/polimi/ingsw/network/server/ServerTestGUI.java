package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.PlayerController;
import it.polimi.ingsw.exceptions.GameEndedException;
import it.polimi.ingsw.view.VirtualView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerTestGUI {

    private final AtomicBoolean running;
    private final int port;
    private final ArrayList<GameController> gameControllers;
    private final ArrayList<VirtualView> players;
    private final AtomicBoolean p1;


    public ServerTestGUI(int port) {
        this.running = new AtomicBoolean(true);
        this.port = port;
        this.gameControllers = new ArrayList<GameController>();
        this.players = new ArrayList<VirtualView>();
        p1 = new AtomicBoolean(true);
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
                newPlayerWorker(client);
            } catch (IOException e) {
                System.out.println("Connection dropped. ");
            }
        }
    }

    private void newPlayerWorker(Socket client) {
        VirtualView player = null;
        try {
            player = new VirtualView(client, new ObjectInputStream(client.getInputStream()), new ObjectOutputStream(client.getOutputStream()));
            if (p1.compareAndSet(true, false)) {
                player.setId("Test1");
                players.add(player);
                newRoom(player);
            } else {
                player.setId("Test2");
                players.add(player);
                joinRoom(player);
            }
        } catch (IOException | ClassNotFoundException e) {
            //
        }
    }

    private void newRoom(VirtualView player) throws IOException {
        int playerNum = 2;
        String gameName = "TestRoom";
        GameController gameController = new GameController(player, playerNum, gameName);
        gameControllers.add(gameController);
    }

    private void joinRoom(VirtualView player) throws IOException, ClassNotFoundException {
        GameController gameController = null;
        String gameRoom = "TestRoom";
        try {
            for (GameController game : gameControllers) {
                if (game.getGame().getName().equals(gameRoom)) {
                    gameController = game;
                }
            }
            gameController.addPlayer(player);
            GameController finalGameController = gameController;
            new Thread(() -> gameWorker(finalGameController)).start();
        } catch (GameEndedException e) {
            //
        }
    }

    private void gameWorker(GameController gameController) {
        gameController.gameSetUp();
    }

}
