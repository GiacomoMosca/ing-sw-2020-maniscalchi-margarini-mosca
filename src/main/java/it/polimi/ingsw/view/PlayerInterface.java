package it.polimi.ingsw.view;

import it.polimi.ingsw.model.game_board.Board;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.network.message.ChoosePosition;
import it.polimi.ingsw.network.message.ChooseYesNo;
import it.polimi.ingsw.network.message.DisplayBoard;
import it.polimi.ingsw.network.message.DisplayMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class PlayerInterface {

    private String id;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    /**
     * creates a PlayerInterface associated with the Interface received as an argument
     *
     * @param output the interface to associate this PlayerInterface to
     */
    public PlayerInterface(ObjectInputStream input, ObjectOutputStream output) {
        this.input = input;
        this.output = output;
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
    public void displayBoard(ArrayList<Player> players, Board board) throws IOException {
        BoardView boardView = new BoardView(players, board);
        DisplayBoard msg = new DisplayBoard(boardView);
        output.writeObject(msg);
    }

    /**
     * shows the message received as an argument
     *
     * @param message
     */
    public void displayMessage(String message) throws IOException {
        DisplayMessage msg = new DisplayMessage(message);
        output.writeObject(msg);
    }

    /**
     * allows the player to choose a worker for his current turn
     *
     * @param workers the workers the player can choose for his turn
     * @return the worker the player chose
     */
    public Worker chooseWorker(ArrayList<Worker> workers) throws IOException {
        ArrayList<CellView> positions = new ArrayList<CellView>();
        for (Worker worker : workers) {
            positions.add(new CellView(worker.getPosition()));
        }
        ChoosePosition msg = new ChoosePosition(positions, "worker");
        output.writeObject(msg);
        return (workers.get(input.readInt());
    }

    /**
     * allows the player to choose a move for one of his workers
     *
     * @param possibleMoves an ArrayList containing all the possible moves a player can do with a worker
     * @return the cell the player decided to move his worker to
     */
    public Cell chooseMovePosition(ArrayList<Cell> possibleMoves) throws IOException {
        ArrayList<CellView> positions = new ArrayList<CellView>();
        for (Cell cell : possibleMoves) {
            positions.add(new CellView(cell));
        }
        ChoosePosition msg = new ChoosePosition(positions, "move");
        output.writeObject(msg);
        return (possibleMoves.get(input.readInt());
    }

    /**
     * allows the player to choose a build for one of his workers
     *
     * @param possibleBuilds an ArrayList containing all the possible builds a player can do with a worker
     * @return the cell the player decided to build on
     */
    public Cell chooseBuildPosition(ArrayList<Cell> possibleBuilds) throws IOException {
        ArrayList<CellView> positions = new ArrayList<CellView>();
        for (Cell cell : possibleBuilds) {
            positions.add(new CellView(cell));
        }
        ChoosePosition msg = new ChoosePosition(positions, "build");
        output.writeObject(msg);
        return (possibleBuilds.get(input.readInt());
    }

    /**
     *
     * @param query the question the player should answer to
     * @return true if the player answered "yes", false if the player answered "no"
     */
    public boolean chooseYesNo(String query) throws IOException {
        ChooseYesNo msg = new ChooseYesNo(query);
        output.writeObject(msg);
        return (input.readChar() == 'y');
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
