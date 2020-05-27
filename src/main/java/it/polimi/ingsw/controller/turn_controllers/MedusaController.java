package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.exceptions.IllegalBuildException;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.view.CellView;

import java.io.IOException;

public class MedusaController extends GodController {

    /**
     * Creates a Medusa controller for this game.
     *
     * @param gameController
     */
    public MedusaController(GameController gameController) {
        super(gameController);
    }

    /**
     * Sets all the attributes of the card to their actual values.
     *
     * @return a complete Card
     */
    @Override
    public Card generateCard() {
        Card card = new Card(
                "Medusa",
                "Petrifying Gorgon",
                "End of Your Turn: If any of your opponent’s Workers occupy lower neighboring spaces, replace them all with blocks and remove them from the game.",
                2,
                false,
                this
        );
        this.card = card;
        return card;
    }

    /**
     * Handles the basic phases of a turn: moving and building.
     * At the end of the turn, if opponent workers occupy lower neighboring spaces, replaces them all with blocks and
     * removes them from the game.
     *
     * @param worker the active worker during this turn
     * @return "WON" if the player won, "NEXT" if the game goes on
     */
    @Override
    public String runPhases(Worker worker) throws IOException, ClassNotFoundException, IOExceptionFromController {
        activeWorker = worker;
        startingPosition = worker.getPosition();
        movePhase();
        if (!checkWin().equals("nope")) return checkWin();
        if (findPossibleBuilds(activeWorker.getPosition()).size() == 0) return "outOfBuilds";
        buildPhase();
        for (Cell cell : board.getNeighbors(activeWorker.getPosition())) {
            if (cell.hasWorker() && !player.getWorkers().contains(cell.getWorker()) && cell.getBuildLevel() < activeWorker.getPosition().getBuildLevel()) {
                try {
                    cell.build();
                } catch (IllegalBuildException e) {
                    System.out.println(e.getMessage());
                }
                CellView cellView = new CellView(cell);
                cell.getWorker().getOwner().removeWorker(cell.getWorker());
                displayBuild(cellView, card);
            }
        }
        return "next";
    }

}
