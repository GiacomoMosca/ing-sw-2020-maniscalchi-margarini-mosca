package it.polimi.ingsw.model.game_board;

import java.util.ArrayList;

/**
 * Stores information about the 25 Cells that make up the Board.
 * Can return information about specific cells or their neighbors.
 */
public class Board {

    private final Cell[][] cells;

    /**
     * Board constructor. Creates a new Board for the Game, instantiating an ArrayList of 25 Cells.
     */
    public Board() {
        this.cells = new Cell[5][5];
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                this.cells[i][j] = new Cell(i, j);
    }

    /**
     * @param x the x-coordinate of the requested Cell, ie the column
     * @param y the y-coordinate of the requested Cell, ie the row
     * @return the requested Cell
     * @throws ArrayIndexOutOfBoundsException when the requested coordinates don't identify a Cell of the Board
     */
    public Cell getCell(int x, int y) throws ArrayIndexOutOfBoundsException {
        if (x < 0 || x >= 5 || y < 0 || y >= 5) throw new ArrayIndexOutOfBoundsException();
        return cells[y][x];
    }

    /**
     * @return an ArrayList of Cells, containing all the Cells of the Board of this Game
     */
    public ArrayList<Cell> getAllCells() {
        ArrayList<Cell> allCells = new ArrayList<Cell>();
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                allCells.add(cells[j][i]);
        return allCells;
    }

    /**
     * @param cell the Cell we want to set
     * @param x    the x-coordinate of the Cell
     * @param y    the y-coordinate of the Cell
     * @throws ArrayIndexOutOfBoundsException when trying to set a Cell with coordinates that don't identify a Cell of the Board
     */
    public void setCell(Cell cell, int x, int y) throws ArrayIndexOutOfBoundsException {
        if (x < 0 || x >= 5 || y < 0 || y >= 5) throw new ArrayIndexOutOfBoundsException();
        cells[y][x] = cell;
    }

    /**
     * @param pos the Cell object indicating the position we want to know the neighbors of
     * @return an ArrayList containing all the Cells surrounding the requested one
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

    /**
     * Indicates whether some other Board is equal to this one.
     *
     * @param obj the reference object with which to compare
     * @return true if this board is the same as the obj argument, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {

        Board board = (Board) obj;
        int NumCell1 = 0, NumCell2 = 0;
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                NumCell1++;
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                NumCell2++;
        return NumCell1 == NumCell2;
    }

}
