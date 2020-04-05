package it.polimi.ingsw.model.game_board;

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
     * @param cell the Cell we want to set
     * @param i the x-coordinate of the cell
     * @param j the y-coordinate of the cell
     * @return the cell we set
     * @throws ArrayIndexOutOfBoundsException
     */
    public Cell setCell(Cell cell, int i, int j) throws ArrayIndexOutOfBoundsException {
        if (i < 0 || i >= 5 || j < 0 || j >= 5) throw new ArrayIndexOutOfBoundsException();
        return cells[i][j] = cell;
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

    /**
     * Indicates whether some other board is "equal to" this one.
     *
     * @param obj the reference object with which to compare
     * @return true if this board is the same as the obj argument, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {

        Board board = (Board)obj;
        int NumCell1=0, NumCell2=0;
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                NumCell1++;

        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                NumCell2++;
        return NumCell1 == NumCell2;
    }
}
