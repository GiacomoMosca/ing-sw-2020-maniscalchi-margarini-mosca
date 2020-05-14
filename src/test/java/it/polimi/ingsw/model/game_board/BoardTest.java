package it.polimi.ingsw.model.game_board;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BoardTest {

    Board board = null;

    @Before
    public void setUp() {
        board = new Board();
        Cell cell = new Cell(0, 0);
    }

    @After
    public void tearDown() {
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void getCell_NotExistingCellGiven_ShouldThrowException() {
        board.getCell(-1, 1);
    }

    @Test
    public void getCell_CellCoordsGiven_ShouldReturnSelectedCell() {
        assertEquals(board.getCell(1, 1).getPosX(), 1);
        assertEquals(board.getCell(1, 1).getPosY(), 1);
    }

    @Test
    public void getNeighbors_CellGiven_ShouldReturnCellNeighbors() {
        //Testing a generic cell inside the board
        assertTrue(board.getNeighbors(board.getCell(1, 1)).contains(board.getCell(0, 0)));
        assertTrue(board.getNeighbors(board.getCell(1, 1)).contains(board.getCell(0, 1)));
        assertTrue(board.getNeighbors(board.getCell(1, 1)).contains(board.getCell(0, 2)));
        assertTrue(board.getNeighbors(board.getCell(1, 1)).contains(board.getCell(1, 0)));
        assertTrue(board.getNeighbors(board.getCell(1, 1)).contains(board.getCell(1, 2)));
        assertTrue(board.getNeighbors(board.getCell(1, 1)).contains(board.getCell(2, 0)));
        assertTrue(board.getNeighbors(board.getCell(1, 1)).contains(board.getCell(2, 1)));
        assertTrue(board.getNeighbors(board.getCell(1, 1)).contains(board.getCell(2, 2)));
        assertFalse(board.getNeighbors(board.getCell(1, 1)).contains(board.getCell(3, 0)));
        assertFalse(board.getNeighbors(board.getCell(1, 1)).contains(board.getCell(1, 1)));
        //Testing a cell on the perimeter
        assertTrue(board.getNeighbors(board.getCell(0, 0)).contains(board.getCell(0, 1)));
        assertTrue(board.getNeighbors(board.getCell(0, 0)).contains(board.getCell(1, 1)));
        assertTrue(board.getNeighbors(board.getCell(0, 0)).contains(board.getCell(1, 0)));
    }

    @Test
    public void setCell_CellIntIntGiven_shouldSetCell() {
        Cell cell1 = new Cell(1, 1);
        board.setCell(cell1, 1, 1);
        assertSame(board.getCell(1, 1), cell1);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void setCell_CellIntIntGiven_shouldThrowArrayIndexOutOfBoundsException() {
        board.setCell(new Cell(10, 10), 10, 10);
    }

    @Test
    public void equals_twoBoardsGiven_shouldReturnTrue() {
        Board board1 = new Board();
        Board board2 = new Board();

        assertEquals(board1.getAllCells().get(0), board1.getCell(0, 0));
        assertTrue(board1.equals(board2));
    }
}