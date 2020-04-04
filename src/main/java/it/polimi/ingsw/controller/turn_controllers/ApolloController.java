package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Worker;

import java.util.ArrayList;

public class ApolloController extends GodController {

    public ApolloController(GameController gameController) {
        super(gameController);
    }

    @Override
    public Card generateCard() {
        Card card = new Card(
                "Apollo",
                "God of Music",
                "Your Move: Your Worker may move into an opponent Worker’s space (using normal movement rules) and force their Worker to the space yours just vacated (swapping their positions).",
                1,
                false,
                this
        );
        this.card = card;
        return card;
    }

    @Override
    protected void movePhase() {
        ArrayList<Cell> possibleMoves = findPossibleMoves(activeWorker.getPosition());
        // + allow swapping position with opponents
        Cell oldPosition = activeWorker.getPosition();
        Cell movePosition = client.chooseMovePosition(possibleMoves);
        if (movePosition.hasWorker()) {
            Worker swappedWorker = movePosition.getWorker();
            oldPosition.setWorker(swappedWorker);
            swappedWorker.setPosition(oldPosition);
            movePosition.setWorker(activeWorker);
            activeWorker.setPosition(movePosition);
            //
        } else {
            try {
                activeWorker.move(movePosition);
            } catch (IllegalArgumentException e) {
                System.out.println("ERROR: illegal move");
            }
        }
    }

    @Override
    protected ArrayList<Cell> findPossibleMoves(Cell workerPosition) {
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
