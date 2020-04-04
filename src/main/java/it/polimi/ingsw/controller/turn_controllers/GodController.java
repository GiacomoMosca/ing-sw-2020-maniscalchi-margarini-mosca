package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Board;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.view.PlayerInterface;

import java.util.ArrayList;

public abstract class GodController {

    protected final GameController gameController;
    protected final Game game;
    protected final Board board;
    protected Card card;
    protected Player player;
    protected PlayerInterface client;
    protected Worker activeWorker;

    public GodController(GameController gameController) {
        this.gameController = gameController;
        this.game = gameController.getGame();
        this.board = this.game.getBoard();
    }

    public abstract Card generateCard(); /* {
        Card card = new Card(
                "god",
                "title",
                "description",
                set (1: Simple, 2: Advanced),
                alwaysActiveModifier,
                this
        );
        this.card = card;
        return card;
    } */

    public Player getPlayer() {
        return player;
    }

    public PlayerInterface getClient() {
        return client;
    }

    public void setPlayer(Player player, PlayerInterface client) {
        this.player = player;
        this.client = client;
    }

    public String playTurn() {
        ArrayList<Worker> playableWorkers = new ArrayList<Worker>();
        for (Worker worker : player.getWorkers()) {
            if (canPlay(worker)) playableWorkers.add(worker);
        }
        if (playableWorkers.size() == 0) return "LOST";
        if (playableWorkers.size() == 1) {
            activeWorker = playableWorkers.get(0);
        } else {
            activeWorker = client.chooseWorker(playableWorkers);
        }
        return runPhases();
    }

    protected boolean canPlay(Worker worker) {
        return findPossibleMoves(worker.getPosition()).size() > 0;
    }

    protected String runPhases() {
        movePhase();
        if (checkWin()) return "WON";
        buildPhase();
        return "NEXT";
    }

    protected void movePhase() {
        ArrayList<Cell> possibleMoves = findPossibleMoves(activeWorker.getPosition());
        Cell movePosition = client.chooseMovePosition(possibleMoves);
        try {
            activeWorker.move(movePosition);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: illegal move");
        }
    }

    protected void buildPhase() {
        ArrayList<Cell> possibleBuilds = findPossibleBuilds(activeWorker.getPosition());
        Cell buildPosition = client.chooseBuildPosition(possibleBuilds);
        try {
            buildPosition.build();
        } catch (IllegalStateException e) {
            System.out.println("ERROR: illegal build");
        }
    }

    protected boolean checkWin() {
        return activeWorker.getPosition().getBuildLevel() == 3;
    }

    protected ArrayList<Cell> findPossibleMoves(Cell workerPosition) {
        ArrayList<Cell> neighbors = board.getNeighbors(workerPosition);
        ArrayList<Cell> possibleMoves = new ArrayList<Cell>();
        for (Cell cell : neighbors) {
            if (!cell.hasWorker() && !cell.isDomed() && (cell.getBuildLevel() <= workerPosition.getBuildLevel() + 1))
                possibleMoves.add(cell);
        }
        return findLegalMoves(workerPosition, possibleMoves);
    }

    protected ArrayList<Cell> findLegalMoves(Cell workerPosition, ArrayList<Cell> possibleMoves) {
        for (Card modifier : game.getActiveModifiers()) {
            if (modifier.getController().getPlayer() == player) continue;
            possibleMoves = modifier.getController().limitMoves(workerPosition, possibleMoves);
        }
        return possibleMoves;
    }

    protected ArrayList<Cell> findPossibleBuilds(Cell workerPosition) {
        ArrayList<Cell> neighbors = board.getNeighbors(workerPosition);
        ArrayList<Cell> possibleBuilds = new ArrayList<Cell>();
        for (Cell cell : neighbors) {
            if (!cell.hasWorker() && !cell.isDomed())
                possibleBuilds.add(cell);
        }
        return findLegalBuilds(workerPosition, possibleBuilds);
    }

    protected ArrayList<Cell> findLegalBuilds(Cell workerPosition, ArrayList<Cell> possibleBuilds) {
        for (Card modifier : game.getActiveModifiers()) {
            if (modifier.getController().getPlayer() == player) continue;
            possibleBuilds = modifier.getController().limitBuilds(workerPosition, possibleBuilds);
        }
        return possibleBuilds;
    }

    public ArrayList<Cell> limitMoves(Cell workerPosition, ArrayList<Cell> possibleMoves) {
        return possibleMoves;
    }

    public ArrayList<Cell> limitBuilds(Cell workerPosition, ArrayList<Cell> possibleBuilds) {
        return possibleBuilds;
    }

}
