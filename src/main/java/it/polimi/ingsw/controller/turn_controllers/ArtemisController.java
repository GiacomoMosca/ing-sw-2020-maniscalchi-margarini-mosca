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

/**
 * Extension of the GodController class that modifies the turn according to Artemis's God Power.
 */
public class ArtemisController extends GodController {

    private Cell beginningCell;
    private boolean secondMove;

    /**
     * ArtemisController constructor.
     *
     * @param gameController the GameController for this Game
     */
    public ArtemisController(GameController gameController) {
        super(gameController);
    }

    /**
     * Sets all the attributes of the God Card Artemis to their correct values.
     *
     * @return the complete Card
     */
    @Override
    public Card generateCard() {
        Card card = new Card(
                "Artemis",
                "Goddess of the Hunt",
                "Your Move: Your Worker may move one additional time, " +
                        "\nbut not back to the space it started on.",
                1,
                false,
                this
        );
        this.card = card;
        return card;
    }


    /**
     * Handles the phases of a turn: moving (which is allowed two times, but not going back to the first cell) and building.
     *
     * @param worker the active Worker
     * @return "winConditionAchieved" if the player won, "next" if the game goes on, "outOfBuilds" if the worker can't build
     * @throws IOException               when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException      when the thread handling the communication is waiting and it is interrupted before or during its activity
     * @throws IOExceptionFromController when an IOException from a specific PlayerController occurs
     */
    @Override
    public String runPhases(Worker worker) throws IOException, InterruptedException, IOExceptionFromController {
        activeWorker = worker;
        startingPosition = worker.getPosition();
        beginningCell = startingPosition;
        secondMove = false;
        movePhase();
        if (!checkWin().equals("nope")) return checkWin();
        secondMove = client.chooseYesNo("Do you want to move again?");
        if (secondMove) {
            startingPosition = worker.getPosition();
            movePhase();
            if (!checkWin().equals("nope")) return checkWin();
        }
        if (findPossibleBuilds(activeWorker.getPosition()).size() == 0) return "outOfBuilds";
        buildPhase();
        return "next";
    }

    /**
     * Handles the moving phase of the turn. Calls displayMove with a non-null Card parameter if the Artemis God Power was used.
     *
     * @throws IOException               when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException      when the thread handling the communication is waiting and it is interrupted before or during its activity
     * @throws IOExceptionFromController when an IOException from a specific PlayerController occurs
     */
    @Override
    public void movePhase() throws IOException, InterruptedException, IOExceptionFromController {
        Card godPower = (secondMove) ? card : null;
        ArrayList<Cell> possibleMoves = findPossibleMoves(activeWorker.getPosition());
        Cell movePosition = client.chooseMovePosition(possibleMoves);
        CellView startView = new CellView(activeWorker.getPosition());
        CellView endView = new CellView(movePosition);
        try {
            activeWorker.move(movePosition);
        } catch (IllegalMoveException e) {
            gameController.logError(e.getMessage());
        }
        displayMove(startView, endView, godPower);
    }

    /**
     * Returns all the Cells where a Worker can move, with the only restrictions due to the general rules (other workers, domes, building levels) and ensures that, if the Player uses a second move, his Worker won't move back to the Cell it started from.
     *
     * @param workerPosition the position of the Worker
     * @return an ArrayList containing all the Cells where a Worker can move
     */
    @Override
    public ArrayList<Cell> findPossibleMoves(Cell workerPosition) {
        ArrayList<Cell> neighbors = board.getNeighbors(workerPosition);
        ArrayList<Cell> possibleMoves = new ArrayList<Cell>();
        for (Cell cell : neighbors) {
            if (secondMove && cell.equals(beginningCell))
                continue;
            if (!cell.hasWorker() && !cell.isDomed() && (cell.getBuildLevel() <= workerPosition.getBuildLevel() + 1))
                possibleMoves.add(cell);
        }
        return findLegalMoves(workerPosition, possibleMoves);
    }

}