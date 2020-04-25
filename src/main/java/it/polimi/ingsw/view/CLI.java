package it.polimi.ingsw.view;

import it.polimi.ingsw.model.game_board.Board;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.server.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Scanner;

public class CLI implements UI {

    private Scanner scanner;
    private Socket server;

    public void parseMessage(Message message)
    {

    }

    public void start(){
        scanner = new Scanner(System.in);
        String ip = getServerIp();
        try {
            server = new Socket(ip,7777);
        } catch (IOException e) {
            System.out.println("server unreachable");
            return;
        }
        System.out.println("Connected");

        try {
            ObjectOutputStream output = new ObjectOutputStream(server.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(server.getInputStream());
        } catch (IOException e) {
            System.out.println("server has died");
        } catch (ClassCastException e) {
            System.out.println("protocol violation");
        }
    }

    public String getServerIp() {
        System.out.println("IP address of server?");
        String ip = scanner.nextLine();
        return ip;
    }

    public void stop(){
        try {
            server.close();
        } catch (IOException e) { }
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
    public void displayBoard(Board board) {
        StringBuilder output = new StringBuilder();
        output.append("    0  1  2  3  4 ");
        output.append("\n");
        for (int i = 0; i < 5; i++) {
            output.append("  ----------------");
            output.append("\n");
            output.append(i + " ");
            for (int j = 0; j < 5; j++) {
                Cell cell = board.getCell(i, j);
                output.append("|");
                if (cell.isDomed()) output.append("X");
                else output.append(cell.getBuildLevel() == 0 ? " " : cell.getBuildLevel());
                if (cell.hasWorker()) output.append(cell.getWorker().getOwner().getId());
                else output.append(" ");
            }
            output.append("|");
            output.append("\n");
        }
        output.append("  ----------------");
        output.append("\n");
        System.out.println(output);
    }

    /**
     * shows to display the message received as an argument
     *
     * @param message the message to show
     */
    public void displayMessage(String message) {
        System.out.println(message + "\n");
    }

    /**
     * allows the player to choose a worker for his current turn
     *
     * @param workers an ArrayList containing the available workers
     * @return the worker the player chose
     */
    public Worker chooseWorker(ArrayList<Worker> workers) {
        StringBuilder output = new StringBuilder();
        output.append("Choose a worker:");
        output.append("\n");
        for (int i = 0; i < workers.size(); i++) {
            Worker worker = workers.get(i);
            if (i > 0) output.append(", ");
            output.append(i + ": ");
            output.append("[" + worker.getPosition().getPosY() + ", " + worker.getPosition().getPosX() + "]");
        }
        output.append("\n");
        output.append("\n");
        System.out.println(output);
        return workers.get(chooseInt(workers.size(), null));
    }

    /**
     * allows the player to choose a move for one of his workers
     *
     * @param possibleMoves an ArrayList containing all the possible moves a player can do with a worker
     * @return the cell the player decided to move his worker to
     */
    public Cell chooseMovePosition(ArrayList<Cell> possibleMoves) {
        StringBuilder output = new StringBuilder();
        output.append("Choose a position to move to:");
        output.append("\n");
        for (int i = 0; i < possibleMoves.size(); i++) {
            Cell cell = possibleMoves.get(i);
            if (i > 0) output.append(", ");
            output.append(i + ": ");
            output.append("[" + cell.getPosY() + ", " + cell.getPosX() + "]");
        }
        output.append("\n");
        System.out.println(output);
        return possibleMoves.get(chooseInt(possibleMoves.size(), null));
    }

    /**
     * allows the player to choose a build for one of his workers
     *
     * @param possibleBuilds an ArrayList containing all the possible builds a player can do with a worker
     * @return the cell the player decided to build on
     */
    public Cell chooseBuildPosition(ArrayList<Cell> possibleBuilds) {
        StringBuilder output = new StringBuilder();
        output.append("Choose a cell to build in:");
        output.append("\n");
        for (int i = 0; i < possibleBuilds.size(); i++) {
            Cell cell = possibleBuilds.get(i);
            if (i > 0) output.append(", ");
            output.append(i + ": ");
            output.append("[" + cell.getPosY() + ", " + cell.getPosX() + "]");
        }
        output.append("\n");
        System.out.println(output);
        return possibleBuilds.get(chooseInt(possibleBuilds.size(), null));
    }

    /**
     * shows the question and waits until the player has answered ("y" for "yes", "n" for "no")
     *
     * @param query the question the player should answer to
     * @return true if the player answered "yes", false if the player answered "no"
     */
    public boolean chooseYesNo(String query) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(query + "(y/n) \n");
        String input = scanner.nextLine();
        while (!input.equals("y") && !input.equals("n")) {
            System.out.println("Invalid input. \n");
            input = scanner.nextLine();
        }
        if (input.equals("y")) return true;
        return false;
    }

    /**
     * displays the message and waits until the player has communicated his choice
     *
     * @param arraySize the size of the array, ie the number of possible choices
     * @param message the message to eventually show
     * @return the number indicating the choice
     */
    public int chooseInt(int arraySize, String message) {
        if (message != null) System.out.println(message + "\n");
        Scanner scanner = new Scanner(System.in);
        int input = scanner.nextInt();
        while (input < 0 || input >= arraySize) {
            System.out.println("Invalid input. \n");
            input = scanner.nextInt();
        }
        return input;
    }

}
