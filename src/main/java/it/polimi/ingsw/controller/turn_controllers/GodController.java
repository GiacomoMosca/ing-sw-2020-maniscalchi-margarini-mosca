package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.game_board.Board;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.model.OpponentModifier;

import java.util.ArrayList;

public abstract class GodController {

    private GameController gameController;
    private Game game;
    private Board board;
    private Player player;
    private PlayerClient client;

    public GodController(GameController gameController) {
        this.gameController = gameController;
        this.game = gameController.getGame();
        this.board = this.game.getBoard();
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerClient getClient() {
        return client;
    }

    public void setPlayer(Player player, PlayerClient client) {
        this.player = player;
        this.client = client;
    }

    public boolean playTurn() {
        Worker worker = client.selectWorker();
        ArrayList<Cell> possibleMoves = findPossibleMoves(worker);
        Cell movePosition = client.selectPosition(possibleMoves);
        worker.move(movePosition);
        if (checkWin(worker)) return true;
        ArrayList<Cell> possibleBuilds = findPossibleBuilds(worker);
        Cell buildPosition = client.selectPosition(possibleBuilds);
        buildPosition.build();
        return false;
    }

    ArrayList<Cell> findPossibleMoves(Worker worker) {
        ArrayList<Cell> neighbors = board.getNeighbors(worker.getPosition());
        ArrayList<Cell> possibleMoves = new ArrayList<Cell>();
        for (Cell cell : neighbors) {
            if (!cell.hasWorker() && !cell.isDomed() && (cell.getBuildLevel() <= worker.getPosition().getBuildLevel() + 1))
                possibleMoves.add(cell);
        }
        for (OpponentModifier modifier : game.getActiveModifiers()) {
            possibleMoves = modifier.getGodCard().getController().limitMoves(possibleMoves);
        }
        return possibleMoves;
    }

    ArrayList<Cell> findPossibleBuilds(Worker worker) {
        ArrayList<Cell> neighbors = board.getNeighbors(worker.getPosition());
        ArrayList<Cell> possibleBuilds = new ArrayList<Cell>();
        for (Cell cell : neighbors) {
            if (!cell.hasWorker() && !cell.isDomed())
                possibleBuilds.add(cell);
        }
        for (OpponentModifier modifier : game.getActiveModifiers()) {
            possibleBuilds = modifier.getGodCard().getController().limitBuilds(possibleBuilds);
        }
        return possibleBuilds;
    }

    boolean checkWin(Worker worker) {
        if (worker.getPosition().getBuildLevel() == 3) return true;
    }

    public ArrayList<Cell> limitMoves(ArrayList<Cell> possibleMoves) {
        return possibleMoves;
    }

    public ArrayList<Cell> limitBuilds(ArrayList<Cell> possibleBuilds) {
        return possibleBuilds;
    }

}
