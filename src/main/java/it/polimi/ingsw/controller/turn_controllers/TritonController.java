package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;

import java.io.IOException;
import java.util.ArrayList;

public class TritonController extends GodController{

    /**
     * creates a Triton controller for this game
     *
     * @param gameController
     */
    public TritonController(GameController gameController) {
        super(gameController);
    }

    /**
     * sets all the attributes of the God Card Triton to their correct values
     *
     * @return a complete Card
     */
    @Override
    public Card generateCard() {
        Card card = new Card(
                "Triton",
                "God of the Waves",
                "Your Move: Each time your Worker moves onto a perimeter space (ground or block), it may immediately move again.",
                2,
                false,
                this
        );
        this.card = card;
        return card;
    }

    /**
     * handles the moving phase of the turn, allowing moving a second time
     * whether the first one was onto a perimeter space
     *
     */
    @Override
    public void movePhase() throws IOException, ClassNotFoundException {
        ArrayList<Cell> possibleMoves = findPossibleMoves(activeWorker.getPosition());
        Cell movePosition = client.chooseMovePosition(possibleMoves);
        try {
            activeWorker.move(movePosition);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: illegal move");
        }
        gameController.displayBoard();

        while (movePosition.getPosX()==0 || movePosition.getPosY()==0 || movePosition.getPosY()==4 || movePosition.getPosX()==4){
            if (client.chooseYesNo("Do you want to move again?")){
                movePosition = client.chooseMovePosition(possibleMoves);
                try {
                    activeWorker.move(movePosition);
                } catch (IllegalArgumentException e) {
                    System.out.println("ERROR: illegal move");
                    return;
                }
            }
            else return;
            gameController.displayBoard();
        }
    }
}
