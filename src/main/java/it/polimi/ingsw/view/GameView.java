package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Player;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;
import java.util.ArrayList;

public class GameView implements Serializable {

    private final String name;
    private final int playerNum;
    private final ArrayList<PlayerView> players;
    private final int activePlayer;
    private final CellView[][] board;
    private final ArrayList<CardView> activeModifiers;

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

    public GameView(String name, int playerNum, ArrayList<PlayerView> players, int activePlayer, CellView[][] board, ArrayList<CardView> modifiers) {
        this.name =name;
        this.playerNum = playerNum;
        this.players = players;
        this.activePlayer = activePlayer;
        this.board = board;
        this.activeModifiers = modifiers;
    }

    public String getName() {
        return name;
    }

    public int getPlayerNum() {
        return playerNum;
    }

    public ArrayList<PlayerView> getPlayers() {
        return new ArrayList<PlayerView>(players);
    }

    public int getActivePlayer() {
        return activePlayer;
    }

    /**
     * @param x the x-coordinate of the requested cell
     * @param y the y-coordinate of the requested cell
     * @return the requested cell
     * @throws ArrayIndexOutOfBoundsException when the requested coordinates don't identify a cell of the Board
     */
    public CellView getCell(int x, int y) throws ArrayIndexOutOfBoundsException {
        if (x < 0 || x >= 5 || y < 0 || y >= 5) throw new ArrayIndexOutOfBoundsException();
        return board[y][x];
    }

    public void setCell(CellView cell) throws ArrayIndexOutOfBoundsException {
        int x = cell.getPosX();
        int y = cell.getPosY();
        if (x < 0 || x >= 5 || y < 0 || y >= 5) throw new ArrayIndexOutOfBoundsException();
        board[x][y] = cell;
    }

    public ArrayList<CellView> getAllCells() {
        ArrayList<CellView> allCells = new ArrayList<CellView>();
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                allCells.add(board[j][i]);
        return allCells;
    }

    public ArrayList<CardView> getActiveModifiers() {
        return new ArrayList<CardView>(activeModifiers);
    }

}
