package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.exceptions.IllegalMoveException;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Worker;

import java.io.IOException;
import java.util.ArrayList;

public class PrometheusController extends GodController {

    private boolean wantBuildBefore;

    /**
     * creates a Prometheus controller for this game
     *
     * @param gameController
     */
    public PrometheusController(GameController gameController) {
        super(gameController);
    }

    /**
     * sets all the attributes of the God Card Prometheus to their correct values
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
     * handles the phases of a turn: moving and building (which may be allowed two times, both before and after moving)
     *
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
     * handles the moving phase of the turn: normally handles the phase if the player didn't build before moving;
     * otherwise doesn't allow him to move up
     */
    @Override
    public void movePhase() throws IOException, ClassNotFoundException, IOExceptionFromController {
        Card godPower = (wantBuildBefore) ? card : null;
        ArrayList<Cell> possibleMoves;
        if (wantBuildBefore) possibleMoves = findPossibleMovesNoUp(activeWorker.getPosition());
        else possibleMoves = findPossibleMoves(activeWorker.getPosition());
        Cell movePosition = client.chooseMovePosition(possibleMoves);
        try {
            activeWorker.move(movePosition);
        } catch (IllegalMoveException e) {
            System.out.println(e.getMessage());
        }
        gameController.broadcastBoard("move", godPower);
    }

    /**
     * finds all the possible moves that don't move up
     *
     * @param workerPosition the position of the worker
     * @return all the cells where a worker can move
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
