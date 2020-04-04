package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;

public class PanController extends GodController {

    private Cell previousPosition;

    public PanController(GameController gameController) {
        super(gameController);
    }

    @Override
    public Card generateCard() {
        Card card = new Card(
                "Pan",
                "God of the Wild",
                "Win Condition: You also win if your Worker moves down two or more levels.",
                1,
                false,
                this
        );
        this.card = card;
        return card;
    }

    @Override
    protected String runPhases() {
        previousPosition=activeWorker.getPosition();
        movePhase();
        if (checkWin()) return "WON";
        buildPhase();
        return "NEXT";
    }

    @Override
    protected boolean checkWin() {
        //Player also win if his worker moves down two or more levels
        return ((activeWorker.getPosition().getBuildLevel() == 3) || (previousPosition.getBuildLevel()-activeWorker.getPosition().getBuildLevel()>=2));
    }
}
