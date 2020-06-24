package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.network.message.to_client.Ping;
import it.polimi.ingsw.network.message.to_client.ToClientMessage;
import it.polimi.ingsw.network.message.to_server.*;
import it.polimi.ingsw.view.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;


public class GUI implements UI {

    private final AtomicBoolean running;
    private final GUIManager manager;
    private final Object busyLock;
    private boolean connected;
    private Socket server;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private LinkedBlockingQueue<ToClientMessage> serverQueue;
    private SynchronousQueue<Object> messageQueue;
    private String id;
    private GameView currentGame;

    public GUI() {
        this.running = new AtomicBoolean();
        this.manager = new GUIManager();
        this.busyLock = manager.getLock();
        this.id = null;
    }

    @Override
    public void run() {

        connected = false;
        running.set(true);
        messageQueue = new SynchronousQueue<Object>();
        currentGame = null;

        manager.setGUI(this);
        manager.setQueue(messageQueue);
        new Thread(manager::run).start();

        String ip;
        do {
            server = new Socket();
            ip = getServerIp();
            try {
                server.connect(new InetSocketAddress(ip, 8000), 5 * 1000);
                connected = true;
            } catch (IOException e) {
                manager.initLogin();
                manager.serverErrorMessage("Server unreachable. ");
                connected = false;
            }
            if (connected) {
                try {
                    output = new ObjectOutputStream(server.getOutputStream());
                    input = new ObjectInputStream(server.getInputStream());
                    connected = true;
                } catch (IOException e) {
                    manager.initLogin();
                    manager.serverErrorMessage("Server is down. ");
                    connected = false;
                } catch (ClassCastException e) {
                    manager.initLogin();
                    manager.serverErrorMessage("Protocol violation. ");
                    connected = false;
                }
            }
        } while (!connected);

        serverQueue = new LinkedBlockingQueue<ToClientMessage>();
        new Thread(this::serverListener).start();
        ToClientMessage message;
        while (running.get()) {
            try {
                message = serverQueue.take();
            } catch (InterruptedException e) {
                System.out.println("Disconnected. ");
                break;
            }
            synchronized (busyLock) {
                while (!manager.setBusy(true)) {
                    try {
                        busyLock.wait();
                    } catch (InterruptedException e) {
                        //
                    }
                }
            }
            parseMessage(message);
        }
        stop();
    }

    public void serverListener() {
        ToClientMessage serverMessage;
        while (running.get()) {
            try {
                serverMessage = (ToClientMessage) input.readObject();
            } catch (IOException | ClassNotFoundException e) {
                running.compareAndSet(true, false);
                System.out.println("Disconnected. ");
                break;
            }
            if (serverMessage instanceof Ping) pong();
            else {
                try {
                    serverQueue.put(serverMessage);
                } catch (InterruptedException e) {
                    //
                }
            }
        }
    }

    private void quit() {
        stop();
    }

    public synchronized void stop() {
        if (!running.compareAndSet(true, false)) return;
        try {
            if (server != null) server.close();
            if (input != null) input.close();
            if (output != null) output.close();
        } catch (IOException e) {
            //
        }
    }

