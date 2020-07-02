package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.game_board.Board;
import it.polimi.ingsw.model.players.Player;

import java.util.ArrayList;

/**
 * Describes the current state of the Game.
 * Stores the name of the Game, the number of players, references to its Players, Board and Deck, the active God Power Cards
 * in a turn, the number of the turn player and an eventual winner.
 */
public class Game {

    /**
     * The name of the Game.
     */
    private final String name;
    /**
     * List of Players who joined the Game.
     */
    private final ArrayList<Player> players;
    /**
     * The Game's player number (2 or 3).
     */
    private final int playerNum;
    /**
     * The Game Board.
     */
    private final Board board;
    /**
     * The Deck containing all God Cards.
     */
    private final Deck deck;
    /**
     * List of Cards with a modifier that's currently active.
     */
    private final ArrayList<Card> activeModifiers;
    /**
     * The number of the turn's active Player.
     */
    private int activePlayer;
    /**
     * The Player who eventually won.
     */
    private Player winner;

    /**
     * Game constructor. Prepares a new Game, setting all the attributes to their default or actual values.
     *
     * @param name the name of the Game
     * @param p1   the player who first enters the Game
     * @param num  the number of players
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
     * @return an ArrayList containing all the players of the Game
     */
    public ArrayList<Player> getPlayers() {
        return new ArrayList<Player>(players);
    }

    /**
     * Adds a player to the current Game.
     *
     * @param player the new Player
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
     * Sets the number of the active players.
     *
     * @param i the number to set
     */
    public void setActivePlayer(int i) {
        if (i >= playerNum) i = playerNum - 1;
        this.activePlayer = i;
    }

    /**
     * Updates the activePlayer counter. It takes a value between 0 and 1 in a two-players game, and among 0, 1 or 2 in a three-players game.
     */
    public void nextPlayer() {
        activePlayer = (activePlayer >= playerNum - 1) ? 0 : activePlayer + 1;
        if (players.get(activePlayer).hasLost()) nextPlayer();
    }

    /**
     * @return all the active Modifiers of the Game
     */
    public ArrayList<Card> getActiveModifiers() {
        return new ArrayList<Card>(activeModifiers);
    }

    /**
     * Adds the Card received as an argument to the list of the active Modifiers.
     *
     * @param modifier the God Card to add
     */
    public void addModifier(Card modifier) {
        activeModifiers.add(modifier);
    }

    /**
     * Removes the Card received as an argument from the list of the active Modifiers.
     *
     * @param modifier the God Card to remove
     */
    public void removeModifier(Card modifier) {
        activeModifiers.remove(modifier);
    }

    /**
     * @return the winner of the current Game
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * Sets the Player received as an argument as the winner of the current Game.
     *
     * @param winner the Player to set as the winner
     */
    public void setWinner(Player winner) {
        this.winner = winner;
    }

    /**
     * @return true if the current Game has a winner, false otherwise
     */
    public boolean hasWinner() {
        return winner != null;
    }

}
