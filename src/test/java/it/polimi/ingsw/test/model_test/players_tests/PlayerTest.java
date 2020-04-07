package it.polimi.ingsw.test.model_test.players_tests;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.turn_controllers.ApolloController;
import it.polimi.ingsw.controller.turn_controllers.GodController;
import it.polimi.ingsw.controller.turn_controllers.GodControllerConcrete;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerTest {

    Player player=null;
    Card card=null;
    Worker worker1,worker2=null;
    Game game=null;
    GodController godcontroller;
    GameController gameController=null;

    @Before
    public void setUp() {
        game = new Game(player,1);
        gameController=new GameController(game);
        godcontroller=new GodControllerConcrete(gameController);
        player=new Player("Eni","Red");
        card=new Card("a", "b", "c", 1, false, godcontroller);
        worker1=new Worker(player);
        worker2=new Worker(player);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getId_NoInputGiven_ShouldReturnPlayerID() {
        assertEquals(player.getId(),"Eni");
    }

    @Test
    public void getColor_NoInputGiven_ShouldReturnPlayerColor() {
        assertEquals(player.getColor(),"Red");
    }

    @Test
    public void getGodCard_NoInputGiven_ShouldReturnPlayerGodCard() {
        assertNull(player.getGodCard());
        player.setGodCard(card);
        assertSame(player.getGodCard(),card);
    }

    @Test
    public void setGodCard_PlayerGodCardGiven_ShouldLinkPlayerWithHisGodCard() {
        player.setGodCard(card);
        assertSame(player.getGodCard(),card);
    }

    @Test
    public void getWorkers_NoInputGiven_ShouldReturnActiveWorkers() {
        assertEquals(player.getWorkers().size(),0);
        player.addWorker(worker1);
        assertTrue(player.getWorkers().contains(worker1));
    }

    @Test
    public void addWorker_PlayerWorkerGiven_ShouldLinkPlayerWithHisWorker() {
        player.addWorker(worker1);
        assertTrue(player.getWorkers().contains(worker1));
        player.addWorker(worker2);
        assertTrue(player.getWorkers().contains(worker2));
        assertTrue(player.getWorkers().contains(worker1));
    }

    @Test
    public void removeWorker_PlayerWorkerGiven_ShouldUnlinkPlayerAndWorker() {
        player.addWorker(worker1);
        player.addWorker(worker2);
        player.removeWorker(worker1);
        assertFalse(player.getWorkers().contains(worker1));
        assertTrue(player.getWorkers().contains(worker2));
    }
}