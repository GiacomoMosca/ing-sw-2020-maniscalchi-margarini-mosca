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

public class ApolloController extends GodController {

    /**
     * Creates an Apollo controller for this game.
     *
     * @param gameController the Game Controller for this game
     */
    public ApolloController(GameController gameController) {
        super(gameController);
    }


    /**
     * Sets all the attributes of the God Card Apollo to their actual values.
     *
     * @return a complete Card
     */
    @Override
    public Card generateCard() {
        Card card = new Card(
                "Apollo",
                "God of Music",
                "Your Move: Your Worker may move into an opponent Worker’s space " +
                        "\n(using normal movement rules) and force their Worker to the space " +
                        "\nyours just vacated (swapping their positions).",
                1,
                false,
                this
        );
        this.card = card;
        return card;
    }

    /**
     * Handles the moving phase of the turn, and eventually allows swapping position with opponent workers.
     * If the player chooses a move that requires swapping positions, handles this change.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Override
    public void movePhase() throws IOException, InterruptedException, IOExceptionFromController {
        boolean godPower = false;
        ArrayList<Cell> possibleMoves = findPossibleMoves(activeWorker.getPosition());
        // + allow swapping position with opponents
        Cell startPosition = activeWorker.getPosition();
        Cell movePosition = client.chooseMovePosition(possibleMoves);
        CellView startView = new CellView(startPosition);
        CellView endView = new CellView(movePosition);
        if (movePosition.hasWorker()) {
            godPower = true;
            Worker swappedWorker = movePosition.getWorker();
            startPosition.setWorker(swappedWorker);
            swappedWorker.setPosition(startPosition);
            movePosition.setWorker(activeWorker);
            activeWorker.setPosition(movePosition);
            //
        } else {
            try {
                activeWorker.move(movePosition);
            } catch (IllegalMoveException e) {
                System.out.println(e.getMessage());
            }
        }
        if (godPower) displayMove(startView, endView, endView, startView, card);
        else displayMove(startView, endView, null);
    }

    /**
     * Returns all the cells where a worker can move, with the only restrictions due to the general rules
     * (other workers, domes, building levels) and allows the movement to cells occupied by opponent workers.
     *
     * @param workerPosition    the position of the worker
     * @return                  all the cells where a worker can move
     */
    @Override
    public ArrayList<Cell> findPossibleMoves(Cell workerPosition) {
        ArrayList<Cell> neighbors = board.getNeighbors(workerPosition);
        ArrayList<Cell> possibleMoves = new ArrayList<Cell>();
        for (Cell cell : neighbors) {
            // + allow movement to cells occupied by opponents
            if ((!cell.hasWorker() || cell.getWorker().getOwner() != player) &&
                    !cell.isDomed() && (cell.getBuildLevel() <= workerPosition.getBuildLevel() + 1))
                possibleMoves.add(cell);
            //
        }
        return findLegalMoves(workerPosition, possibleMoves);
    }

}
