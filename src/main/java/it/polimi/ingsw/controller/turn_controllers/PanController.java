package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.cards.Card;

public class PanController extends GodController {

    /**
     * PanController constructor.
     *
     * @param gameController the GameController for this Game
     */
    public PanController(GameController gameController) {
        super(gameController);
    }

    /**
     * Sets all the attributes of the God Card Pan to their correct values.
     *
     * @return the complete Card
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
     * @return "winConditionAchieved" if the Player won because moved up from the second to the third level, "godConditionAchieved" if the Player won because he achieved his God's win condition,"nope" if the Player didn't win
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
