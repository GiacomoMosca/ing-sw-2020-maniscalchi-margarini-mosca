package it.polimi.ingsw.view;

import it.polimi.ingsw.model.game_board.Board;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Worker;

import java.util.ArrayList;
import java.util.Scanner;

public class CLI implements UI {

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

    public void displayMessage(String message) {
        System.out.println(message + "\n");
    }

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
