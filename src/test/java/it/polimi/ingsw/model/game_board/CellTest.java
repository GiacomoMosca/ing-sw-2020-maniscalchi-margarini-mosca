package it.polimi.ingsw.model.game_board;

import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CellTest {

    Cell cell=null;
    Worker worker=null;
    Player player=null;

    @Before
    public void setUp() {
        cell=new Cell(1,3);
        player=new Player("Luca","Giallo Fluorescente");
        worker=new Worker(player);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getPosX_NoInputGiven_ShouldReturnXCoordOfPosition() {
        assertEquals(cell.getPosX(),1);
    }

    @Test
    public void getPosY_NoInputGiven_ShouldReturnYCoordOfPosition() {
        assertEquals(cell.getPosY(),3);
    }

    @Test
    public void getBuildLevel_NoInputGiven_ShouldReturnCurrentBuiltLevel() {
        assertEquals(cell.getBuildLevel(),0);
    }

    @Test
    public void build_NoInputGiven_ShouldIncreaseBuiltLevel() {
        cell.build();
        assertEquals(cell.getBuildLevel(),1);
    }

    @Test (expected = IllegalStateException.class)
    public void build_TryToBuildAnotherLevelOverLevel3_ShouldThrowException() {
        cell.build();
        cell.build();
        cell.build();
        cell.build();
        cell.build();
    }

    @Test
    public void isDomed_NoInputGiven_ShouldReturnIfTheBuildingIsDomed() {
        assertFalse(cell.isDomed());
    }

    @Test
    public void buildDome_NoInputGiven_ShouldSetTheDomedBooleanVariableToTrue() {
        cell.buildDome();
        assertTrue(cell.isDomed());
    }

    @Test
    public void getWorker_NoInputGiven_ShouldReturnWorkerInTheCell() {
        assertNull(cell.getWorker());
        cell.setWorker(worker);
        assertSame(cell.getWorker(),worker);
    }

    @Test
    public void hasWorker_NoInputGiven_ShouldReturnIfCellHasWorker() {
        assertFalse(cell.hasWorker());
        cell.setWorker(worker);
        assertTrue(cell.hasWorker());
    }

    @Test
    public void setWorker_WorkerToPutGiven_ShouldPutWorkerInCell() {
        cell.setWorker(worker);
        assertTrue(cell.hasWorker());
    }

    @Test
    public void setBuildLevel_buildingLevelGiven_shouldSetTheBuildingLevelOfThisCell(){
        cell.setBuildLevel(2);
        assertEquals(cell.getBuildLevel(), 2);
    }
}