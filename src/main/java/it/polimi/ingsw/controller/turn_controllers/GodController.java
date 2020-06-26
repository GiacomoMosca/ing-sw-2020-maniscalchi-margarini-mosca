package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.exceptions.IllegalBuildException;
import it.polimi.ingsw.exceptions.IllegalMoveException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Board;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.view.CellView;
import it.polimi.ingsw.view.VirtualView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The GodController abstract class. All the GodController implementing the 14 God Powers of the Game extends this class:
 * Apollo, Artemis, Athena, Atlas, Demeter, Hephaestus, Hestia, Limus, Medusa, Minotaur, Pan, Prometheus, Triton, Zeus.
 */
public abstract class GodController {

    protected final GameController gameController;
    protected final Game game;
    protected final Board board;
    public Player player;
    public Worker activeWorker;
    protected Card card;
    protected VirtualView client;
    protected Cell startingPosition;

    /**
     * GodController constructor.
     *
     * @param gameController the GameController for this Game.
     */
    public GodController(GameController gameController) {
        this.gameController = gameController;
        this.game = gameController.getGame();
        this.board = this.game.getBoard();
    }


    /**
     * Sets all the attributes of the God Card to their actual values.
     *
     * @return the complete Card
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
     * @return the Player associated with this GodController
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the VirtualView associated with the Player
     */
    public VirtualView getClient() {
        return client;
    }

    /**
     * Sets the attributes player and client to the values passed as arguments.
     *
     * @param player the Player
     * @param client the Client associated with this player
     */
    public void setPlayer(Player player, VirtualView client) {
        this.player = player;
        this.client = client;
    }

    /**
     * Checks if the Worker can move.
     *
     * @param worker the active Worker
     * @return true if the Worker can move, false otherwise
     */
    public boolean canPlay(Worker worker) {
        return findPossibleMoves(worker.getPosition()).size() > 0;
    }

    /**
     * Handles the basic phases of a turn: moving and building.
     *
     * @return "godConditionAchieved" if the Player won, "next" if the game goes on
     */
    public String runPhases(Worker worker) throws IOException, InterruptedException, IOExceptionFromController {
        activeWorker = worker;
        startingPosition = worker.getPosition();
        movePhase();
        if (!checkWin().equals("nope")) return checkWin();
        if (findPossibleBuilds(activeWorker.getPosition()).size() == 0) return "outOfBuilds";
        buildPhase();
        return "next";
    }

    /**
     * Handles the moving phase of the turn.
     *
     * @throws IOException
     * @throws InterruptedException
     * @throws IOExceptionFromController
     */
    public void movePhase() throws IOException, InterruptedException, IOExceptionFromController {
        ArrayList<Cell> possibleMoves = findPossibleMoves(activeWorker.getPosition());
        Cell movePosition = client.chooseMovePosition(possibleMoves);
        CellView startView = new CellView(activeWorker.getPosition());
        CellView endView = new CellView(movePosition);
        try {
            activeWorker.move(movePosition);
        } catch (IllegalMoveException e) {
            System.out.println(e.getMessage());
        }
        displayMove(startView, endView, null);
    }

    /**
     * Handles the building phase of the turn.
     *
     * @throws IOException
     * @throws InterruptedException
     * @throws IOExceptionFromController
     */
    public void buildPhase() throws IOException, InterruptedException, IOExceptionFromController {
        ArrayList<Cell> possibleBuilds = findPossibleBuilds(activeWorker.getPosition());
        Cell buildPosition = client.chooseBuildPosition(possibleBuilds);
        try {
            buildPosition.build();
        } catch (IllegalBuildException e) {
            System.out.println(e.getMessage());
        }
        displayBuild(new CellView(buildPosition), null);
    }

    /**
     * Checks if the active Player won.
     *
     * @return "winConditionAchieved" if the active Player won, "nope" otherwise
     */
    public String checkWin() {
        if ((activeWorker.getPosition().getBuildLevel() == 3) && (activeWorker.getPosition().getBuildLevel() - startingPosition.getBuildLevel() >= 1))
            return "winConditionAchieved";
        return "nope";
    }

