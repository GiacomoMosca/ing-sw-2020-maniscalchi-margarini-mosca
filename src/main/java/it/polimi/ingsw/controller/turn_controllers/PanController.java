package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.cards.Card;

public class PanController extends GodController {

    /**
     * creates a Pan controller for this game
     *
     * @param gameController
     */
    public PanController(GameController gameController) {
        super(gameController);
    }

    /**
     * sets all the attributes of the God Card Pan to their correct values
     *
     * @return a complete Card
     */
    @Override
    public Card generateCard() {
        Card card = new Card(
                "Pan",
                "God of the Wild",
                "Win Condition: You also win if your Worker moves down two " +
                        "\nor more levels.",
                1,
                false,
                this
        );
        this.card = card;
        return card;
    }

    /**
     * @return true if a worker moves up from level two to level three or moves down two or more levels, false otherwise
     */
    @Override
    public String checkWin() {
        if ((activeWorker.getPosition().getBuildLevel() == 3) && (activeWorker.getPosition().getBuildLevel() - startingPosition.getBuildLevel() == 1))
            return "winConditionAchieved";
        if (startingPosition.getBuildLevel() - activeWorker.getPosition().getBuildLevel() >= 2)
            return "godConditionAchieved";
        return "nope";
    }

}