    public String getServerIp() {
        while (!manager.setBusy(true)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //
            }
        }
        manager.getServerIp();
        return getString();
    }

    public void parseMessage(ToClientMessage message) {
        message.performAction(this);
    }

    // get from queue

    public boolean getBoolean() {
        boolean val = false;
        try {
            val = (Boolean) messageQueue.take();
        } catch (InterruptedException e) {
            System.out.println("Error getting input. \n");
        }
        return val;
    }

    public int getInteger() {
        int val = -1;
        try {
            val = (Integer) messageQueue.take();
        } catch (InterruptedException e) {
            System.out.println("Error getting input. \n");
        }
        return val;
    }

    public ArrayList<Integer> getIntegers() {
        ArrayList<Integer> val = null;
        try {
            val = (ArrayList<Integer>) messageQueue.take();
        } catch (InterruptedException e) {
            System.out.println("Error getting input. \n");
        }
        return val;
    }

    public String getString() {
        String val = null;
        try {
            val = (String) messageQueue.take();
        } catch (InterruptedException e) {
            System.out.println("Error getting input. \n");
        }
        return val;
    }

    // send to server

    public void pong() {
        try {
            output.writeObject(new Pong(id));
        } catch (IOException e) {
            System.out.println("Disconnected. ");
            stop();
        }
    }

    public void sendBoolean(boolean body) {
        try {
            output.writeObject(new SendBoolean(id, body));
        } catch (IOException e) {
            System.out.println("Disconnected. ");
            stop();
        }
    }

    public void sendInteger(int body) {
        try {
            output.writeObject(new SendInteger(id, body));
        } catch (IOException e) {
            System.out.println("Disconnected. ");
            stop();
        }
    }

    public void sendIntegers(ArrayList<Integer> body) {
        try {
            output.writeObject(new SendIntegers(id, body));
        } catch (IOException e) {
            System.out.println("Disconnected. ");
            stop();
        }
    }

    public void sendString(String body) {
        try {
            output.writeObject(new SendString(id, body));
        } catch (IOException e) {
            System.out.println("Disconnected. ");
            stop();
        }
    }

    // message functions

    public void chooseCards(ArrayList<CardView> possibleCards, int num, ArrayList<CardView> pickedCards) {
        manager.chooseCards(possibleCards, num, pickedCards);
        sendIntegers(getIntegers());
    }

    public void chooseGameName(boolean taken) {
        manager.chooseGameName(taken);
        String gameRoom = getString();
        sendString(gameRoom);
    }

    public void chooseGameRoom(ArrayList<GameView> gameRooms) {
        manager.chooseGameRoom(gameRooms);
        int choice = getInteger();
        String room;
        switch (choice) {
            case 0:
                room = "/back";
                break;
            case 1:
                room = "/refresh";
                break;
            default:
                room = gameRooms.get(choice - 2).getName();
        }
        sendString(room);
    }

    public void chooseNickname(boolean taken) {
        manager.chooseNickname(taken);
        id = getString();
        manager.setId(id);
        sendString(null);
    }

    public void choosePlayersNumber() {
        manager.choosePlayersNumber();
        int num = getInteger();
        sendInteger(num);
    }

    public void choosePosition(ArrayList<CellView> positions, String desc) {
        manager.choosePosition(positions, desc);
        sendInteger(getInteger());
    }

    public void chooseStartingPlayer(ArrayList<PlayerView> players) {
        manager.chooseStartingPlayer(players);
        sendInteger(getInteger());
    }

    public void chooseStartJoin() {
        manager.chooseStartJoin();
        int num = getInteger();
        sendBoolean(num == 1);
    }

    public void chooseYesNo(String query) {
        manager.chooseYesNo(query);
        sendBoolean(getBoolean());

    }

    public void displayBuild(CellView buildPosition, CardView godCard) {
        manager.displayBuild(buildPosition, godCard);
        currentGame.setCell(buildPosition);
    }

    public void displayGameInfo(GameView game, String desc) {
        manager.displayGameInfo(game, desc);
        currentGame = game;
    }

    public void displayMessage(String message) {
        manager.displayMessage(message);
        System.out.println("\n" + message + "(NON STAMPARE)\n");
    }

    public void displayMove(HashMap<CellView, CellView> moves, CardView godCard) {
        manager.displayMove(moves, godCard);
        moves.forEach((startPosition, endPosition) -> {
            CellView newStart = new CellView(
                    startPosition.getPosX(), startPosition.getPosY(), startPosition.getBuildLevel(), startPosition.isDomed(), null
            );
            currentGame.setCell(newStart);
            CellView newEnd = new CellView(
                    endPosition.getPosX(), endPosition.getPosY(), endPosition.getBuildLevel(), endPosition.isDomed(), startPosition.getWorker()
            );
            currentGame.setCell(newEnd);
        });
    }

    public void displayPlaceWorker(CellView position) {
        manager.displayPlaceWorker(position);
        currentGame.setCell(position);
    }

    public void notifyDisconnection(PlayerView player) {
        manager.notifyDisconnection(player);
        System.out.println("\n" + player.getId() + " has disconnected. ");
    }

    public void notifyGameStarting() {
        manager.gameStarting();
        //System.out.println("\nGame is starting! Press ENTER to continue. ");
        //new Scanner(System.in).nextLine();
        sendBoolean(true);
    }

    public void notifyGameOver() {
        manager.notifyGameOver();
        System.out.println("\nGame over! \n\n\n\n\n");
    }

    public void notifyLoss(String reason, PlayerView winner) {
        manager.notifyLoss(reason, winner);
        //manager.setBusy(false);
        StringBuilder string = new StringBuilder();
        string.append("You lost! ");
        if (winner != null) {
            string.append(winner.getId() + " won!");
        } else {
            switch (reason) {
                case "outOfMoves":
                    string.append("(No legal moves available)\n");
                    break;
                case "outOfWorkers":
                    string.append("(All workers have been removed from the game)\n");
                    break;
                default:
                    break;
            }
        }
        System.out.println(string);
    }

    public void notifyWin(String reason) {
        manager.notifyWin(reason);
        //manager.setBusy(false);
        StringBuilder string = new StringBuilder();
        string.append("Congratulations! You won! ");
        switch (reason) {
            case "winConditionAchieved":
                string.append("(Win condition achieved)\n");
                break;
            case "outOfWorkers":
                string.append("(All other players were eliminated)\n");
                break;
            default:
                break;
        }
        System.out.println(string);
    }

}