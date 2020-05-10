package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.game_board.Board;
import it.polimi.ingsw.model.players.Player;

import java.util.ArrayList;

/**
 * represents the current state of the game
 */
public class Game {

    private final String name;
    private final ArrayList<Player> players;
    private final int playerNum;
    private final Board board;
    private final Deck deck;
    private final ArrayList<Card> activeModifiers;
    private int activePlayer;
    private Player winner;

    /**
     * prepares a new Game
     *
     * @param p1  the player who first sign up for the game
     * @param num the number of players
     */
    public Game(String name, Player p1, int num) {
        this.name = name;
        players = new ArrayList<Player>();
        players.add(p1);
        playerNum = num;
        board = new Board();
        deck = new Deck();
        activePlayer = 0;
        activeModifiers = new ArrayList<Card>();
        winner = null;
    }

    public String getName() {
        return name;
    }

    /**
     * @return all the players
     */
    public ArrayList<Player> getPlayers() {
        return new ArrayList<Player>(players);
    }

    /**
     * adds a player to the current Game
     *
     * @param player the new player
     * @throws IndexOutOfBoundsException when trying to add more players than allowed for the current Game
     * @throws IllegalArgumentException  when trying to add a player who already joined the current Game
     */
    public void addPlayer(Player player) throws IndexOutOfBoundsException, IllegalArgumentException {
        if (players.size() >= playerNum) throw new IndexOutOfBoundsException();
        if (players.contains(player)) throw new IllegalArgumentException();
        players.add(player);
    }

    /**
     * @return the number of players in the current Game
     */
    public int getPlayerNum() {
        return playerNum;
    }

    /**
     * @return the Board of the current Game
     */
    public Board getBoard() {
        return board;
    }

    /**
     * @return the Deck containing all the God Power Cards
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * @return the active player's number
     */
    public int getActivePlayer() {
        return activePlayer;
    }

    /**
     * sets the number of the active player
     *
     * @param i the number to set
     */
    public void setActivePlayer(int i) {
        if (i >= playerNum) i = playerNum - 1;
        this.activePlayer = i;
    }

    /**
     * correctly updates the activePlayer counter
     */
    public void nextPlayer() {
        activePlayer = (activePlayer >= playerNum - 1) ? 0 : activePlayer + 1;
        if (players.get(activePlayer).hasLost()) nextPlayer();
    }

    /**
     * @return all the active Modifiers
     */
    public ArrayList<Card> getActiveModifiers() {
        return new ArrayList<Card>(activeModifiers);
    }

    /**
     * adds the argument to the list of the active Modifiers
     *
     * @param modifier the God Card we want to add to the list of the modifiers
     */
    public void addModifier(Card modifier) {
        activeModifiers.add(modifier);
    }

    /**
     * removes the argument from the list of the active Modifiers
     *
     * @param modifier the God Card we want to remove from the list of the modifiers
     */
    public void removeModifier(Card modifier) {
        activeModifiers.remove(modifier);
    }

    /**
     * @return the player of the current Game
     */
    public Player getWinner() {
        return winner;
    }

    /**
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
