package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Worker;

import java.util.ArrayList;

public class MinotaurController extends GodController{

    public MinotaurController(GameController gameController) {
        super(gameController);
    }

    @Override
    public Card generateCard() {
        Card card = new Card(
                "Minotaur",
                "Bull-headed Monster",
                "Your Move: Your Worker may move into an opponent Worker’s space (using normal movement rules), if the next space in the same direction is unoccupied. Their Worker is forced into that space (regardless of its level).",
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
        Cell movePosition = client.chooseMovePosition(possibleMoves);
        // + allow pushing away opponents
        if (movePosition.hasWorker()) {
            Worker pushedWorker = movePosition.getWorker();
            Cell nextCell;
            int nextX = movePosition.getPosX() + (movePosition.getPosX() - activeWorker.getPosition().getPosX());
            int nextY = movePosition.getPosY() + (movePosition.getPosY() - activeWorker.getPosition().getPosY());
            try {
                nextCell = board.getCell(nextX, nextY);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("ERROR: out of bounds");
                return;
            }
            try {
                pushedWorker.move(nextCell);
            } catch (IllegalArgumentException e) {
                System.out.println("ERROR: illegal move");
                return;
            }
            //
        }
        try {
            activeWorker.move(movePosition);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: illegal move");
        }
    }

    @Override
    protected ArrayList<Cell> findPossibleMoves(Cell workerPosition) {
        ArrayList<Cell> neighbors = board.getNeighbors(workerPosition);
        ArrayList<Cell> possibleMoves = new ArrayList<Cell>();
        for (Cell cell : neighbors) {
            // + allow movement to cells occupied by opponents, if they can be pushed away
            Cell nextCell;
            int nextX = cell.getPosX() + (cell.getPosX() - workerPosition.getPosX());
            int nextY = cell.getPosY() + (cell.getPosY() - workerPosition.getPosY());
            try {
                nextCell = board.getCell(nextX, nextY);
            } catch (ArrayIndexOutOfBoundsException e) {
                nextCell = null;
            }
            if ((!cell.hasWorker() || (nextCell != null && !nextCell.hasWorker() && !nextCell.isDomed())) &&
                    !cell.isDomed() && (cell.getBuildLevel() <= workerPosition.getBuildLevel() + 1))
                possibleMoves.add(cell);
            //
        }
        return findLegalMoves(workerPosition, possibleMoves);
    }

}