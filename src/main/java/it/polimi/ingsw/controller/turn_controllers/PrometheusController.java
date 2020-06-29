package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.exceptions.IllegalBuildException;
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
     * PrometheusController constructor.
     *
     * @param gameController the GameController for this Game
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
                "Your Turn: If your Worker does not move up, it may build both " +
                        "\nbefore and after moving.",
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
     * @param worker the active Worker
     * @return "winConditionAchieved" if the Player won, "next" if the game goes on, "outOfMoves" if the Worker can't move, "outOfBuilds" if the Worker can't build
     * @throws IOException               when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException      when the thread handling the communication is waiting and it is interrupted before or during its activity
     * @throws IOExceptionFromController when an IOException from a specific PlayerController occurs
     */
    @Override
    public String runPhases(Worker worker) throws IOException, InterruptedException, IOExceptionFromController {
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
        wantBuildBefore = false;
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
     * Handles the moving phase of the turn. If the Player didn't build before moving, normally handles the phase, otherwise doesn't allow him to move up.
     *
     * @throws IOException
     * @throws InterruptedException
     * @throws IOExceptionFromController
     */
    @Override
    public void movePhase() throws IOException, InterruptedException, IOExceptionFromController {
        ArrayList<Cell> possibleMoves;
        if (wantBuildBefore) possibleMoves = findPossibleMovesNoUp(activeWorker.getPosition());
        else possibleMoves = findPossibleMoves(activeWorker.getPosition());
        Cell movePosition = client.chooseMovePosition(possibleMoves);
        CellView startView = new CellView(activeWorker.getPosition());
        CellView endView = new CellView(movePosition);
        try {
            activeWorker.move(movePosition);
        } catch (IllegalMoveException e) {
            gameController.logError(e.getMessage());
        }
        displayMove(startView, endView, null);
    }

    /**
     * Finds all the possible moves that don't require move up.
     *
     * @param workerPosition the position of the Worker
     * @return an ArrayList containing all the Cells where a Worker can move
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

    /**
     * Handles the building phase of a turn, which may be duplicated (before and after moving).
     * Calls displayBuild with a non-null Card parameter if the Prometheus God Power was used.
     *
     * @throws IOException
     * @throws InterruptedException
     * @throws IOExceptionFromController
     */
    @Override
    public void buildPhase() throws IOException, InterruptedException, IOExceptionFromController {
        Card godPower = (wantBuildBefore) ? card : null;
        ArrayList<Cell> possibleBuilds = findPossibleBuilds(activeWorker.getPosition());
        Cell buildPosition = client.chooseBuildPosition(possibleBuilds);
        try {
            buildPosition.build();
        } catch (IllegalBuildException e) {
            gameController.logError(e.getMessage());
        }
        displayBuild(new CellView(buildPosition), godPower);
    }

}
