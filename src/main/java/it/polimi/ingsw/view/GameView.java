package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.players.Player;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a view of the Game class to the client.
 * It implements serializable so that it can be serialized in the messages that client and server exchange: this way, the client won't have access to the Model objects.
 */
public class GameView implements Serializable {

    /**
     * The Game's name.
     */
    private final String name;
    /**
     * The Game's player number (2 or 3).
     */
    private final int playerNum;
    /**
     * List of Players who joined the Game.
     */
    private final ArrayList<PlayerView> players;
    /**
     * The number of the turn's active Player.
     */
    private final int activePlayer;
    /**
     * The cells that make up the Game's board.
     */
    private final CellView[][] board;
    /**
     * List of Cards with a modifier that's currently active.
     */
    private final ArrayList<CardView> activeModifiers;

    /**
     * GameView constructor.
     * GameView attributes are set to the values of the same attributes of the Game Object received as an argument.
     *
     * @param game the Game represented by this GameView
     */
    public GameView(Game game) {
        name = game.getName();
        playerNum = game.getPlayerNum();
        players = new ArrayList<PlayerView>();
        for (Player player : game.getPlayers()) {
            players.add(new PlayerView(player));
        }
        activePlayer = game.getActivePlayer();
        board = new CellView[5][5];
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) {
                board[i][j] = new CellView(game.getBoard().getCell(i, j));
            }
        activeModifiers = new ArrayList<CardView>();
        for (Card card : game.getActiveModifiers()) {
            activeModifiers.add(new CardView(card));
        }
    }

    /**
     * GameView constructor.
     * GameView attributes are set to the values received as arguments.
     *
     * @param name         the name of the Game represented by this GameView
     * @param playerNum    the number of the Players in the Game represented by this GameView
     * @param players      the PlayerViews of the Players involved in the Game represented by this GameView
     * @param activePlayer the number of the active Player
     * @param board        the Array of CellViews representing the Board in the Game represented by this GameView
     * @param modifiers    the ArrayList of CardViews representing the modifiers in the  Game represented by this GameView
     */
    public GameView(String name, int playerNum, ArrayList<PlayerView> players, int activePlayer, CellView[][] board, ArrayList<CardView> modifiers) {
        this.name = name;
        this.playerNum = playerNum;
        this.players = players;
        this.activePlayer = activePlayer;
        this.board = board;
        this.activeModifiers = modifiers;
    }

    /**
     * @return the name of the Game represented by this GameView
     */
    public String getName() {
        return name;
    }

    /**
     * @return the number of the Players in the Game represented by this GameView
     */
    public int getPlayerNum() {
        return playerNum;
    }

    /**
     * @return an ArrayList containing the PlayerViews of the Players involved in the Game represented by this GameView
     */
    public ArrayList<PlayerView> getPlayers() {
        return new ArrayList<PlayerView>(players);
    }

    /**
     * @return the number of the active Player in the Game represented by this GameView
     */
    public int getActivePlayer() {
        return activePlayer;
    }

    /**
     * @param x the x-coordinate of the requested Cell
     * @param y the y-coordinate of the requested Cell
     * @return the CellView representing the requested Cell
     * @throws ArrayIndexOutOfBoundsException when index out of Array length
     */
    public CellView getCell(int x, int y) throws ArrayIndexOutOfBoundsException {
        if (x < 0 || x >= 5 || y < 0 || y >= 5) throw new ArrayIndexOutOfBoundsException();
        return board[y][x];
    }

    /**
     * @param cell the CellView which the x and y-coordinates will be equals to
     * @throws ArrayIndexOutOfBoundsException when index out of Array length
     */
    public void setCell(CellView cell) throws ArrayIndexOutOfBoundsException {
        int x = cell.getPosX();
        int y = cell.getPosY();
        if (x < 0 || x >= 5 || y < 0 || y >= 5) throw new ArrayIndexOutOfBoundsException();
        board[x][y] = cell;
    }

    /**
     * @return an ArrayList containing all the CellViews representing the Board in the Game
     */
    public ArrayList<CellView> getAllCells() {
        ArrayList<CellView> allCells = new ArrayList<CellView>();
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                allCells.add(board[j][i]);
        return allCells;
    }

    /**
     * @return an ArrayList containing the CardViews representing the active God Power Cards in the Game
     */
    public ArrayList<CardView> getActiveModifiers() {
        return new ArrayList<CardView>(activeModifiers);
    }

}
