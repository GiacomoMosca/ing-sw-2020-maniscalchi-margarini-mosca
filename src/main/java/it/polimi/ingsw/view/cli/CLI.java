package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.network.message.to_client.ToClientMessage;
import it.polimi.ingsw.network.message.to_server.ToServerMessage;
import it.polimi.ingsw.view.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.SynchronousQueue;

public class CLI implements UI {

    private Socket server;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private SynchronousQueue messageQueue;
    private String id;
    private boolean running;

    public CLI() {
        this.id = null;
    }

    public void run() {
        running = true;
        messageQueue = new SynchronousQueue();
        new Thread(this::inputListener).start();

        String ip = getServerIp();
        server = new Socket();
        try {
            server.connect(new InetSocketAddress(ip, 8000), 5 * 1000);
        } catch (IOException e) {
            System.out.println("Server unreachable. ");
            stop();
            return;
        }
        System.out.println("Connected! ");

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

        ToClientMessage message = null;
        while (running) {
            try {
                message = (ToClientMessage) input.readObject();
            } catch (IOException e) {
                System.out.println("Disconnected. ");
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }
            if (message != null) {
                parseMessage(message);
            }
        }
        stop();
    }

    private void inputListener() {
        Scanner scanner = new Scanner(System.in);
        while (running) {
            String input = scanner.nextLine();
            switch (input) {
                case "/quit":
                    quit();
                    break;
            }
            messageQueue.offer(input);
        }
    }

    public synchronized void stop() {
        if (!running) return;
        running = false;
        System.out.println("\nPress ENTER to quit. ");
        try {
            if (server != null) server.close();
            if (input != null) input.close();
            if (output != null) output.close();
        } catch (IOException e) {
            //
        }
    }

    public void parseMessage(ToClientMessage message) {
        message.performAction(this);
    }

    public String getServerIp() {
        System.out.println("\nServer IP address: ");
        String ip = getString();
        return ip;
    }

    public void chooseNickname() {
        System.out.println("\nChoose your nickname: ");
        String nickname = getString();
        id = nickname;
        try {
            output.writeObject(new ToServerMessage(null, id));
        } catch (IOException e) {
            System.out.println("Disconnected. ");
            stop();
        }
    }

    public void choosePlayersNumber() {
        System.out.println("\nSetting up a new game! Choose the number of players (2 or 3):");
        int num = getInt();
        while (num < 2 || num > 3) {
            System.out.println("Invalid input. ");
            num = getInt();
        }
        try {
            output.writeObject(new ToServerMessage(num, id));
        } catch (IOException e) {
            System.out.println("Disconnected. ");
            stop();
        }
    }

