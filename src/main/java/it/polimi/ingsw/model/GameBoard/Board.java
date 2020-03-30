package it.polimi.ingsw.model.GameBoard;

import java.util.ArrayList;

public class Board {

    private final Cell[][] cells;

    /**
     * creates a new Board for the Game, having 25 cells
     */
    public Board() {
        this.cells = new Cell[5][5];
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                this.cells[i][j] = new Cell(i, j);
    }

    /**
     *
     * @param i the x-coordinate of the requested cell
     * @param j the y-coordinate of the requested cell
     * @return the requested cell
     * @throws ArrayIndexOutOfBoundsException when the requested coordinates don't identify a cell of the Board
     */
    public Cell getCell(int i, int j) throws ArrayIndexOutOfBoundsException {
        if (i < 0 || i >= 5 || j < 0 || j >= 5) throw new ArrayIndexOutOfBoundsException();
        return cells[i][j];
    }

    /**
     *
     * @param pos the cell indicating the position we want to know the neighbors of
     * @return a list of all the cells surrounding the requested one
     */
    public ArrayList<Cell> getNeighbors(Cell pos) {
        Cell curr;
        ArrayList<Cell> res = new ArrayList<Cell>();
        int x = pos.getPosX();
        int y = pos.getPosY();
        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++) {
                if (i == 0 && j == 0) continue;
                try {
                    curr = getCell(x + i, y + j);
                } catch (ArrayIndexOutOfBoundsException e) {
                    continue;
                }
                res.add(curr);
            }
        return res;
    }

}
