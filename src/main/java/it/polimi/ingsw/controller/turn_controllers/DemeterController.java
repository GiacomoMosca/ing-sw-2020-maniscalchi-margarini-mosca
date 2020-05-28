package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.exceptions.IllegalBuildException;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.view.CellView;

import java.io.IOException;
import java.util.ArrayList;

public class DemeterController extends GodController {

    /**
     * Creates a Demeter controller for this game.
     *
     * @param gameController the Game Controller for this game
     */
    public DemeterController(GameController gameController) {
        super(gameController);
    }

    /**
     * Sets all the attributes of the God Card Demeter to their actual values.
     *
     * @return a complete Card
     */
    @Override
    public Card generateCard() {
        Card card = new Card(
                "Demeter",
                "Goddess of the Harvest",
                "Your Build: Your Worker may build one additional time, but not on the same space.",
                1,
                false,
                this
        );
        this.card = card;
        return card;
    }

    /**
     * Handles the building phase of the turn, and eventually allows building one additional time, but not on the same
     * space.
     */
    @Override
    public void buildPhase() throws NullPointerException, IOException, InterruptedException, IOExceptionFromController {
        ArrayList<Cell> possibleBuilds = findPossibleBuilds(activeWorker.getPosition());
        Cell buildPosition = client.chooseBuildPosition(possibleBuilds);
        try {
            buildPosition.build();
        } catch (IllegalBuildException e) {
            System.out.println(e.getMessage());
        }
        displayBuild(new CellView(buildPosition), null);
        possibleBuilds.remove(buildPosition);
        if ((possibleBuilds.size() != 0) && (client.chooseYesNo("Do you want to build again?"))) {
            buildPosition = client.chooseBuildPosition(possibleBuilds);
            try {
                buildPosition.build();
            } catch (IllegalBuildException e) {
                System.out.println(e.getMessage());
            }
            displayBuild(new CellView(buildPosition), card);
        }
    }

}
