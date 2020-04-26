package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;

import java.io.IOException;
import java.util.ArrayList;

public class AtlasController extends GodController {

    /**
     * creates an Atlas controller for this game
     *
     * @param gameController
     */
    public AtlasController(GameController gameController) {
        super(gameController);
    }

    /**
     * sets all the attributes of the God Card Atlas to their correct values
     *
     * @return a complete Card
     */
    @Override
    public Card generateCard() {
        Card card = new Card(
                "Atlas",
                "Titan Shouldering the Heavens",
                "Your Build: Your Worker may build a dome at any level including the ground.",
                1,
                false,
                this
        );
        this.card = card;
        return card;
    }

    /**
     * handles the building phase of the turn, and allows building domes at any level
     *
     */
    @Override
    public void buildPhase() throws IOException, ClassNotFoundException {
        ArrayList<Cell> possibleBuilds = findPossibleBuilds(activeWorker.getPosition());
        Cell buildPosition = client.chooseBuildPosition(possibleBuilds);
        try {
            if(client.chooseYesNo("Do you want to build a dome?"))
                buildPosition.buildDome();
            else
                buildPosition.build();
        } catch (IllegalStateException | ClassNotFoundException e) {
            System.out.println("ERROR: illegal build");
        }
        gameController.displayBoard();
    }
}
