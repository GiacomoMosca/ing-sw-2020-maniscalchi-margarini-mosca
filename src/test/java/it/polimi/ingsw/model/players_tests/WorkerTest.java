package it.polimi.ingsw.model.players_tests;

import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class WorkerTest {

    Worker worker=null;
    Player player=null;
    Cell cell=null;

    @Before
    public void setUp() {
        player=new Player("Francesco","Blu");
        worker=new Worker(player);
        cell=new Cell(3,3);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getOwner_NoInputGiven_ShouldWorkerOwnerPlayer() {
        assertEquals(worker.getOwner(),player);
    }

    @Test
    public void getPosition_NoInputGiven_ShouldReturnWorkerPosition() {
        assertNull(worker.getPosition());
        worker.move(cell);
        assertEquals(worker.getPosition(),cell);
    }

    @Test
    public void move_CellToMoveTo_ShouldChangeWorkerPosition() {
        worker.move(cell);
        assertEquals(worker.getPosition(),cell);
    }
}