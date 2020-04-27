package it.polimi.ingsw.view;

import it.polimi.ingsw.model.game_board.Board;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.network.message.to_client.*;
import it.polimi.ingsw.network.message.to_server.ToServerMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class VirtualView {

    private String id;
    private final Socket socket;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;

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
     * @return the id associated with this VirtualView (ie the id of the Player associated with it)
     */
    public String getId() {
        return id;
    }

    public String chooseNickname(ArrayList<String> playerList) throws IOException, ClassNotFoundException {
        output.writeObject(new ChooseNickname(playerList));
        id = ((ToServerMessage) input.readObject()).getSender();
        return id;
    }

    public int choosePlayersNumber() throws IOException, ClassNotFoundException {
        output.writeObject(new ChoosePlayersNumber(null));
        return (int) ((ToServerMessage) input.readObject()).getBody();
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
        return workers.get((int) ((ToServerMessage) input.readObject()).getBody());
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
        return possibleMoves.get((int) ((ToServerMessage) input.readObject()).getBody());
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
        return possibleBuilds.get((int) ((ToServerMessage) input.readObject()).getBody());
    }

    public Cell chooseStartPosition(ArrayList<Cell> possiblePositions) throws IOException, ClassNotFoundException {
        ArrayList<CellView> positions = new ArrayList<CellView>();
        for (Cell cell : possiblePositions) {
            positions.add(new CellView(cell));
        }
        ChoosePosition msg = new ChoosePosition(positions, "start");
        output.writeObject(msg);
        return possiblePositions.get((int) ((ToServerMessage) input.readObject()).getBody());
    }

    /**
     * @param query the question the player should answer to
     * @return true if the player answered "yes", false if the player answered "no"
     */
    public boolean chooseYesNo(String query) throws IOException, ClassNotFoundException {
        ChooseYesNo msg = new ChooseYesNo(query);
        output.writeObject(msg);
        return (boolean) ((ToServerMessage) input.readObject()).getBody();
    }

    public void notifyLoss(Player player, String reason) throws IOException {
        PlayerView playerView = new PlayerView(player);
        NotifyLoss msg = new NotifyLoss(playerView, reason);
        output.writeObject(msg);
    }

    public void notifyWin(Player player) throws IOException {
        PlayerView playerView = new PlayerView(player);
        NotifyWin msg = new NotifyWin(playerView);
        output.writeObject(msg);
    }

}
