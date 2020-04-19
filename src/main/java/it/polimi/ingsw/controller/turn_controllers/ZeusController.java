package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;

import java.util.ArrayList;

public class ZeusController extends GodController {

    /**
     * creates a Zeus controller for this game
     *
     * @param gameController
     */
    public ZeusController(GameController gameController) {
        super(gameController);
    }

    /**
     * sets all the attributes of the God Card Zeus to their correct values
     *
     * @return the complete Card
     */
    @Override
    public Card generateCard() {
        Card card = new Card(
                "Zeus",
                "God of the Sky",
                "Your Build: Your Worker may build under itself in its current space, forcing it up one level. You do not win by forcing yourself up to the third level.",
                2,
                false,
                this
        );
        this.card = card;
        return card;
    }

    /**
     * returns all the cells where a worker can build including its current cell, with the only restrictions due to the general rules (other workers, domes, building levels)
     *
     * @param workerPosition the position of the worker
     * @return all the cells where a worker can build
     */
    @Override
    public ArrayList<Cell> findPossibleBuilds(Cell workerPosition) {
        ArrayList<Cell> neighbors = board.getNeighbors(workerPosition);
        ArrayList<Cell> possibleBuilds = new ArrayList<Cell>();
        for (Cell cell : neighbors) {
            if (!cell.hasWorker() && !cell.isDomed())
                possibleBuilds.add(cell);
        }
        possibleBuilds.add(workerPosition);
        return findLegalBuilds(workerPosition, possibleBuilds);
    }
}
