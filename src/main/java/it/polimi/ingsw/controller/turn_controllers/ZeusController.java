package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.exceptions.IllegalBuildException;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.view.CellView;

import java.io.IOException;
import java.util.ArrayList;

public class ZeusController extends GodController {

    /**
     * ZeusController constructor.
     *
     * @param gameController the GameController for this Game
     */
    public ZeusController(GameController gameController) {
        super(gameController);
    }

    /**
     * Sets all the attributes of the God Card Zeus to their actual values.
     *
     * @return the complete Card
     */
    @Override
    public Card generateCard() {
        Card card = new Card(
                "Zeus",
                "God of the Sky",
                "Your Build: Your Worker may build under itself in its current space, " +
                        "\nforcing it up one level. You do not win by forcing yourself up " +
                        "\nto the third level.",
                2,
                false,
                this
        );
        this.card = card;
        return card;
    }

    /**
     * Handles the building phase of a turn, allowing the Worker to build under itself.
     * Calls displayBuild with a non-null Card parameter if the Zeus God Power was used.
     *
     * @throws IOException               when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException      when the thread handling the communication is waiting and it is interrupted before or during its activity
     * @throws IOExceptionFromController when an IOException from a specific PlayerController occurs
     */
    public void buildPhase() throws IOException, InterruptedException, IOExceptionFromController {
        ArrayList<Cell> possibleBuilds = findPossibleBuilds(activeWorker.getPosition());
        Cell buildPosition = client.chooseBuildPosition(possibleBuilds);
        try {
            buildPosition.build();
        } catch (IllegalBuildException e) {
            System.out.println(e.getMessage());
        }
        Card godPower = (buildPosition.getPosX() == activeWorker.getPosition().getPosX() && buildPosition.getPosY() == activeWorker.getPosition().getPosY()) ? card : null;
        displayBuild(new CellView(buildPosition), godPower);
    }

    /**
     * Returns all the Cells where a Worker can build with the only restrictions due to the general rules (other workers, domes, building levels), including its current position.
     *
     * @param workerPosition the position of the Worker
     * @return an ArrayList containing the cells where a Worker can build
     */
    @Override
    public ArrayList<Cell> findPossibleBuilds(Cell workerPosition) {
        ArrayList<Cell> neighbors = board.getNeighbors(workerPosition);
        ArrayList<Cell> possibleBuilds = new ArrayList<Cell>();
        for (Cell cell : neighbors) {
            if (!cell.hasWorker() && !cell.isDomed())
                possibleBuilds.add(cell);
        }
        if (workerPosition.getBuildLevel() < 3) possibleBuilds.add(workerPosition);
        return findLegalBuilds(workerPosition, possibleBuilds);
    }

}
