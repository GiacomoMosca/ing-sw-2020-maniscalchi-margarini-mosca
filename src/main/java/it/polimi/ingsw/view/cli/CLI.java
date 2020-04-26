package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.network.message.to_client.ToClientMessage;
import it.polimi.ingsw.view.BoardView;
import it.polimi.ingsw.view.CellView;
import it.polimi.ingsw.view.UI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class CLI implements UI {

    private Scanner scanner;
    private Socket server;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    public void start(){
        scanner = new Scanner(System.in);
        String ip = /*getServerIp()*/ "127.0.0.1";
        try {
            server = new Socket(ip,8000);
        } catch (IOException e) {
            System.out.println("server unreachable");
            return;
        }
        System.out.println("Connected");

        try {
            output = new ObjectOutputStream(server.getOutputStream());
            input = new ObjectInputStream(server.getInputStream());
        } catch (IOException e) {
            System.out.println("server has died");
        } catch (ClassCastException e) {
            System.out.println("protocol violation");
        }

        ToClientMessage message = null;
        while (true) {
            try {
                message = (ToClientMessage) input.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (message != null) {
                parseMessage(message);
            }
        }

        //stop();
    }

    public void stop(){
        try {
            server.close();
            input.close();
            output.close();
        } catch (IOException e) {}
    }

    public void parseMessage(ToClientMessage message)
    {
        message.performAction(this);
    }

    public String getServerIp() {
        System.out.println("IP address of server?");
        String ip = scanner.nextLine();
        return ip;
    }

    /**
     * shows the board of the current game, at his actual state:
     * " " if a cell is unoccupied
     * "a" if the cell is occupied by a worker of the first player
     * "b" if the cell is occupied by a worker of the second player
     * "c" if there is a third player and a cell is occupied by a worker of his
     * "X" if the cell has a Dome
     * "1" if the building level of the cell is 1
     * "2" if the building level of the cell is 2
     * "3" if the building level of the cell is 3
     *
     * @param board the board associated with the current game
     */
    public void displayBoard(BoardView board) {
        StringBuilder string = new StringBuilder();
        string.append("    0  1  2  3  4 ");
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
        System.out.println(message + "\n");
    }

    public void choosePosition(ArrayList<CellView> positions, String desc) {
        StringBuilder string = new StringBuilder();
        switch(desc) {
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
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        while (choice < 0 || choice >= positions.size()) {
            System.out.println("Invalid input. \n");
            choice = scanner.nextInt();
        }
        try {
            output.flush();
            output.writeInt(choice);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * shows the question and waits until the player has answered ("y" for "yes", "n" for "no")
     *
     * @param query the question the player should answer to
     * @return true if the player answered "yes", false if the player answered "no"
     */
    public void chooseYesNo(String query) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(query + "(y/n) \n");
        String choice = scanner.nextLine();
        while (!choice.equals("y") && !choice.equals("n")) {
            System.out.println("Invalid input. \n");
            choice = scanner.nextLine();
        }
        boolean res = false;
        if (choice.equals("y")) res = true;
        try {
            output.writeBoolean(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
