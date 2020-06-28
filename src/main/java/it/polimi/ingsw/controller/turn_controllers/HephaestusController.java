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
     * HephaestusController constructor.
     *
     * @param gameController the GameController for this game
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
                "Your Build: Your Worker may build one additional block (not dome) " +
                        "\non top of your first block.",
                1,
                false,
                this
        );
        this.card = card;
        return card;
    }

    /**
     * Handles the building phase of the turn, which may be duplicated: the Player can build two times on the same block (not a dome).
     * Calls displayBuild with a non-null Card parameter if the Hephaestus God Power was used.
     *
     * @throws IOException               when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException      when the thread handling the communication is waiting and it is interrupted before or during its activity
     * @throws IOExceptionFromController when an IOException from a specific PlayerController occurs
     */
    @Override
    public void buildPhase() throws IOException, InterruptedException, IOExceptionFromController {
        Card godPower = null;
        ArrayList<Cell> possibleBuilds = findPossibleBuilds(activeWorker.getPosition());
        Cell buildPosition = client.chooseBuildPosition(possibleBuilds);
        try {
            buildPosition.build();
        } catch (IllegalBuildException e) {
            System.out.println(e.getMessage());
        }
        if (buildPosition.getBuildLevel() <= 2) {
            if (client.chooseYesNo("Do you want to build here twice?")) {
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
