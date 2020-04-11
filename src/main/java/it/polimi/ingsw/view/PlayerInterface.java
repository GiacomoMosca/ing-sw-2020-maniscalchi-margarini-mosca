package it.polimi.ingsw.view;

import it.polimi.ingsw.model.game_board.Board;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Worker;

import java.util.ArrayList;

public class PlayerInterface {

    private final UI client;
    private String id;

    /**
     * creates a PlayerInterface associated with the Interface received as an argument
     *
     * @param client the interface to associate this PlayerInterface to
     */
    public PlayerInterface(UI client) {
        this.client = client;
    }

    /**
     *
     * @return the id associated with this PlayerInterface (ie the id of the Player associated with it)
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id the id to associate this PlayerInterface to
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * shows the Board of the current Game
     *
     * @param board the Board associated with the current Game
     */
    public void displayBoard(Board board) {
        client.displayBoard(board);
    }

    /**
     * shows the message received as an argument
     *
     * @param message
     */
    public void displayMessage(String message) {
        client.displayMessage(message);
    }

    /**
     * allows the player to choose a worker for his current turn
     *
     * @param workers the workers the player can choose for his turn
     * @return the worker the player chose
     */
    public Worker chooseWorker(ArrayList<Worker> workers) {
        return client.chooseWorker(workers);
    }

    /**
     * allows the player to choose a move for one of his workers
     *
     * @param possibleMoves an ArrayList containing all the possible moves a player can do with a worker
     * @return the cell the player decided to move his worker to
     */
    public Cell chooseMovePosition(ArrayList<Cell> possibleMoves) {
        return client.chooseMovePosition(possibleMoves);
    }

    /**
     * allows the player to choose a build for one of his workers
     *
     * @param possibleBuilds an ArrayList containing all the possible builds a player can do with a worker
     * @return the cell the player decided to build on
     */
    public Cell chooseBuildPosition(ArrayList<Cell> possibleBuilds) {
        return client.chooseBuildPosition(possibleBuilds);
    }

    /**
     *
     * @param query the question the player should answer to
     * @return true if the player answered "yes", false if the player answered "no"
     */
    public boolean chooseYesNo(String query) {
        return client.chooseYesNo(query);
    }

    /**
     * allows the player to choose among many possibilities
     *
     * @param arraySize the size of the array
     * @param message the message describing the choice to make
     * @return the number indicating the choice of the player
     */
    public int chooseInt(int arraySize, String message) {
        return client.chooseInt(arraySize, message);
    }

}
