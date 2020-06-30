package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.exceptions.IllegalBuildException;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.view.CellView;

import java.io.IOException;

/**
 * Extension of the GodController class that modifies the turn according to Medusa's God Power.
 */
public class MedusaController extends GodController {

    /**
     * MedusaController constructor.
     *
     * @param gameController the GameController for this Game
     */
    public MedusaController(GameController gameController) {
        super(gameController);
    }

    /**
     * Sets all the attributes of the God Card Medusa to their actual values.
     *
     * @return the complete Card
     */
    @Override
    public Card generateCard() {
        Card card = new Card(
                "Medusa",
                "Petrifying Gorgon",
                "End of Your Turn: If any of your opponent’s Workers occupy " +
                        "\nlower neighboring spaces, replace them all with blocks and " +
                        "\nremove them from the game.",
                2,
                false,
                this
        );
        this.card = card;
        return card;
    }

    /**
     * Handles the phases of a turn: moving and building.
     * At the end of the turn, if opponent Workers occupy lower neighboring spaces, replaces them all with blocks and removes them from the game.
     * Calls displayBuild with a non-null Card parameter if the Medusa God Power was used.
     *
     * @param worker the active Worker
     * @return "winConditionAchieved" if the player won, "next" if the game goes on, "outOfBuilds" if the player can't build
     * @throws IOException               when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException      when the thread handling the communication is waiting and it is interrupted before or during its activity
     * @throws IOExceptionFromController when an IOException from a specific PlayerController occurs
     */
    @Override
    public String runPhases(Worker worker) throws IOException, InterruptedException, IOExceptionFromController {
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
                    gameController.logError(e.getMessage());
                }
                CellView cellView = new CellView(cell);
                cell.getWorker().getOwner().removeWorker(cell.getWorker());
                displayBuild(cellView, card);
            }
        }
        return "next";
    }

}
