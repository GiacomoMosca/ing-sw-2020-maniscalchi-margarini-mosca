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
 * Extension of the GodController class that modifies the turn according to Minotaur's God Power.
 */
public class MinotaurController extends GodController {

    /**
     * MinotaurController constructor.
     *
     * @param gameController the GameController for this Game
     */
    public MinotaurController(GameController gameController) {
        super(gameController);
    }

    /**
     * Sets all the attributes of the God Card Minotaur to their actual values.
     *
     * @return the complete Card
     */
    @Override
    public Card generateCard() {
        Card card = new Card(
                "Minotaur",
                "Bull-headed Monster",
                "Your Move: Your Worker may move into an opponent Worker’s space " +
                        "\n(using normal movement rules), if the next space in the same direction is " +
                        "\nunoccupied. Their Worker is forced into that space (regardless of its level).",
                1,
                false,
                this
        );
        this.card = card;
        return card;
    }

    /**
     * Handles the moving phase of the turn, allowing pushing away opponent Workers. If the Player chooses to move to a cell occupied by an opponent Worker, checks if it is unoccupied: if it's free, handles the pushing away of the opponent worker.
     * Calls displayMove with a non-null Card parameter if the Minotaur God Power was used.
     *
     * @throws IOException               when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException      when the thread handling the communication is waiting and it is interrupted before or during its activity
     * @throws IOExceptionFromController when an IOException from a specific PlayerController occurs
     */
    @Override
    public void movePhase() throws IOException, InterruptedException, IOExceptionFromController {
        boolean godPower = false;
        ArrayList<Cell> possibleMoves = findPossibleMoves(activeWorker.getPosition());
        Cell movePosition = client.chooseMovePosition(possibleMoves);
        CellView startView = new CellView(activeWorker.getPosition());
        CellView endView = new CellView(movePosition);
        CellView startView2 = null;
        CellView endView2 = null;
        // + allow pushing away opponents
        if (movePosition.hasWorker()) {
            godPower = true;
            Worker pushedWorker = movePosition.getWorker();
            Cell nextCell;
            int nextX = movePosition.getPosX() + (movePosition.getPosX() - activeWorker.getPosition().getPosX());
            int nextY = movePosition.getPosY() + (movePosition.getPosY() - activeWorker.getPosition().getPosY());
            nextCell = board.getCell(nextX, nextY);
            startView2 = endView;
            endView2 = new CellView(nextCell);
            try {
                pushedWorker.move(nextCell);
            } catch (IllegalMoveException e) {
                gameController.logError(e.getMessage());
                return;
            }
            //
        }
        try {
            activeWorker.move(movePosition);
        } catch (IllegalMoveException e) {
            gameController.logError(e.getMessage());
        }
        if (godPower) displayMove(startView, endView, startView2, endView2, card);
        else displayMove(startView, endView, null);
    }

    /**
     * Returns all the Cells where a Worker can move, with the only restrictions due to the general rules (other workers, domes, building levels). Also allows the Worker to move to cells occupied by opponents (if they can be pushed away).
     *
     * @param workerPosition the position of the Worker
     * @return an ArrayList containing all the Cells where a Worker can move
     */
    @Override
    public ArrayList<Cell> findPossibleMoves(Cell workerPosition) {
        ArrayList<Cell> neighbors = board.getNeighbors(workerPosition);
        ArrayList<Cell> possibleMoves = new ArrayList<Cell>();
        for (Cell cell : neighbors) {
            // + allow movement to cells occupied by opponents, if they can be pushed away
            Cell nextCell;
            int nextX = cell.getPosX() + (cell.getPosX() - workerPosition.getPosX());
            int nextY = cell.getPosY() + (cell.getPosY() - workerPosition.getPosY());
            try {
                nextCell = board.getCell(nextX, nextY);
            } catch (ArrayIndexOutOfBoundsException e) {
                nextCell = null;
            }
            if ((!cell.hasWorker() || (nextCell != null && !nextCell.hasWorker() && !nextCell.isDomed())) &&
                    !cell.isDomed() && (cell.getBuildLevel() <= workerPosition.getBuildLevel() + 1))
                possibleMoves.add(cell);
            //
        }
        return findLegalMoves(workerPosition, possibleMoves);
    }

}
