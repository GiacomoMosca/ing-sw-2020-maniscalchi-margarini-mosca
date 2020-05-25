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
import java.util.Scanner;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;


public class GUI implements UI {//implements Runnable

    private final AtomicBoolean running;
    private final GUIManager manager;
    private final Object busyLock;
    private Socket server;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private SynchronousQueue<String> messageQueue;
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
        messageQueue = new SynchronousQueue<String>();
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

    private int getInt() {
        while (true) {
            try {
                return Integer.parseInt(messageQueue.take());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. ");
            } catch (InterruptedException e) {
                System.out.println("Error getting input. \n");
            }
        }
    }

    private String getString() {
        while (true) {
            try {
                return messageQueue.take();
            } catch (InterruptedException e) {
                System.out.println("Error getting input. \n");
            }
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

    public void chooseCards(ArrayList<CardView> possibleCards, int num, ArrayList<CardView> pickedCards) {
        manager.chooseCards(possibleCards, num, pickedCards);
        StringBuilder string = new StringBuilder();
        if (num > 1) string.append("\nChoose the " + num + " God Powers that will be used for this game: \n");
        else string.append("\nPick your God Power: \n");
        for (int i = 0; i < possibleCards.size(); i++) {
            string.append(i + ": ");
            string.append(possibleCards.get(i).getGod() + "\n");
        }
        if (pickedCards != null) for (CardView pickedCard : pickedCards) {
            string.append("X: ");
            string.append(pickedCard.getGod() + "\n");
        }
        System.out.println(string);
        ArrayList<Integer> choices = new ArrayList<Integer>();
        for (int i = 0; i < num; i++) {
            int choice = getInt();
            while (choice < 0 || choice >= possibleCards.size() || choices.contains(choice)) {
                System.out.println("Invalid input. ");
                choice = getInt();
            }
            System.out.println("Picked " + possibleCards.get(choice).getGod());
            choices.add(choice);
        }
        sendIntegers(choices);
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
        int choice = getInt();
        String room;
        switch (choice) {
            case 0:
                room = "/back";
                break;
            case 1:
                room = "/refresh";
                break;
            default:
                room = gameRooms.get(choice-2).getName();
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
        int num = getInt();
        while (num < 2 || num > 3) {
            System.out.println("Invalid input. ");
            num = getInt();
        }
        sendInteger(num);
    }

    public void choosePosition(ArrayList<CellView> positions, String desc) {
        /*StringBuilder string = new StringBuilder();
        string.append("\n");
        switch (desc) {
            case "start1":
                string.append("(Worker 1) Choose the starting position for your worker:");
                break;
            case "start2":
                string.append("(Worker 2) Choose the starting position for your worker:");
                break;
            case "worker":
                string.append("Choose a worker:");
                break;
            case "move":
                string.append("Choose a position to move to:");
                break;
            case "build":
                string.append("Choose a position to build in:");
                break;
        }
        string.append("\n");
        for (int i = 0; i < positions.size(); i++) {
            CellView cell = positions.get(i);
            if (i > 0) {
                string.append(", ");
                if (i % 5 == 0) string.append("\n");
            }
            string.append(i + ": ");
            string.append("[" + cell.getPosX() + ", " + cell.getPosY() + "]");
        }
        string.append("\n");
        System.out.println(string);
        int choice = getInt();
        while (choice < 0 || choice >= positions.size()) {
            System.out.println("Invalid input. ");
            choice = getInteger();
        }*/
        manager.choosePosition(positions, desc);
        sendInteger(getInteger());
    }

    public void chooseStartingPlayer(ArrayList<PlayerView> players) {
        /*StringBuilder string = new StringBuilder();
        string.append("\nChoose the starting player: \n");
        for (int i = 0; i < players.size(); i++) {
            string.append(i + ": ");
            string.append(players.get(i).getId() + "\n");
        }
        System.out.println(string);
        int choice = getInt();
        while (choice < 0 || choice >= players.size()) {
            System.out.println("Invalid input. ");
            choice = getInteger();
        }*/
        manager.chooseStartingPlayer(players);
        sendInteger(getInteger());
    }

    public void chooseStartJoin() {
        manager.chooseStartJoin();
        int num = getInt();
        while (num < 1 || num > 2) {
            System.out.println("Invalid input. ");
            num = getInt();
        }
        sendBoolean(num == 1);
    }

    /**
     * shows the question and waits until the player has answered ("y" for "yes", "n" for "no")
     *
     * @param query the question the player should answer to
     */
    public void chooseYesNo(String query) {
        /*boolean res;

        if (query.equals("Do you want to randomize the playable God Powers pool?")) {
            manager.chooseYesNo(query);
            res = getBoolean();
        } else {
            System.out.println("\n" + query + " (y/n) ");
            String choice = getString();
            while (!choice.equals("y") && !choice.equals("n")) {
                System.out.println("Invalid input. ");
                choice = getString();
            }
            res = choice.equals("y");
        }
        sendBoolean(res);*/
        manager.chooseYesNo(query);
        sendBoolean(getBoolean());
    }

    public void displayBuild(CellView buildPosition, CardView godCard) {
        manager.displayBuild(buildPosition, godCard);
        currentGame.setCell(buildPosition);
        displayBoard();
    }

    /**
     * shows the board of the current game, at its actual state:
     * " " if a cell is unoccupied
     * "(color)" if the cell is occupied by a worker of the specified color
     * "X" if the cell has a Dome
     * "1" if the building level of the cell is 1
     * "2" if the building level of the cell is 2
     * "3" if the building level of the cell is 3
     *
     * @param game the board associated with the current game
     */
    public void displayGameInfo(GameView game, String desc) {
        manager.displayGameInfo(game, desc);
        currentGame = game;
        // displayBoard();
    }

    /**
     * shows to display the message received as an argument
     *
     * @param message the message to show
     */
    public void displayMessage(String message) {
        manager.displayMessage(message);
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
        //displayBoard();
    }

    public void displayPlaceWorker(CellView position) {
        manager.displayPlaceWorker(position);
        currentGame.setCell(position);
        //displayBoard();
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
        /* StringBuilder string = new StringBuilder();
        string.append("\n    0  1  2  3  4 ");
        string.append("\n");
        for (int i = 0; i < 5; i++) {
            string.append("  ----------------");
            string.append("\n");
            string.append(i + " ");
            for (int j = 0; j < 5; j++) {
                CellView cell = currentGame.getCell(i, j);
                string.append("|");
                if (cell.isDomed()) string.append("X");
                else string.append(cell.getBuildLevel() == 0 ? " " : cell.getBuildLevel());
                if (cell.hasWorker()) string.append(cell.getWorker().getColor());
                else string.append(" ");
            }
            string.append("|");
            string.append("\n");
        }
        string.append("  ----------------");
        string.append("\n");
        System.out.println(string); */
    }

}