package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.exceptions.IllegalBuildException;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.view.CellView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Extension of the GodController class that modifies the turn according to Hestia's God Power.
 */
public class HestiaController extends GodController {

    /**
     * Specifies if the player wants to perform one additional build.
     */
    private boolean buildAgain;

    /**
     * HestiaController constructor.
     *
     * @param gameController the GameController for this Game
     */
    public HestiaController(GameController gameController) {
        super(gameController);
    }

    /**
     * Sets all the attributes of the God Card Hestia to their actual values.
     *
     * @return the complete Card
     */
    @Override
    public Card generateCard() {
        Card card = new Card(
                "Hestia",
                "Goddess of Hearth and Home",
                "Your Build: Your Worker may build one additional time. " +
                        "\nThe additional build cannot be on a perimeter space.",
                2,
                false,
                this
        );
        this.card = card;
        return card;
    }

    /**
     * Handles the phases of a turn: moving and building (which is allowed two times, but not building again on a perimeter space).
     *
     * @return "winConditionAchieved" if the player won, "next" if the game goes on, "outOfBuilds" if the Worker can't build
     * @throws IOException               when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException      when the thread handling the communication is waiting and it is interrupted before or during its activity
     * @throws IOExceptionFromController when an IOException from a specific PlayerController occurs
     */
    @Override
    public String runPhases(Worker worker) throws IOException, InterruptedException, IOExceptionFromController {
        buildAgain = false;
        activeWorker = worker;
        startingPosition = worker.getPosition();
        movePhase();
        if (!checkWin().equals("nope")) return checkWin();
        if (findPossibleBuilds(activeWorker.getPosition()).size() == 0) return "outOfBuilds";
        buildPhase();
        buildAgain = true;
        if (findPossibleBuilds(activeWorker.getPosition()).size() > 0) {
            if (client.chooseYesNo("Do you want to build again?"))
                buildPhase();
        }
        return "next";
    }

    /**
     * Handles the building phase of the turn, which may be duplicated: the Player can build two times, but the second build cannot be on a perimeter space.
     * Calls displayBuild with a non-null Card parameter if the Hestia God Power was used.
     *
     * @throws IOException               when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException      when the thread handling the communication is waiting and it is interrupted before or during its activity
     * @throws IOExceptionFromController when an IOException from a specific PlayerController occurs
     */
    @Override
    public void buildPhase() throws IOException, InterruptedException, IOExceptionFromController {
        Card godPower = (buildAgain) ? card : null;
        ArrayList<Cell> possibleBuilds = findPossibleBuilds(activeWorker.getPosition());
        Cell buildPosition = client.chooseBuildPosition(possibleBuilds);
        try {
            buildPosition.build();
        } catch (IllegalBuildException e) {
            gameController.logError(e.getMessage());
        }
        displayBuild(new CellView(buildPosition), godPower);
    }


    /**
     * Returns all the Cells where a worker can build, with the only restrictions due to the general rules (other workers and domes), and allows the second build.
     *
     * @param workerPosition the position of the Worker
     * @return all the Cells where a Worker can build
     */
    @Override
    public ArrayList<Cell> findPossibleBuilds(Cell workerPosition) {
        ArrayList<Cell> neighbors = board.getNeighbors(workerPosition);
        ArrayList<Cell> possibleBuilds = new ArrayList<Cell>();
        for (Cell cell : neighbors) {
            if (buildAgain && (cell.getPosX() == 0 || cell.getPosY() == 0 || cell.getPosX() == 4 || cell.getPosY() == 4))
                continue;
            if (!cell.hasWorker() && !cell.isDomed())
                possibleBuilds.add(cell);
        }
        return findLegalBuilds(workerPosition, possibleBuilds);
    }

}
