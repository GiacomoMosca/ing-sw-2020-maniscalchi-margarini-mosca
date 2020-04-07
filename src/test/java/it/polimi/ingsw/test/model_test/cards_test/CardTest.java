package it.polimi.ingsw.test.model_test.cards_test;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.turn_controllers.GodController;
import it.polimi.ingsw.controller.turn_controllers.GodControllerConcrete;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.players.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CardTest {
    Card card=null;
    Player p1;
    Game game;
    GameController gamecontroller;
    GodController gc;


    @Before
    public void setUp() {
        p1=new Player("Luca", "Giallo");
        game=new Game(p1, 2);
        gamecontroller=new GameController(game);
        gc=new GodControllerConcrete(gamecontroller);
        card=new Card("a", "b", "c", 1, false, gc);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getGod_NoInputGiven_ShouldReturnGodCardName() {
        assertEquals(card.getGod(),"a");
    }

    @Test
    public void getController_NoInputGiven_ShouldReturnGodCardController() {
        assertSame(card.getController(), gc);
    }

}