package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.exceptions.IllegalBuildException;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Worker;

import java.io.IOException;
import java.util.ArrayList;

public class HestiaController extends GodController {

    private boolean buildAgain;

    /**
     * creates an Hestia controller for this game
     *
     * @param gameController
     */
    public HestiaController(GameController gameController) {
        super(gameController);
    }

    /**
     * sets all the attributes of the God Card Hestia to their correct values
     *
     * @return the complete Card
     */
    @Override
    public Card generateCard() {
        Card card = new Card(
                "Hestia",
                "Goddess of Hearth and Home",
                "Your Build: Your Worker may build one additional time. The additional build cannot be on a perimeter space.",
                2,
                false,
                this
        );
        this.card = card;
        return card;
    }

    /**
     * handles the phases of a turn: moving and building (which is allowed two times, but not building again on a perimetter space)
     *
     * @return "WON" if the player won, "NEXT" if the game goes on
     */
    @Override
    public String runPhases(Worker worker) throws IOException, ClassNotFoundException, IOExceptionFromController {
        buildAgain = false;
        activeWorker = worker;
        startingPosition = worker.getPosition();
        movePhase();
        if (!checkWin().equals("nope")) return checkWin();
        if (findPossibleBuilds(activeWorker.getPosition()).size() == 0) return "outOfBuilds";
        buildPhase();
        if (findPossibleBuilds(activeWorker.getPosition()).size() > 0) {
            buildAgain = client.chooseYesNo("Do you want to build again?");
            if (buildAgain)
                buildPhase();
        }
        return "next";
    }

    public void buildPhase() throws IOException, ClassNotFoundException, IOExceptionFromController {
        Card godPower = (buildAgain) ? card : null;
        ArrayList<Cell> possibleBuilds = findPossibleBuilds(activeWorker.getPosition());
        Cell buildPosition = client.chooseBuildPosition(possibleBuilds);
        try {
            buildPosition.build();
        } catch (IllegalBuildException e) {
            System.out.println(e.getMessage());
        }
        gameController.broadcastBoard("build", godPower);
    }


    /**
     * returns all the cells where a worker can build, with the only restrictions due to the general rules (other workers and domes)
     * and allows a second building
     *
     * @param workerPosition the position of the worker
     * @return all the cells where a worker can build
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
