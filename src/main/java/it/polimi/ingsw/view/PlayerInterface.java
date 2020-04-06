package it.polimi.ingsw.view;

import it.polimi.ingsw.model.game_board.Board;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Worker;

import java.util.ArrayList;

public class PlayerInterface {

    private UI client;

    public PlayerInterface(UI client) {
        this.client = client;
    }

    public void displayBoard(Board board) {
        client.displayBoard(board);
    }

    public void displayMessage(String message) {
        client.displayMessage(message);
    }

    public Worker chooseWorker(ArrayList<Worker> workers) {
        return client.chooseWorker(workers);
    }

    public Cell chooseMovePosition(ArrayList<Cell> possibleMoves) {
        return client.chooseMovePosition(possibleMoves);
    }

    public Cell chooseBuildPosition(ArrayList<Cell> possibleBuilds) {
        return client.chooseBuildPosition(possibleBuilds);
    }

    public boolean chooseYesNo(String query) {
        return client.chooseYesNo(query);
    }

}
