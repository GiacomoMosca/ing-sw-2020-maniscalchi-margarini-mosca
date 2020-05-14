package it.polimi.ingsw.model.players;

import it.polimi.ingsw.exceptions.IllegalMoveException;
import it.polimi.ingsw.model.game_board.Cell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class WorkerTest {

    Worker worker1, worker2 = null;
    Player player = null;
    Cell cell1, cell2 = null;

    @Before
    public void setUp() {
        player = new Player("Francesco", "Blu");
        worker1 = new Worker(player, 1);
        worker2 = new Worker(player, 2);
        cell1 = new Cell(3, 3);
        cell2 = new Cell(2, 3);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getOwner_NoInputGiven_ShouldReturnWorkerOwnerPlayer() {
        assertSame(worker1.getOwner(), player);
    }

    @Test
    public void getPosition_NoInputGiven_ShouldReturnWorkerPosition() {
        assertNull(worker1.getPosition());
        worker1.setPosition(cell1);
        assertSame(worker1.getPosition(), cell1);
    }

    @Test
    public void move_CellToMoveTo_ShouldChangeWorkerPosition() throws IllegalMoveException {
        worker1.setPosition(cell1);
        worker1.move(cell2);
        assertSame(worker1.getPosition(), cell2);
        assertNull(cell1.getWorker());
    }

    @Test(expected = IllegalMoveException.class)
    public void move_CellToMoveTo_ShouldThrowExceptionFirstBranch() throws IllegalMoveException {
        worker1.setPosition(cell2);
        cell1.buildDome();
        worker1.move(cell1);
    }

    @Test(expected = IllegalMoveException.class)
    public void move_CellToMoveTo_ShouldThrowExceptionSecondBranch() throws IllegalMoveException {
        worker1.setPosition(cell2);
        worker2.setPosition(cell1); //Only for completeness
        cell1.setWorker(worker2);
        worker1.move(cell1);
    }
}