package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.network.message.to_client.Ping;
import it.polimi.ingsw.network.message.to_client.ToClientMessage;
import it.polimi.ingsw.network.message.to_server.SendBoolean;
import it.polimi.ingsw.network.message.to_server.SendInteger;
import it.polimi.ingsw.network.message.to_server.SendIntegers;
import it.polimi.ingsw.network.message.to_server.SendString;
import it.polimi.ingsw.view.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;


public class GUI implements UI {//implements Runnable

    private final AtomicBoolean running;
    private final GUIManager manager;
    private final Object busyLock;
    private Socket server;
    private ObjectInputStream input;
    private ObjectOutputStream output;
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
        running.set(true);
        messageQueue = new SynchronousQueue<Object>();
        currentGame = null;

        manager.setGui(this);
        manager.setQueue(messageQueue);
        new Thread(manager::run).start();

        String ip = getServerIp();
        server = new Socket();
        try {
            server.connect(new InetSocketAddress(ip, 8000), 5 * 1000);
        } catch (IOException e) {
            System.out.println("Server unreachable. ");
            stop();
            return;
        }

        try {
            output = new ObjectOutputStream(server.getOutputStream());
            input = new ObjectInputStream(server.getInputStream());
        } catch (IOException e) {
            System.out.println("Server is down. ");
            stop();
            return;
        } catch (ClassCastException e) {
            System.out.println("Protocol violation. ");
            stop();
            return;
        }

        ToClientMessage message;
        while (running.get()) {
            try {
                message = (ToClientMessage) input.readObject();
            } catch (IOException e) {
                System.out.println("Disconnected. ");
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }
            if (message == null) continue;
            if (message instanceof Ping) continue;
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

    private void quit() {
        stop();
    }

    public synchronized void stop() {
        if (!running.compareAndSet(true, false)) return;
        System.out.println("\nPress ENTER to quit. ");
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
        if (taken) System.out.println("\nName already taken. ");
        String gameRoom;
        while (true) {
            gameRoom = getString();
            if (gameRoom.length() > 12) System.out.println("Invalid input (max 12 characters). ");
            else break;
        }
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
        if (taken) System.out.println("\nNickname already taken. ");
        while (true) {
            id = getString();
            if (id.length() > 12) System.out.println("Invalid input (max 12 characters). ");
            else break;
        }
        sendString(null);
    }

    public void choosePlayersNumber() {
        manager.choosePlayersNumber();
        int num = getInteger();
        while (num < 2 || num > 3) {
            System.out.println("Invalid input. ");
            num = getInteger();
        }
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
        while (num < 1 || num > 2) {
            System.out.println("Invalid input. ");
            num = getInteger();
        }
        sendBoolean(num == 1);
    }

    public void chooseYesNo(String query) {
        manager.chooseYesNo(query);
        sendBoolean(getBoolean());

    }

    public void displayBuild(CellView buildPosition, CardView godCard) {
        manager.displayBuild(buildPosition, godCard);
        currentGame.setCell(buildPosition);
        displayBoard();
    }

    public void displayGameInfo(GameView game, String desc) {
        manager.displayGameInfo(game, desc);
        currentGame = game;
    }

    public void displayMessage(String message) {
        manager.displayMessage(message);
        System.out.println("\n" + message);
    }

    public void displayMove(HashMap<CellView, CellView> moves, CardView godCard) {
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
        manager.displayMove(moves, godCard);
    }

    public void displayPlaceWorker(CellView position) {
        manager.displayPlaceWorker(position);
        currentGame.setCell(position);
    }

    public void notifyDisconnection(PlayerView player) {
        manager.notifyDisconnection(player);
        System.out.println("\n" + player.getId() + " has disconnected. ");
    }

    public void notifyGameOver() {
        manager.notifyGameOver();
        System.out.println("\nGame over! \n\n\n\n\n");
    }

    public void notifyLoss(PlayerView player, String reason) {
        manager.notifyLoss(player, reason);
        StringBuilder string = new StringBuilder();
        if (player.getId().equals(id)) {
            string.append("You lost! ");
        } else {
            string.append(player.getId() + " lost! ");
        }
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
        System.out.println(string);
    }

    public void notifyWin(PlayerView player, String reason) {
        manager.notifyWin(player, reason);
        StringBuilder string = new StringBuilder();
        if (player.getId().equals(id)) {
            string.append("Congratulations! You won! ");
        } else {
            string.append(player.getId() + " won! ");
        }
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

    private void displayBoard() {
        //
    }

}