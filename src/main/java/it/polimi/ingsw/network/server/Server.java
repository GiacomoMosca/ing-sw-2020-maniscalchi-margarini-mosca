package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.view.VirtualView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private int port;
    private ServerSocket socket;
    private GameController gameController;
    private ArrayList<String> playerList;

    public Server(int port) {
        this.port = port;
        this.gameController = null;
        this.playerList = new ArrayList<String>();
    }

    public void start() {
        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("cannot open server socket");
            System.exit(1);
            return;
        }
        while (gameController == null || !gameController.checkPlayersNumber()) {
            try {
                addPlayer();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        gameController.gameSetUp();
    }

    public synchronized void addPlayer() throws IOException, ClassNotFoundException {
        Socket cSocket = null;
        cSocket = socket.accept();
        ObjectOutputStream outputStream = null;
        ObjectInputStream inputStream = null;
        outputStream = new ObjectOutputStream(cSocket.getOutputStream());
        inputStream = new ObjectInputStream(cSocket.getInputStream());

        VirtualView player = new VirtualView(cSocket, inputStream, outputStream);
        playerList.add(player.chooseNickname(playerList));
        if (gameController == null) {
            gameController = new GameController(player, player.choosePlayersNumber());
            player.displayMessage("Waiting for players...");
        } else {
            player.displayMessage("Joining " + playerList.get(0) + "'s game...");
            gameController.addPlayer(player);
        }
    }

}
