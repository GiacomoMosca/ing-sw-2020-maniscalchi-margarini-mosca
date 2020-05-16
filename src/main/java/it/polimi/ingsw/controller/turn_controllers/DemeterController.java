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
     * creates a Demeter controller for this game
     *
     * @param gameController
     */
    public DemeterController(GameController gameController) {
        super(gameController);
    }

    /**
     * sets all the attributes of the God Card Demeter to their correct values
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
     * handles the building phase of the turn, and eventually allows building one additional time, but not on the same space
     */
    @Override
    public void buildPhase() throws NullPointerException, IOException, ClassNotFoundException, IOExceptionFromController {
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
