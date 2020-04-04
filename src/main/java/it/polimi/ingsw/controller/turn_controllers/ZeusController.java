package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;

import java.util.ArrayList;

public class ZeusController extends GodController {

    public ZeusController(GameController gameController) {
        super(gameController);
    }

    @Override
    public Card generateCard() {
        Card card = new Card(
                "Zeus",
                "God of the Sky",
                "Your Build: Your Worker may build under itself in its current space, forcing it up one level. You do not win by forcing yourself up to the third level.",
                1,
                false,
                this
        );
        this.card = card;
        return card;
    }

    @Override
    protected ArrayList<Cell> findPossibleBuilds(Cell workerPosition) {
        ArrayList<Cell> neighbors = board.getNeighbors(workerPosition);
        ArrayList<Cell> possibleBuilds = new ArrayList<Cell>();
        for (Cell cell : neighbors) {
            if (!cell.hasWorker() && !cell.isDomed())
                possibleBuilds.add(cell);
        }
        possibleBuilds.add(workerPosition); //Zeus allows the worker to build under itself
        return findLegalBuilds(workerPosition, possibleBuilds);
    }
}
