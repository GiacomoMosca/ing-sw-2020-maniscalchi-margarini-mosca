package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;

import java.util.ArrayList;

public class LimusController extends GodController {

    public LimusController(GameController gameController) {
        super(gameController);
    }

    @Override
    public Card generateCard() {
        Card card = new Card(
                "Limus",
                "Goddess of Famine",
                "Opponent’s Turn: Opponent Workers cannot build on spaces neighboring your Workers, unless building a dome to create a Complete Tower.",
                2,
                true,
                this
        );
        this.card = card;
        return card;
    }

    @Override
    public ArrayList<Cell> limitBuilds(Cell workerPosition, ArrayList<Cell> possibleBuilds) {
        // + opponents can only build Complete Towers next to your workers
        ArrayList<Cell> limitedBuilds = new ArrayList<Cell>();
        for (Cell cell : possibleBuilds) {
            for (Cell neighborCell : board.getNeighbors(cell)) {
                if (neighborCell.hasWorker() && neighborCell.getWorker().getOwner() == player && cell.getBuildLevel() < 3) continue;
            }
            limitedBuilds.add(cell);
        }
        return limitedBuilds;
        //
    }
}
