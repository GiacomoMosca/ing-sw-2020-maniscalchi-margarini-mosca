package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;

import java.util.ArrayList;

public class LimusController extends GodController {

    /**
     * creates a Limus controller for this game
     *
     * @param gameController
     */
    public LimusController(GameController gameController) {
        super(gameController);
    }

    /**
     * sets all the attributes of the God Card Limus to their correct values
     *
     * @return the complete Card
     */
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

    /**
     * gets a list containing all the cells where an opponent worker can build and creates another list, removing from
     * the previous all the cells neighboring the associated workers (unless the opponents can build there a dome to
     * create a Complete Tower)
     *
     * @param workerPosition the position of the worker
     * @param possibleBuilds all the cells where the worker is able to build, considering only the game restrictions
     * @return all the cells where the worker is actually able to build
     */
    @Override
    public ArrayList<Cell> limitBuilds(Cell workerPosition, ArrayList<Cell> possibleBuilds) {
        // + opponents can only build Complete Towers next to your workers
        ArrayList<Cell> limitedBuilds = new ArrayList<Cell>();
        LABEL: for (Cell cell : possibleBuilds) {
             for (Cell neighborCell : board.getNeighbors(cell)) {
                if (neighborCell.hasWorker() && neighborCell.getWorker().getOwner() == player && cell.getBuildLevel() < 3) continue LABEL;
            }
            limitedBuilds.add(cell);
        }
        return limitedBuilds;
        //
    }
}
