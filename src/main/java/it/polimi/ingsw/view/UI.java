package it.polimi.ingsw.view;

import it.polimi.ingsw.model.game_board.Board;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Worker;

import java.util.ArrayList;

public interface UI {

    public void displayBoard(Board board);

    public void displayMessage(String message);

    public Worker chooseWorker(ArrayList<Worker> workers);

    public Cell chooseMovePosition(ArrayList<Cell> possibleMoves);

    public Cell chooseBuildPosition(ArrayList<Cell> possibleBuilds);

    public boolean chooseYesNo(String query);

}
