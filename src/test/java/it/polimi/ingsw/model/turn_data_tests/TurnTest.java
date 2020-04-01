package it.polimi.ingsw.model.turn_data_tests;

import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TurnTest {

    Turn turn=null;
    Worker worker=null;
    Player player=null;

    @Before
    public void setUp() {
        player=new Player("Alessia","Bianco");
        turn=new Turn(player);
        worker=new Worker(player);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getActivePlayer_NoInputGiven_ShouldReturnActivePlayer() {
        assertEquals(turn.getActivePlayer(),player);
    }

    @Test
    public void getPhase_NoInputGiven_ShouldReturnPhase() {
        assertEquals(turn.getPhase(),1);
    }

    @Test
    public void setPhase_NextPhaseGiven_ShouldChangePhase() {
        turn.setPhase(2);
        assertEquals(turn.getPhase(),2);
    }

    @Test
    public void getActiveWorker_NoInputGiven_ShouldReturnActiveWorker() {
        assertNull(turn.getActiveWorker());
        turn.setActiveWorker(worker);
        assertEquals(turn.getActiveWorker(),worker);
    }

    @Test
    public void setActiveWorker_WorkerPlayerWantToMoveGiven_ShouldSetActiveWorker() {
        turn.setActiveWorker(worker);
        assertEquals(turn.getActiveWorker(),worker);
    }
}