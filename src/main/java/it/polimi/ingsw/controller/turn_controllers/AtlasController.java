package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;

import java.util.ArrayList;

public class AtlasController extends GodController {

    public AtlasController(GameController gameController) {
        super(gameController);
    }

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

    @Override
    protected void buildPhase() {
        ArrayList<Cell> possibleBuilds = findPossibleBuilds(activeWorker.getPosition());
        Cell buildPosition = client.chooseBuildPosition(possibleBuilds);
        try {
            if(client.chooseYesNo("Do you want to build a dome?")) // Atlas allows the worker to build a dome at any level
                buildPosition.buildDome();
            else
                buildPosition.build();
        } catch (IllegalStateException e) {
            System.out.println("ERROR: illegal build");
        }
    }
}
