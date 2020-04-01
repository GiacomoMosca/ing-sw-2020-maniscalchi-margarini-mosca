package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.game_board.Board;
import it.polimi.ingsw.model.players.Player;

import java.util.ArrayList;

/**
 * represents the current state of the game
 */
public class Game {

    private ArrayList<Player> players;
    private final int playerNum;
    private final Board board;
    private final Deck deck;
    private Player activePlayer;
    private ArrayList<OpponentModifier> activeModifiers;
    private Player winner;

    /**
     * prepares a new Game
     *
     * @param p1 the player who first sign up for the game
     * @param num the number of players
     */
    public Game(Player p1, int num) {
        players = new ArrayList<Player>();
        players.add(p1);
        playerNum = num;
        board = new Board();
        deck = new Deck();
        activePlayer = p1;
        activeModifiers = new ArrayList<OpponentModifier>();
        winner = null;
    }

    /**
     * returns the next player
     *
     * @param currPlayer the player currently playing
     * @return the player who will play next
     * @throws IllegalArgumentException when the argument is not a player of the current Game
     */
    public Player getNextPlayer(Player currPlayer) throws IllegalArgumentException {
        if (!players.contains(currPlayer)) throw new IllegalArgumentException();
        int i = players.indexOf(currPlayer) + 1;
        if (i >= players.size()) i = 0;
        return players.get(i);
    }

    /**
     * adds a player to the current Game
     *
     * @param player the new player
     * @throws IndexOutOfBoundsException when trying to add more players than allowed for the current Game
     * @throws IllegalArgumentException when trying to add a player who already joined the current Game
     */
    public void addPlayer(Player player) throws IndexOutOfBoundsException, IllegalArgumentException {
        if (players.size() >= playerNum) throw new IndexOutOfBoundsException();
        if (players.contains(player)) throw new IllegalArgumentException();
        players.add(player);
    }

    /**
     *
     * @return the number of players in the current Game
     */
    public int getPlayerNum() {
        return playerNum;
    }

    /**
     *
     * @return the Board of the current Game
     */
    public Board getBoard() {
        return board;
    }

    /**
     *
     * @return the Deck containing all the God Power Cards
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     *
     * @return the active player
     */
    public Player getActivePlayer() {
        return activePlayer;
    }

    /**
     * sets the active player
     *
     * @param player
     */
    public void setActivePlayer(Player player) {
        activePlayer = player;
    }

    /**
     *
     * @return all the active Modifiers
     */
    public ArrayList<OpponentModifier> getActiveModifiers() {
        return new ArrayList<OpponentModifier>(activeModifiers);
    }

    /**
     * adds the argument to the list of the active Modifiers
     *
     * @param modifier
     */
    public void addModifier(OpponentModifier modifier) {
        activeModifiers.add(modifier);
    }

    /**
     * removes the argument from the list of the active Modifiers
     *
     * @param modifier
     */
    public void removeModifier(OpponentModifier modifier) {
        activeModifiers.remove(modifier);
    }

    /**
     *
     * @return the player of the current Game
     */
    public Player getWinner() {
        return winner;
    }

    /**
     *
     * @return true if the current Game has a winner, false otherwise
     */
    public boolean hasWinner() {
        return winner != null;
    }

    /**
     * sets the winner of the current Game
     *
     * @param winner the player to set as the winner of the current Game
     */
    public void setWinner(Player winner) {
        this.winner = winner;
    }

}