    /**
     * Returns all the Cells where a Worker can move, with the only restrictions due to the general rules (other workers, domes, building levels).
     *
     * @param workerPosition the position of the Worker
     * @return an ArrayList containing all the Cells where a worker can move
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
     * Returns all the legal moves, applying possible restrictions due to active God Power Cards.
     *
     * @param workerPosition the position of the Worker
     * @param possibleMoves  all the Cells where the Worker can move, with the only restrictions due to the general rules
     * @return an ArrayList containing all the Cells where a Worker can effectively move
     */
    public ArrayList<Cell> findLegalMoves(Cell workerPosition, ArrayList<Cell> possibleMoves) {
        for (Card modifier : game.getActiveModifiers()) {
            if (modifier.getController().getPlayer() == player) continue;
            possibleMoves = modifier.getController().limitMoves(workerPosition, possibleMoves);
        }
        return possibleMoves;
    }

    /**
     * Returns all the Cells where a Worker can build, with the only restrictions due to the general rules (other workers and domes).
     *
     * @param workerPosition the position of the Worker
     * @return an ArrayList containing all the Cells where a Worker can build
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
     * Returns all the legal builds, applying possible restrictions due to active God Power Cards.
     *
     * @param workerPosition the position of the Worker
     * @param possibleBuilds all the cells where the Worker can build, with the only restrictions due to the general rules
     * @return an ArrayList containing all the Cells where a Worker can effectively build
     */
    public ArrayList<Cell> findLegalBuilds(Cell workerPosition, ArrayList<Cell> possibleBuilds) {
        for (Card modifier : game.getActiveModifiers()) {
            if (modifier.getController().getPlayer() == player) continue;
            possibleBuilds = modifier.getController().limitBuilds(workerPosition, possibleBuilds);
        }
        return possibleBuilds;
    }

    /**
     * Applies the God Power associated with this GodController. Gets an ArrayList containing all the cells where an opponent Worker can move and creates another ArrayList, removing from the first one all the Cells that are not allowed due to this God Power Card.
     *
     * @param workerPosition the position of the Worker
     * @param possibleMoves  all the Cells where the Worker can move, considering only the game restrictions
     * @return an ArrayList containing all the Cells where the Worker is actually able to build
     */
    public ArrayList<Cell> limitMoves(Cell workerPosition, ArrayList<Cell> possibleMoves) {
        return possibleMoves;
    }

    /**
     * Applies the God Power associated with this GodController. Gets an ArrayList containing all the Cells where an opponent Worker can build and creates another ArrayList, removing from the first one all the Cells that are not allowed due to this God Power Card.
     *
     * @param workerPosition the position of the Worker
     * @param possibleBuilds all the Cells where the Worker is able to build, considering only the game restrictions
     * @return all the Cells where the Worker is actually able to build
     */
    public ArrayList<Cell> limitBuilds(Cell workerPosition, ArrayList<Cell> possibleBuilds) {
        return possibleBuilds;
    }

    /**
     * Creates an HashMap containing the starting position and the final position of a move.
     * Calls the broadcastMove method so that the move can be properly displayed on screen.
     *
     * @param startPosition the starting position of a move
     * @param endPosition   the final position of a move
     * @param godPower      the God Power Card who allowed this move, eventually null in a basic move
     * @throws IOExceptionFromController
     */
    protected void displayMove(CellView startPosition, CellView endPosition, Card godPower) throws IOExceptionFromController {
        HashMap<CellView, CellView> moves = new HashMap<CellView, CellView>();
        moves.put(startPosition, endPosition);
        gameController.broadcastMove(moves, godPower);
    }

    /**
     * Creates an HashMap containing two couples of positions: the starting position and the final position of a move for two players.
     * Calls the broadcastMove method so that this double move can be properly displayed on screen.
     *
     * @param startPosition1 the starting position of a move for the first player
     * @param endPosition1 the final position of a move for the first player
     * @param startPosition2 the starting position of a move for the second player
     * @param endPosition2 the final position of a move for the second player
     * @param godPower the God Power Card who allowed this move
     * @throws IOExceptionFromController
     */
    protected void displayMove(CellView startPosition1, CellView endPosition1, CellView startPosition2, CellView endPosition2, Card godPower) throws IOExceptionFromController {
        HashMap<CellView, CellView> moves = new HashMap<CellView, CellView>();
        moves.put(startPosition1, endPosition1);
        moves.put(startPosition2, endPosition2);
        gameController.broadcastMove(moves, godPower);
    }

    /**
     * Calls the broadcastBuild method so that this build can properly be displayed on screen.
     *
     * @param buildPosition the position of the build
     * @param godPower the God Card that eventually allowed this build
     * @throws IOExceptionFromController
     */
    protected void displayBuild(CellView buildPosition, Card godPower) throws IOExceptionFromController {
        gameController.broadcastBuild(buildPosition, godPower);
    }

}