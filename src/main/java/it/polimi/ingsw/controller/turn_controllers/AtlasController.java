package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.exceptions.IllegalBuildException;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.view.CellView;

import java.io.IOException;
import java.util.ArrayList;

public class AtlasController extends GodController {

    /**
     * Creates an Atlas controller for this game.
     *
     * @param gameController the Game Controller for this game
     */
    public AtlasController(GameController gameController) {
        super(gameController);
    }

    /**
     * Sets all the attributes of the God Card Atlas to their correct values.
     *
     * @return a complete Card
     */
    @Override
    public Card generateCard() {
        Card card = new Card(
                "Atlas",
                "Titan Shouldering the Heavens",
                "Your Build: Your Worker may build a dome at any level " +
                        "\nincluding the ground.",
                1,
                false,
                this
        );
        this.card = card;
        return card;
    }

    /**
     * Handles the building phase of the turn, and allows building domes at any level.
     */
    @Override
    public void buildPhase() throws IOException, InterruptedException, IOExceptionFromController {
        Card godPower = null;
        ArrayList<Cell> possibleBuilds = findPossibleBuilds(activeWorker.getPosition());
        Cell buildPosition = client.chooseBuildPosition(possibleBuilds);
        try {
            if (client.chooseYesNo("Do you want to build a dome?")) {
                godPower = card;
                buildPosition.buildDome();
            } else {
                buildPosition.build();
            }
        } catch (IllegalBuildException e) {
            System.out.println(e.getMessage());
        }
        displayBuild(new CellView(buildPosition), godPower);
    }

}
