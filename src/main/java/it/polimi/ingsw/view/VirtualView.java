package it.polimi.ingsw.view;

import it.polimi.ingsw.model.game_board.Board;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.network.message.to_client.ChoosePosition;
import it.polimi.ingsw.network.message.to_client.ChooseYesNo;
import it.polimi.ingsw.network.message.to_client.DisplayBoard;
import it.polimi.ingsw.network.message.to_client.DisplayMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class VirtualView {

    private String id;
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    /**
     * creates a VirtualView associated with the Interface received as an argument
     *
     * @param output the interface to associate this VirtualView to
     */
    public VirtualView(Socket socket, ObjectInputStream input, ObjectOutputStream output) {
        this.socket = socket;
        this.input = input;
        this.output = output;
    }

    /**
     *
     * @return the id associated with this VirtualView (ie the id of the Player associated with it)
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id the id to associate this VirtualView to
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
    public Worker chooseWorker(ArrayList<Worker> workers) throws IOException, ClassNotFoundException {
        ArrayList<CellView> positions = new ArrayList<CellView>();
        for (Worker worker : workers) {
            positions.add(new CellView(worker.getPosition()));
        }
        ChoosePosition msg = new ChoosePosition(positions, "worker");
        output.writeObject(msg);
        return (workers.get((int) input.readObject()));
    }

    /**
     * allows the player to choose a move for one of his workers
     *
     * @param possibleMoves an ArrayList containing all the possible moves a player can do with a worker
     * @return the cell the player decided to move his worker to
     */
    public Cell chooseMovePosition(ArrayList<Cell> possibleMoves) throws IOException, ClassNotFoundException {
        ArrayList<CellView> positions = new ArrayList<CellView>();
        for (Cell cell : possibleMoves) {
            positions.add(new CellView(cell));
        }
        ChoosePosition msg = new ChoosePosition(positions, "move");
        output.writeObject(msg);
        return (possibleMoves.get((int) input.readObject()));
    }

    /**
     * allows the player to choose a build for one of his workers
     *
     * @param possibleBuilds an ArrayList containing all the possible builds a player can do with a worker
     * @return the cell the player decided to build on
     */
    public Cell chooseBuildPosition(ArrayList<Cell> possibleBuilds) throws IOException, ClassNotFoundException {
        ArrayList<CellView> positions = new ArrayList<CellView>();
        for (Cell cell : possibleBuilds) {
            positions.add(new CellView(cell));
        }
        ChoosePosition msg = new ChoosePosition(positions, "build");
        output.writeObject(msg);
        return (possibleBuilds.get((int) input.readObject()));
    }

    public Cell chooseStartPosition(ArrayList<Cell> possiblePositions) throws IOException, ClassNotFoundException {
        ArrayList<CellView> positions = new ArrayList<CellView>();
        for (Cell cell : possiblePositions) {
            positions.add(new CellView(cell));
        }
        ChoosePosition msg = new ChoosePosition(positions, "start");
        output.writeObject(msg);
        return (possiblePositions.get((int) input.readObject()));
    }

    /**
     *
     * @param query the question the player should answer to
     * @return true if the player answered "yes", false if the player answered "no"
     */
    public boolean chooseYesNo(String query) throws IOException, ClassNotFoundException {
        ChooseYesNo msg = new ChooseYesNo(query);
        output.writeObject(msg);
        return ((Boolean) input.readObject());
    }

}
