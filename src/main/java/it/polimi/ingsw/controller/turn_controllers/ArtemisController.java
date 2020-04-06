package it.polimi.ingsw.controller.turn_controllers;


import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Worker;

import java.util.ArrayList;

public class ArtemisController extends GodController{

    private Cell beginningCell;
    private boolean secondMove;

    /**
     * creates an ArtemisController for this Game
     *
     * @param gameController
     */
    public ArtemisController(GameController gameController) {
        super(gameController);
    }

    /**
     * sets all the attributes of the God Card Artemis to their correct values
     *
     * @return a complete Card
     */
    @Override
    public Card generateCard() {
        Card card = new Card(
                "Artemis",
                "Goddes of the Hunt",
                "Your Move: Your Worker may move one additional time, but not back to the space it started on.",
                1,
                false,
                this
        );
        this.card = card;
        return card;
    }


    /**
     * handles the phases of a turn: moving (which is allowed two times, but not going back to the first cell) and building
     *
     * @return "WON" if the player won, "NEXT" if the game goes on
     */
    @Override
    public String runPhases(Worker worker) {
        activeWorker = worker;
        startingPosition = worker.getPosition();
        secondMove=false;
        beginningCell=activeWorker.getPosition();
        movePhase();
        if (checkWin()) return "WON";
        secondMove=client.chooseYesNo("Do you want to move again?");
        if (secondMove){
            movePhase();
            if (checkWin()) return "WON";
        }
        buildPhase();
        return "NEXT";
    }


    /**
     * returns all the cells where a worker can move, with the only restrictions due to the general rules (other workers, domes, building levels)
     * and ensures that, if the player uses a second move, his worker won't move back to the cell it started from
     *
     * @param workerPosition the position of the worker
     * @return all the cells where a worker can move
     */
    @Override
    protected ArrayList<Cell> findPossibleMoves(Cell workerPosition) {
        ArrayList<Cell> neighbors = board.getNeighbors(workerPosition);
        ArrayList<Cell> possibleMoves = new ArrayList<Cell>();
        for (Cell cell : neighbors) {
            if (secondMove && cell.equals(beginningCell))
                continue;
            if (!cell.hasWorker() && !cell.isDomed() && (cell.getBuildLevel() <= workerPosition.getBuildLevel() + 1))
                possibleMoves.add(cell);
        }
        return findLegalMoves(workerPosition, possibleMoves);
    }

}