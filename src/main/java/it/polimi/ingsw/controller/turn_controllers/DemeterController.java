package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;

import java.util.ArrayList;

public class DemeterController extends GodController {

    public DemeterController(GameController gameController) {
        super(gameController);
    }

    @Override
    public Card generateCard() {
        Card card = new Card(
                "Demeter",
                "Goddes of the Harvest",
                "Your Build: Your Worker may build one additional time, but not on the same space.",
                1,
                false,
                this
        );
        this.card = card;
        return card;
    }

    @Override
    protected void buildPhase() throws NullPointerException {
        ArrayList<Cell> possibleBuilds = findPossibleBuilds(activeWorker.getPosition());
        Cell buildPosition = client.chooseBuildPosition(possibleBuilds);
        try {
            buildPosition.build();
        } catch (IllegalStateException e) {
            System.out.println("ERROR: illegal build");
        }
        possibleBuilds.remove(buildPosition);
        if((possibleBuilds.size()!=0)&&(client.chooseYesNo("Do you want to build again?"))) {
            buildPosition = client.chooseBuildPosition(possibleBuilds);
            try {
                buildPosition.build();
            } catch (IllegalStateException e) {
                System.out.println("ERROR: illegal build");
            }
        }
    }
}
