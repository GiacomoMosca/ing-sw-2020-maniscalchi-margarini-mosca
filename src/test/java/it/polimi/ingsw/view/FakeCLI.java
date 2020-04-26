package it.polimi.ingsw.view;

import it.polimi.ingsw.model.game_board.Board;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Worker;

import java.util.ArrayList;

public class FakeCLI implements UI {

    public void displayBoard(Board board) {}

    public void displayMessage(String message) {}

    public Worker chooseWorker(ArrayList<Worker> workers) {
        return workers.get(chooseInt(workers.size(), null));
    }

    public Cell chooseMovePosition(ArrayList<Cell> possibleMoves) {
        return possibleMoves.get(chooseInt(possibleMoves.size(), null));
    }

    public Cell chooseBuildPosition(ArrayList<Cell> possibleBuilds) {
        return possibleBuilds.get(chooseInt(possibleBuilds.size(), null));
    }

    public boolean chooseYesNo(String query) {
        return true;
        //return false;
    }

    public int chooseInt(int arraySize, String message) {
        return 0;
    }

}