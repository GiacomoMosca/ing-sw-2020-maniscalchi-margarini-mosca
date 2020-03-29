package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Cards.Deck;
import it.polimi.ingsw.model.GameBoard.Board;
import it.polimi.ingsw.model.Players.Player;
import it.polimi.ingsw.model.TurnData.OpponentModifier;
import it.polimi.ingsw.model.TurnData.Turn;

import java.util.ArrayList;

public class Game {

    private ArrayList<Player> players;
    private final int playerNum;
    private final Board board;
    private final Deck deck;
    private Turn currentTurn;
    private ArrayList<OpponentModifier> activeModifiers;
    private Player winner;

    public Game(Player p1, int num) {
        players = new ArrayList<Player>();
        players.add(p1);
        playerNum = num;
        board = new Board();
        deck = new Deck();
        currentTurn = new Turn(p1);
        activeModifiers = new ArrayList<OpponentModifier>();
        winner = null;
    }

    public Player getNextPlayer(Player currPlayer) throws IllegalArgumentException {
        if (!players.contains(currPlayer)) throw new IllegalArgumentException();
        int i = players.indexOf(currPlayer) + 1;
        if (i >= players.size()) i = 0;
        return players.get(i);
    }

    public void addPlayer(Player player) throws IndexOutOfBoundsException, IllegalArgumentException {
        if (players.size() >= playerNum) throw new IndexOutOfBoundsException();
        if (players.contains(player)) throw new IllegalArgumentException();
        players.add(player);
    }

    public int getPlayerNum() {
        return playerNum;
    }

    public Board getBoard() {
        return board;
    }

    public Deck getDeck() {
        return deck;
    }

    public Turn getCurrentTurn() {
        return currentTurn;
    }

    public void nextTurn(Player currPlayer) throws IllegalArgumentException {
        currentTurn = new Turn(getNextPlayer(currPlayer));
    }

    public ArrayList<OpponentModifier> getActiveModifiers() {
        return new ArrayList<OpponentModifier>(activeModifiers);
    }

    public void addModifier(OpponentModifier modifier) {
        activeModifiers.add(modifier);
    }

    public void removeModifier(OpponentModifier modifier) {
        activeModifiers.remove(modifier);
    }

    public Player getWinner() {
        return winner;
    }

    public boolean hasWinner() {
        return winner != null;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

}
