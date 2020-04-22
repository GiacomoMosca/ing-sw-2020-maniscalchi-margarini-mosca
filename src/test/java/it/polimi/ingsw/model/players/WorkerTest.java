package it.polimi.ingsw.model.players;

import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class WorkerTest {

    Worker worker1,worker2=null;
    Player player=null;
    Cell cell1,cell2=null;

    @Before
    public void setUp() {
        player=new Player("Francesco","Blu");
        worker1=new Worker(player);
        worker2=new Worker(player);
        cell1=new Cell(3,3);
        cell2=new Cell(2,3);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getOwner_NoInputGiven_ShouldReturnWorkerOwnerPlayer() {
        assertSame(worker1.getOwner(),player);
    }

    @Test
    public void getPosition_NoInputGiven_ShouldReturnWorkerPosition() {
        assertNull(worker1.getPosition());
        worker1.setPosition(cell1);
        assertSame(worker1.getPosition(),cell1);
    }

    @Test
    public void move_CellToMoveTo_ShouldChangeWorkerPosition() {
        worker1.setPosition(cell1);
        worker1.move(cell2);
        assertSame(worker1.getPosition(),cell2);
        assertNull(cell1.getWorker());
    }

    @Test (expected = IllegalArgumentException.class)
    public void move_CellToMoveTo_ShouldThrowExceptionFirstBranch() {
        worker1.setPosition(cell2);
        cell1.buildDome();
        worker1.move(cell1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void move_CellToMoveTo_ShouldThrowExceptionSecondBranch() {
        worker1.setPosition(cell2);
        worker2.setPosition(cell1); //Only for completeness
        cell1.setWorker(worker2);
        worker1.move(cell1);
    }
}