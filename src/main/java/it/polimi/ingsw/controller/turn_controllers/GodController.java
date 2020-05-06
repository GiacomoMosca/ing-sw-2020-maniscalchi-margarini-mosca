package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Board;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.view.VirtualView;

import java.io.IOException;
import java.util.ArrayList;

public abstract class GodController {

    protected final GameController gameController;
    protected final Game game;
    protected final Board board;
    protected Card card;
    public Player player;
    protected VirtualView client;
    public Worker activeWorker;
    protected Cell startingPosition;

    /**
     * creates a God Controller for this game
     *
     * @param gameController
     */
    public GodController(GameController gameController) {
        this.gameController = gameController;
        this.game = gameController.getGame();
        this.board = this.game.getBoard();
    }


    /**
     * sets all the attributes of the card to their correct values
     *
     * @return a complete Card
     */
    public abstract Card generateCard(); /* {
        Card card = new Card(
                "god",
                "title",
                "description",
                set (1: Simple, 2: Advanced),
                alwaysActiveModifier,
                this
        );
        this.card = card;
        return card;
    } */

    /**
     * @return the client associated with this GodController
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the client associated with the player
     */
    public VirtualView getClient() {
        return client;
    }

    /**
     * sets the attributes player and client to the values passed as arguments
     *
     * @param player the player
     * @param client the client associated with this player
     */
    public void setPlayer(Player player, VirtualView client) {
        this.player = player;
        this.client = client;
    }

    /**
     * checks if the worker can move
     *
     * @param worker
     * @return true if the worker can move, false otherwise
     */
    public boolean canPlay(Worker worker) {
        return findPossibleMoves(worker.getPosition()).size() > 0;
    }

    /**
     * handles the basic phases of a turn: moving and building
     *
     * @return "WON" if the player won, "NEXT" if the game goes on
     */
    public String runPhases(Worker worker) throws IOException, ClassNotFoundException {
        activeWorker = worker;
        startingPosition = worker.getPosition();
        movePhase();
        if (checkWin()) return "WON";
        buildPhase();
        return "NEXT";
    }

    /**
     * handles the moving phase of the turn
     */
    public void movePhase() throws IOException, ClassNotFoundException {
        ArrayList<Cell> possibleMoves = findPossibleMoves(activeWorker.getPosition());
        Cell movePosition = client.chooseMovePosition(possibleMoves);
        try {
            activeWorker.move(movePosition);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: illegal move");
        }
        gameController.broadcastBoard("move", null);
    }

    /**
     * handles the building phase of the turn
     */
    public void buildPhase() throws IOException, ClassNotFoundException {
        ArrayList<Cell> possibleBuilds = findPossibleBuilds(activeWorker.getPosition());
        Cell buildPosition = client.chooseBuildPosition(possibleBuilds);
        try {
            buildPosition.build();
        } catch (IllegalStateException e) {
            System.out.println("ERROR: illegal build");
        }
        gameController.broadcastBoard("build", null);
    }

    public boolean checkWin() {
        return (activeWorker.getPosition().getBuildLevel() == 3) &&
                (activeWorker.getPosition().getBuildLevel() - startingPosition.getBuildLevel() == 1);
    }

    /**
     * returns all the cells where a worker can move, with the only restrictions due to the general rules (other workers, domes, building levels)
     *
     * @param workerPosition the position of the worker
     * @return all the cells where a worker can move
     */
    public ArrayList<Cell> findPossibleMoves(Cell workerPosition) {
        ArrayList<Cell> neighbors = board.getNeighbors(workerPosition);
        ArrayList<Cell> possibleMoves = new ArrayList<Cell>();
        for (Cell cell : neighbors) {
            if (!cell.hasWorker() && !cell.isDomed() && (cell.getBuildLevel() <= workerPosition.getBuildLevel() + 1))
                possibleMoves.add(cell);
        }
        return findLegalMoves(workerPosition, possibleMoves);
    }

    /**
     * returns all the legal moves, applying possible restrictions due to active God Power Cards
     *
     * @param workerPosition the position of the worker
     * @param possibleMoves  all the cells where the worker can move, with the only restrictions due to the general rules
     * @return all the cells where a worker can effectively move
     */
    public ArrayList<Cell> findLegalMoves(Cell workerPosition, ArrayList<Cell> possibleMoves) {
        for (Card modifier : game.getActiveModifiers()) {
            if (modifier.getController().getPlayer() == player) continue;
            possibleMoves = modifier.getController().limitMoves(workerPosition, possibleMoves);
        }
        return possibleMoves;
    }

    /**
     * returns all the cells where a worker can build, with the only restrictions due to the general rules (other workers and domes)
     *
     * @param workerPosition the position of the worker
     * @return all the cells where a worker can build
     */
    public ArrayList<Cell> findPossibleBuilds(Cell workerPosition) {
        ArrayList<Cell> neighbors = board.getNeighbors(workerPosition);
        ArrayList<Cell> possibleBuilds = new ArrayList<Cell>();
        for (Cell cell : neighbors) {
            if (!cell.hasWorker() && !cell.isDomed())
                possibleBuilds.add(cell);
        }
        return findLegalBuilds(workerPosition, possibleBuilds);
    }

    /**
     * returns all the legal builds, applying possible restrictions due to active God Power Cards
     *
     * @param workerPosition the position of the worker
     * @param possibleBuilds all the cells where the worker can build, with the only resctrictions due to the general rules
     * @return all the cells where a worker can effectively build
     */
    public ArrayList<Cell> findLegalBuilds(Cell workerPosition, ArrayList<Cell> possibleBuilds) {
        for (Card modifier : game.getActiveModifiers()) {
            if (modifier.getController().getPlayer() == player) continue;
            possibleBuilds = modifier.getController().limitBuilds(workerPosition, possibleBuilds);
        }
        return possibleBuilds;
    }

    /**
     * gets a list containing all the cells where an opponent worker can move and creates another list, removing from
     * the previous all the cells that are not allowed due to this God Power Card
     *
     * @param workerPosition the position of the worker
     * @param possibleMoves  all the cells where the worker can move, considering only the game restrictions
     * @return all the cells where the worker is actually able to build
     */
    public ArrayList<Cell> limitMoves(Cell workerPosition, ArrayList<Cell> possibleMoves) {
        return possibleMoves;
    }

    /**
     * gets a list containing all the cells where an opponent worker can build and creates another list, removing from
     * the previous all the cells that are not allowed due to this God Power Card
     *
     * @param workerPosition the position of the worker
     * @param possibleBuilds all the cells where the worker is able to build, considering only the game restrictions
     * @return all the cells where the worker is actually able to build
     */
    public ArrayList<Cell> limitBuilds(Cell workerPosition, ArrayList<Cell> possibleBuilds) {
        return possibleBuilds;
    }

}