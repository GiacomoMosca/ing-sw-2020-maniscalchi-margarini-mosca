package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;

import java.util.ArrayList;

/**
 * Extension of the GodController class that modifies the turn according to Limus's God Power.
 */
public class LimusController extends GodController {

    /**
     * LimusController constructor.
     *
     * @param gameController the GameController for this Game
     */
    public LimusController(GameController gameController) {
        super(gameController);
    }

    /**
     * Sets all the attributes of the God Card Limus to their actual values.
     *
     * @return the complete Card
     */
    @Override
    public Card generateCard() {
        Card card = new Card(
                "Limus",
                "Goddess of Famine",
                "Opponent’s Turn: Opponent Workers cannot build on spaces neighboring " +
                        "\nyour Workers, unless building a dome to create a Complete Tower.",
                2,
                true,
                this
        );
        this.card = card;
        return card;
    }

    /**
     * Applies Limus God Power. Gets an ArrayList containing all the Cells where an opponent Worker can build and creates another ArrayList, removing from the first one all the Cells neighboring the Limus Worker (unless the opponents can build there a dome to create a Complete Tower).
     *
     * @param workerPosition the position of the Worker
     * @param possibleBuilds all the Cells where the Worker is able to build, considering only the game restrictions
     * @return an ArrayList containing all the Cells where the Worker is actually able to build
     */
    @Override
    public ArrayList<Cell> limitBuilds(Cell workerPosition, ArrayList<Cell> possibleBuilds) {
        // + opponents can only build Complete Towers next to your workers
        ArrayList<Cell> limitedBuilds = new ArrayList<Cell>();
        LABEL:
        for (Cell cell : possibleBuilds) {
            for (Cell neighborCell : board.getNeighbors(cell)) {
                if (neighborCell.hasWorker() && neighborCell.getWorker().getOwner() == player && cell.getBuildLevel() < 3)
                    continue LABEL;
            }
            limitedBuilds.add(cell);
        }
        return limitedBuilds;
        //
    }

}
