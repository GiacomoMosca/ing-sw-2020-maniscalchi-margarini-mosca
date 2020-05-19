package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.exceptions.IllegalBuildException;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.view.CellView;

import java.io.IOException;
import java.util.ArrayList;

public class HephaestusController extends GodController {

    /**
     * Creates an Hephaestus controller for this game.
     *
     * @param gameController the Game Controller for this game
     */
    public HephaestusController(GameController gameController) {
        super(gameController);
    }

    /**
     * Sets all the attributes of the God Card Hephaestus to their actual values.
     *
     * @return the complete Card
     */
    @Override
    public Card generateCard() {
        Card card = new Card(
                "Hephaestus",
                "God of Blacksmiths",
                "Your Build: Your Worker may build one additional block (not dome) on top of your first block.",
                1,
                false,
                this
        );
        this.card = card;
        return card;
    }

    /**
     * Handles the building phase of the turn, which can be duplicated if the player wants: the player can build two
     * times on the same block (not a dome).
     */
    @Override
    public void buildPhase() throws IOException, ClassNotFoundException, IOExceptionFromController {
        Card godPower = null;
        ArrayList<Cell> possibleBuilds = findPossibleBuilds(activeWorker.getPosition());
        Cell buildPosition = client.chooseBuildPosition(possibleBuilds);
        try {
            buildPosition.build();
        } catch (IllegalBuildException e) {
            System.out.println(e.getMessage());
        }
        if (buildPosition.getBuildLevel() <= 2) {
            if (client.chooseYesNo("Do you want to build here again?")) {
                godPower = card;
                try {
                    buildPosition.build();
                } catch (IllegalBuildException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        displayBuild(new CellView(buildPosition), godPower);
    }

}
