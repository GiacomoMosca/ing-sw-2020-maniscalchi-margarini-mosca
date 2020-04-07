package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;

import java.util.ArrayList;

public class HephaestusController extends GodController {

    private boolean secondBuild;

    /**
     * creates an Hephaestus controller for this game
     *
     * @param gameController
     */
    public HephaestusController(GameController gameController) {
        super(gameController);
    }

    /**
     * sets all the attributes of the God Card Hephaestus to their correct values
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
     * handles the building phase of the turn, which can be duplicated if the player wants
     */
    @Override
    public void buildPhase() {
        ArrayList<Cell> possibleBuilds = findPossibleBuilds(activeWorker.getPosition());
        Cell buildPosition = client.chooseBuildPosition(possibleBuilds);
        try {
            buildPosition.build();
        } catch (IllegalStateException e) {
            System.out.println("ERROR: illegal build");
        }

        if (buildPosition.getBuildLevel() <= 2) {
            secondBuild = client.chooseYesNo("Do you want to build here again?");
            if (secondBuild) {
                try {
                    buildPosition.build();
                } catch (IllegalStateException e) {
                    System.out.println("ERROR: illegal build");
                }
            }
        }
        gameController.displayBoard();
    }
}
