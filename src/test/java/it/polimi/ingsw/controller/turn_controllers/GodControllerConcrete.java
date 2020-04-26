package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.cards.Card;

public class GodControllerConcrete  extends GodController{

    /**
     * creates a God Controller for this game
     *
     * @param gameController
     */
    public GodControllerConcrete(GameController gameController) {
        super(gameController);
    }

   @Override
   public Card generateCard() {
        return null;
    }

}
