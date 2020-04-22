package it.polimi.ingsw.model.game_board;

import it.polimi.ingsw.model.game_board.Board;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BoardTest {

    Board board=null;

    @Before
    public void setUp() {
        board = new Board();
    }

    @After
    public void tearDown() {
    }

    @Test (expected = ArrayIndexOutOfBoundsException.class)
    public void getCell_NotExistingCellGiven_ShouldThrowException() {
        board.getCell(-1,1);
    }

    @Test
    public void getCell_CellCoordsGiven_ShouldReturnSelectedCell() {
        assertEquals(board.getCell(1,1).getPosX(),1);
        assertEquals(board.getCell(1,1).getPosY(),1);
    }

    @Test
    public void getNeighbors_CellGiven_ShouldReturnCellNeighbors() {
        //Generica cella all'interno
        assertTrue(board.getNeighbors(board.getCell(1,1)).contains(board.getCell(0,0)));
        assertTrue(board.getNeighbors(board.getCell(1,1)).contains(board.getCell(0,1)));
        assertTrue(board.getNeighbors(board.getCell(1,1)).contains(board.getCell(0,2)));
        assertTrue(board.getNeighbors(board.getCell(1,1)).contains(board.getCell(1,0)));
        assertTrue(board.getNeighbors(board.getCell(1,1)).contains(board.getCell(1,2)));
        assertTrue(board.getNeighbors(board.getCell(1,1)).contains(board.getCell(2,0)));
        assertTrue(board.getNeighbors(board.getCell(1,1)).contains(board.getCell(2,1)));
        assertTrue(board.getNeighbors(board.getCell(1,1)).contains(board.getCell(2,2)));
        assertFalse(board.getNeighbors(board.getCell(1,1)).contains(board.getCell(3,0)));
        assertFalse(board.getNeighbors(board.getCell(1,1)).contains(board.getCell(1,1)));
        //Cella sul bordo
        assertTrue(board.getNeighbors(board.getCell(0,0)).contains(board.getCell(0,1)));
        assertTrue(board.getNeighbors(board.getCell(0,0)).contains(board.getCell(1,1)));
        assertTrue(board.getNeighbors(board.getCell(0,0)).contains(board.getCell(1,0)));
    }
}