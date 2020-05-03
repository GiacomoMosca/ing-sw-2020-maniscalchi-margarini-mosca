package it.polimi.ingsw.view;

import it.polimi.ingsw.model.game_board.Board;
import it.polimi.ingsw.model.players.Player;

import java.io.Serializable;
import java.util.ArrayList;

public class GameView implements Serializable {

    private final ArrayList<PlayerView> players;
    private final CellView[][] board;

    public GameView(ArrayList<Player> players, Board board) {
        this.players = new ArrayList<PlayerView>();
        for (Player player : players) {
            this.players.add(new PlayerView(player));
        }
        this.board = new CellView[5][5];
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                this.board[i][j] = new CellView(board.getCell(i, j));
    }

    public GameView(ArrayList<PlayerView> players, CellView[][] board) {
        this.players = players;
        this.board = board;
    }

    public ArrayList<PlayerView> getPlayers() {
        return new ArrayList<PlayerView>(players);
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

}
