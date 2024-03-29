package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.exceptions.IllegalMoveException;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.view.CellView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Extension of the GodController class that modifies the turn according to Triton's God Power.
 */
public class TritonController extends GodController {

    /**
     * TritonController constructor.
     *
     * @param gameController the GameController for this Game.
     */
    public TritonController(GameController gameController) {
        super(gameController);
    }

    /**
     * Sets all the attributes of the God Card Triton to their actual values.
     *
     * @return the complete Card
     */
    @Override
    public Card generateCard() {
        Card card = new Card(
                "Triton",
                "God of the Waves",
                "Your Move: Each time your Worker moves onto a perimeter space " +
                        "\n(ground or block), it may immediately move again.",
                2,
                false,
                this
        );
        this.card = card;
        return card;
    }

    /**
     * Handles the moving phase of the turn, allowing moving a second time whether the first one was onto a perimeter space.
     * Calls displayMove with a non-null Card parameter if the Triton God Power was used.
     *
     * @throws IOException               when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException      when the thread handling the communication is waiting and it is interrupted before or during its activity
     * @throws IOExceptionFromController when an IOException from a specific PlayerController occurs
     */
    @Override
    void movePhase() throws IOException, InterruptedException, IOExceptionFromController {
        ArrayList<Cell> possibleMoves = findPossibleMoves(activeWorker.getPosition());
        Cell movePosition = client.chooseMovePosition(possibleMoves);
        CellView startView = new CellView(activeWorker.getPosition());
        CellView endView = new CellView(movePosition);
        try {
            activeWorker.move(movePosition);
        } catch (IllegalMoveException e) {
            gameController.logError(e.getMessage());
        }
        displayMove(startView, endView, null);

        while (movePosition.getPosX() == 0 || movePosition.getPosY() == 0 || movePosition.getPosY() == 4 || movePosition.getPosX() == 4) {
            if (client.chooseYesNo("Do you want to move again?")) {
                possibleMoves = findPossibleMoves(activeWorker.getPosition());
                movePosition = client.chooseMovePosition(possibleMoves);
                startView = new CellView(activeWorker.getPosition());
                endView = new CellView(movePosition);
                try {
                    activeWorker.move(movePosition);
                } catch (IllegalMoveException e) {
                    gameController.logError(e.getMessage());
                    return;
                }
            } else return;
            displayMove(startView, endView, card);
        }
    }

}
