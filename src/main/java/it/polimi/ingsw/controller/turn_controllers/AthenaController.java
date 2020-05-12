package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.exceptions.IllegalMoveException;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;

import java.io.IOException;
import java.util.ArrayList;

public class AthenaController extends GodController {

    /**
     * creates an Athena controller for this game
     *
     * @param gameController
     */
    public AthenaController(GameController gameController) {
        super(gameController);
    }

    /**
     * sets all the attributes of the God Card Athena to their correct values
     *
     * @return the complete Card
     */
    @Override
    public Card generateCard() {
        Card card = new Card(
                "Athena",
                "Goddess of Wisdom",
                "Opponent’s Turn: If one of your Workers moved up on your last turn, opponent Workers cannot move up this turn.",
                1,
                false,
                this
        );
        this.card = card;
        return card;
    }

    /**
     * handles the moving phase of the turn, and adds a Modifier if the worker moved up on this turn
     */
    @Override
    public void movePhase() throws IOException, ClassNotFoundException, IOExceptionFromController {
        Card godPower = null;
        ArrayList<Cell> possibleMoves = findPossibleMoves(activeWorker.getPosition());
        Cell oldPosition = activeWorker.getPosition();
        Cell movePosition = client.chooseMovePosition(possibleMoves);
        try {
            activeWorker.move(movePosition);
        } catch (IllegalMoveException e) {
            System.out.println("ERROR: illegal move");
        }
        // + limit opponent's movements if worker moved up
        if (movePosition.getBuildLevel() > oldPosition.getBuildLevel()) {
            godPower = card;
            game.addModifier(card);
        }
        //
        gameController.broadcastBoard("move", godPower);
    }

    /**
     * gets a list containing all the cells where an opponent worker can move and creates another list, removing from
     * the previous all the cells that requires to move up
     *
     * @param workerPosition the position of the worker
     * @param possibleMoves  all the cells where the worker can move, considering only the game restrictions
     * @return all the cells where the worker is actually able to build
     */
    @Override
    public ArrayList<Cell> limitMoves(Cell workerPosition, ArrayList<Cell> possibleMoves) {
        // + opponents can't move up if modifier is active
        ArrayList<Cell> limitedMoves = new ArrayList<Cell>();
        for (Cell cell : possibleMoves) {
            if (cell.getBuildLevel() <= workerPosition.getBuildLevel())
                limitedMoves.add(cell);
        }
        return limitedMoves;
        //
    }

}
