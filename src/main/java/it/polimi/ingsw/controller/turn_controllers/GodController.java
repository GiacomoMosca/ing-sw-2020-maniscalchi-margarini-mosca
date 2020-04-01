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
    private Worker activeWorker;

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

    public String playTurn() {
        ArrayList<Worker> playableWorkers = new ArrayList<Worker>();
        for (Worker worker : player.getWorkers()) {
            if (checkWorker(worker)) playableWorkers.add(worker);
        }
        if (playableWorkers.size() == 0) return "LOST";
        if (playableWorkers.size() == 1) {
            activeWorker = playableWorkers.get(0);
        } else {
            activeWorker = client.selectWorker(playableWorkers);
        }
        return runPhases();
    }

    private boolean checkWorker(Worker worker) {
        return findPossibleMoves(worker).size() > 0;
    }

    private String runPhases() {
        movePhase();
        if (checkWin()) return "WON";
        buildPhase();
        return "NEXT";
    }

    private void movePhase() {
        ArrayList<Cell> possibleMoves = findPossibleMoves(activeWorker);
        Cell movePosition = client.selectPosition(possibleMoves);
        try {
            activeWorker.move(movePosition);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: illegal move");
        }
    }

    private void buildPhase() throws NullPointerException {
        ArrayList<Cell> possibleBuilds = findPossibleBuilds(activeWorker);
        Cell buildPosition = client.selectPosition(possibleBuilds);
        try {
            buildPosition.build();
        } catch (IllegalStateException e) {
            System.out.println("ERROR: illegal build");
        }
    }

    boolean checkWin() {
        return activeWorker.getPosition().getBuildLevel() == 3;
    }

    ArrayList<Cell> findPossibleMoves(Worker worker) {
        ArrayList<Cell> neighbors = board.getNeighbors(worker.getPosition());
        ArrayList<Cell> possibleMoves = new ArrayList<Cell>();
        for (Cell cell : neighbors) {
            if (!cell.hasWorker() && !cell.isDomed() && (cell.getBuildLevel() <= worker.getPosition().getBuildLevel() + 1))
                possibleMoves.add(cell);
        }
        for (OpponentModifier modifier : game.getActiveModifiers()) {
            if (modifier.getPlayer() == player) continue;
            possibleMoves = modifier.getGodCard().getController().limitMoves(worker, possibleMoves);
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
            if (modifier.getPlayer() == player) continue;
            possibleBuilds = modifier.getGodCard().getController().limitBuilds(worker, possibleBuilds);
        }
        return possibleBuilds;
    }

    public ArrayList<Cell> limitMoves(Worker worker, ArrayList<Cell> possibleMoves) {
        return possibleMoves;
    }

    public ArrayList<Cell> limitBuilds(Worker worker, ArrayList<Cell> possibleBuilds) {
        return possibleBuilds;
    }

}
