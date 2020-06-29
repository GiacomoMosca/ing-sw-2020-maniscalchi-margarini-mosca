package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.players.Worker;

import java.io.IOException;

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

    @Override
    public String runPhases(Worker worker) throws IOException, InterruptedException, IOExceptionFromController {
        return super.runPhases(worker);
    }
}
