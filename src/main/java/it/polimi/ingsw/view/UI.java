package it.polimi.ingsw.view;

import it.polimi.ingsw.model.game_board.Board;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.network.message.Message;

import java.util.ArrayList;

public interface UI {

    public void start();

    public void stop();

    public void parseMessage(Message message);

    public String getServerIp();

    /**
     * shows the game board
     *
     * @param board the Board of the current game
     */
    public void displayBoard(Board board);

    /**
     * shows a message
     *
     * @param message the message to show
     */
    public void displayMessage(String message);

    /**
     * allows the player to choose a worker for his current turn
     *
     * @param workers the workers the player can choose for his turn
     * @return the worker the player chose
     */
    public Worker chooseWorker(ArrayList<Worker> workers);

    /**
     * allows the player to choose a move for one of his workers
     *
     * @param possibleMoves an ArrayList containing all the possible moves a player can do with a worker
     * @return the cell the player decided to move his worker to
     */
    public Cell chooseMovePosition(ArrayList<Cell> possibleMoves);

    /**
     * allows the player to choose a build for one of his workers
     *
     * @param possibleBuilds an ArrayList containing all the possible builds a player can do with a worker
     * @return the cell the player decided to build on
     */
    public Cell chooseBuildPosition(ArrayList<Cell> possibleBuilds);

    /**
     *
     * @param query the question the player should answer to
     * @return true if the player answered "yes", false if the player answered "no"
     */
    public boolean chooseYesNo(String query);

    /**
     * allows the player to choose among many possibilities
     *
     * @param arraySize the size of the array
     * @param message the message describing the choice to make
     * @return the number indicating the choice of the player
     */
    public int chooseInt(int arraySize, String message);

}
