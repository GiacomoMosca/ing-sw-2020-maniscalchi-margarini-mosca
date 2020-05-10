package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Worker;

import java.io.IOException;

public class MedusaController extends GodController {

    /**
     * creates a Medusa controller for this game
     *
     * @param gameController
     */
    public MedusaController(GameController gameController) {
        super(gameController);
    }

    /**
     * sets all the attributes of the card to their correct values
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
     * handles the basic phases of a turn: moving and building.
     * At the end of the turn if any opponent’s Workers occupy lower neighboring spaces,
     * replace them all with blocks and remove them from the game.
     *
     * @param worker
     * @return "WON" if the player won, "NEXT" if the game goes on
     */
    @Override
    public String runPhases(Worker worker) throws IOException, ClassNotFoundException, IOExceptionFromController {
        activeWorker = worker;
        startingPosition = worker.getPosition();
        movePhase();
        if (checkWin()) return "WON";
        buildPhase();
        for (Cell cell : board.getNeighbors(activeWorker.getPosition())) {
            if (cell.hasWorker()) {
                if (!player.getWorkers().contains(cell.getWorker())) {
                    if (cell.getBuildLevel() < activeWorker.getPosition().getBuildLevel()) {
                        cell.getWorker().getOwner().removeWorker(cell.getWorker());
                        cell.build();
                        gameController.broadcastBoard("removeWorker", card);
                    }
                }
            }
        }
        return "NEXT";
    }
}
