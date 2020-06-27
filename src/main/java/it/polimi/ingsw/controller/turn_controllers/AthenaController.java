package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.exceptions.IllegalMoveException;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.view.CellView;

import java.io.IOException;
import java.util.ArrayList;

public class AthenaController extends GodController {

    /**
     * AthenaController constructor.
     *
     * @param gameController the GameController for this game
     */
    public AthenaController(GameController gameController) {
        super(gameController);
    }

    /**
     * Sets all the attributes of the God Card Athena to their actual values.
     *
     * @return the complete Card
     */
    @Override
    public Card generateCard() {
        Card card = new Card(
                "Athena",
                "Goddess of Wisdom",
                "Opponent’s Turn: If one of your Workers moved up on your " +
                        "\nlast turn, opponent Workers cannot move up this turn.",
                1,
                false,
                this
        );
        this.card = card;
        return card;
    }

    /**
     * Handles the moving phase of the turn, adding a Modifier if the Worker moved up on this turn.
     * Calls displayMove with a non-null Card parameter if the Athena God Power was used.
     *
     * @throws IOException               when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException      when the thread handling the communication is waiting and it is interrupted before or during its activity
     * @throws IOExceptionFromController when an IOException from a specific PlayerController occurs
     */
    @Override
    public void movePhase() throws IOException, InterruptedException, IOExceptionFromController {
        Card godPower = null;
        ArrayList<Cell> possibleMoves = findPossibleMoves(activeWorker.getPosition());
        Cell oldPosition = activeWorker.getPosition();
        Cell movePosition = client.chooseMovePosition(possibleMoves);
        CellView startView = new CellView(oldPosition);
        CellView endView = new CellView(movePosition);
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
        displayMove(startView, endView, godPower);
    }

    /**
     * Applies Athena God Power. Gets an ArrayList containing all the Cells where an opponent Worker can move and creates another ArrayList, removing from the first one all the Cells that require a move up.
     *
     * @param workerPosition the position of the Worker
     * @param possibleMoves  all the Cells where the Worker can move, considering only the game restrictions
     * @return an ArrayList containing all the Cells where the Worker is actually able to move
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
