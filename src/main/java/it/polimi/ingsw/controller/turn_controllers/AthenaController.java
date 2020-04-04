package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;

import java.util.ArrayList;

public class AthenaController extends GodController {

    public AthenaController(GameController gameController) {
        super(gameController);
    }

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

    @Override
    protected void movePhase() {
        ArrayList<Cell> possibleMoves = findPossibleMoves(activeWorker.getPosition());
        Cell oldPosition = activeWorker.getPosition();
        Cell movePosition = client.chooseMovePosition(possibleMoves);
        try {
            activeWorker.move(movePosition);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: illegal move");
        }
        // + limit opponent's movements if worker moved up
        if (movePosition.getBuildLevel() > oldPosition.getBuildLevel()) {
            game.addModifier(card);
        }
        //
    }

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
