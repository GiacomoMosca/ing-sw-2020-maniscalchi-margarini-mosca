package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.exceptions.IllegalMoveException;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.view.CellView;

import java.io.IOException;
import java.util.ArrayList;

public class PrometheusController extends GodController {

    private boolean wantBuildBefore;

    /**
     * Creates a Prometheus controller for this game.
     *
     * @param gameController the Game Controller for this game
     */
    public PrometheusController(GameController gameController) {
        super(gameController);
    }

    /**
     * Sets all the attributes of the God Card Prometheus to their actual values.
     *
     * @return the complete Card
     */
    @Override
    public Card generateCard() {
        Card card = new Card(
                "Prometheus",
                "Titan Benefactor of Mankind",
                "Your Turn: If your Worker does not move up, it may build both before and after moving.",
                1,
                false,
                this
        );
        this.card = card;
        return card;
    }


    /**
     * Handles the phases of a turn: moving and building (which may be allowed two times, both before and after moving).
     *
     * @param worker the active worker during this turn
     * @return "WON" if the player won, "NEXT" if the game goes on
     */
    @Override
    public String runPhases(Worker worker) throws IOException, ClassNotFoundException, IOExceptionFromController {
        activeWorker = worker;
        startingPosition = worker.getPosition();
        wantBuildBefore = false;
        if (checkMoves()) {
            wantBuildBefore = client.chooseYesNo("Do you want to build before moving?");
            if (wantBuildBefore) {
                buildPhase();
            }
        }
        ArrayList<Cell> possibleMoves = wantBuildBefore ? findPossibleMoves(activeWorker.getPosition()) : findPossibleMovesNoUp(activeWorker.getPosition());
        if (possibleMoves.size() == 0) return "outOfMoves";
        movePhase();
        if (!checkWin().equals("nope")) return checkWin();
        if (findPossibleBuilds(activeWorker.getPosition()).size() == 0) return "outOfBuilds";
        buildPhase();
        return "next";
    }


    /**
     * @return true if any move from a level to the same level or less is allowed, false otherwise
     */
    protected boolean checkMoves() {
        ArrayList<Cell> neighbors = board.getNeighbors(activeWorker.getPosition());
        ArrayList<Cell> possibleMoves = new ArrayList<Cell>();
        for (Cell cell : neighbors) {
            if (!cell.isDomed() && !cell.hasWorker() && (cell.getBuildLevel() <= activeWorker.getPosition().getBuildLevel()))
                possibleMoves.add(cell);
        }
        findLegalMoves(activeWorker.getPosition(), possibleMoves); //ok?
        if (possibleMoves.size() == 0) return false;
        else return true;
    }

    /**
     * Handles the moving phase of the turn. If the player didn't build before moving, normally handles the phase;
     * otherwise doesn't allow him to move up
     */
    @Override
    public void movePhase() throws IOException, ClassNotFoundException, IOExceptionFromController {
        Card godPower = (wantBuildBefore) ? card : null;
        ArrayList<Cell> possibleMoves;
        if (wantBuildBefore) possibleMoves = findPossibleMovesNoUp(activeWorker.getPosition());
        else possibleMoves = findPossibleMoves(activeWorker.getPosition());
        Cell movePosition = client.chooseMovePosition(possibleMoves);
        CellView startView = new CellView(activeWorker.getPosition());
        CellView endView = new CellView(movePosition);
        try {
            activeWorker.move(movePosition);
        } catch (IllegalMoveException e) {
            System.out.println(e.getMessage());
        }
        displayMove(startView, endView, godPower);
    }

    /**
     * Finds all the possible moves that don't require moving up.
     *
     * @param workerPosition    the position of the worker
     * @return                  all the cells where a worker can move
     */
    protected ArrayList<Cell> findPossibleMovesNoUp(Cell workerPosition) {
        ArrayList<Cell> neighbors = board.getNeighbors(workerPosition);
        ArrayList<Cell> possibleMoves = new ArrayList<Cell>();
        for (Cell cell : neighbors) {
            if (!cell.isDomed() && !cell.hasWorker() && (cell.getBuildLevel() <= activeWorker.getPosition().getBuildLevel()))
                possibleMoves.add(cell);
        }
        return findLegalMoves(workerPosition, possibleMoves);
    }

}