    public void chooseCards(ArrayList<CardView> possibleCards, int num, ArrayList<CardView> pickedCards) {
        StringBuilder string = new StringBuilder();
        if (num > 1) string.append("\nChoose the " + num + " God Powers that will be used for this game: \n");
        else string.append("\nPick your God Power: \n");
        for (int i = 0; i < possibleCards.size(); i++) {
            string.append(i + ": ");
            string.append(possibleCards.get(i).getGod() + "\n");
        }
        if (pickedCards != null) for (int i = 0; i < pickedCards.size(); i++) {
            string.append("X: ");
            string.append(pickedCards.get(i).getGod() + "\n");
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
        try {
            output.writeObject(new ToServerMessage(choices, id));
        } catch (IOException e) {
            System.out.println("Disconnected. ");
            stop();
        }
    }

    public void chooseStartingPlayer(ArrayList<PlayerView> players) {
        StringBuilder string = new StringBuilder();
        string.append("\nChoose the starting player: \n");
        for (int i = 0; i < players.size(); i++) {
            string.append(i + ": ");
            string.append(players.get(i).getId() + "\n");
        }
        System.out.println(string);
        int choice = getInt();
        while (choice < 0 || choice >= players.size()) {
            System.out.println("Invalid input. ");
            choice = getInt();
        }
        try {
            output.writeObject(new ToServerMessage(choice, id));
        } catch (IOException e) {
            System.out.println("Disconnected. ");
            stop();
        }
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
     * @param board the board associated with the current game
     */
    public void displayBoard(GameView board, String desc, CardView godPower) {
        StringBuilder string = new StringBuilder();
        string.append("\n    0  1  2  3  4 ");
        string.append("\n");
        for (int i = 0; i < 5; i++) {
            string.append("  ----------------");
            string.append("\n");
            string.append(i + " ");
            for (int j = 0; j < 5; j++) {
                CellView cell = board.getCell(i, j);
                string.append("|");
                if (cell.isDomed()) string.append("X");
                else string.append(cell.getBuildLevel() == 0 ? " " : cell.getBuildLevel());
                if (cell.hasWorker()) string.append(cell.getWorkerColor());
                else string.append(" ");
            }
            string.append("|");
            string.append("\n");
        }
        string.append("  ----------------");
        string.append("\n");
        System.out.println(string);
    }

    /**
     * shows to display the message received as an argument
     *
     * @param message the message to show
     */
    public void displayMessage(String message) {
        System.out.println("\n" + message);
    }

    public void choosePosition(ArrayList<CellView> positions, String desc) {
        StringBuilder string = new StringBuilder();
        string.append("\n");
        switch (desc) {
            case "start":
                string.append("Choose the starting position for your worker:");
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
            choice = getInt();
        }
        try {
            output.writeObject(new ToServerMessage(choice, id));
        } catch (IOException e) {
            System.out.println("Disconnected. ");
            stop();
        }
    }

    /**
     * shows the question and waits until the player has answered ("y" for "yes", "n" for "no")
     *
     * @param query the question the player should answer to
     * @return true if the player answered "yes", false if the player answered "no"
     */
    public void chooseYesNo(String query) {
        System.out.println("\n" + query + " (y/n) ");
        String choice = getString();
        while (!choice.equals("y") && !choice.equals("n")) {
            System.out.println("Invalid input. ");
            choice = getString();
        }
        boolean res = choice.equals("y");
        try {
            output.writeObject(new ToServerMessage(res, id));
        } catch (IOException e) {
            System.out.println("Disconnected. ");
            stop();
        }
    }

    public void notifyLoss(PlayerView player, String reason) {
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

    public void gameOver() {
        System.out.println("\n\nGame over! Thanks for playing! ");
        stop();
    }

    private void quit() {
        gameOver();
    }

    private int getInt() {
        while (true) {
            try {
                return Integer.parseInt((String) messageQueue.take());
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
                return (String) messageQueue.take();
            } catch (InterruptedException e) {
                System.out.println("Error getting input. \n");
            }
        }
    }

    //Multiple Games
    public void chooseGameRoom(ArrayList<String> gameRooms) {
        System.out.println("\nRoom List:");
        for (int i=1; i<=gameRooms.size(); i++)
            System.out.println(i + ". " + gameRooms.get(i-1) + "\n");
        System.out.println("0. Refresh , 1. Choose room");
        int choice = getInt();
        while((choice!=0)&&(choice!=1)) {
            System.out.println("Invalid input\n");
            choice = getInt();
        }
        if (choice == 0)
            try {
                output.writeObject(new ToServerMessage(0, id));
            } catch (IOException e) {
                System.out.println("Disconnected. ");
                stop();
            }
        else {
            System.out.println("\nWhich room do you want to join?: ");
            int gameRoom = getInt();
            while((gameRoom<1)||(gameRoom>gameRooms.size())) {
                System.out.println("Invalid input\n");
                gameRoom = getInt();
            }
            try {
                output.writeObject(new ToServerMessage(gameRoom, id));
            } catch (IOException e) {
                System.out.println("Disconnected. ");
                stop();
            }
        }
    }

    public void chooseGameName() {
        System.out.println("\nChoose a Game Room name: ");
        String gameRoom = getString();
        try {
            output.writeObject(new ToServerMessage(gameRoom, id));
        } catch (IOException e) {
            System.out.println("Disconnected. ");
            stop();
        }
    }

    public void chooseInt (String query, int max){
        System.out.println(query + "\n");
        int choice = getInt();
        while((choice<1)||(choice>max)) {
            System.out.println("Invalid input. \n");
            choice = getInt();
        }
        try {
            output.writeObject(new ToServerMessage(choice, id));
        } catch (IOException e) {
            System.out.println("Disconnected. ");
            stop();
        }
    }

}
