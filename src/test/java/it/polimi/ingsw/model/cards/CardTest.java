package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.turn_controllers.GodController;
import it.polimi.ingsw.controller.turn_controllers.GodControllerConcrete;
import it.polimi.ingsw.view.FakeVirtualView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static org.junit.Assert.*;

public class CardTest {
    Card card;
    GameController gamecontroller;
    GodController godController;
    Socket socket;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;


    @Before
    public void setUp() {
        gamecontroller = new GameController(new FakeVirtualView(socket, objectInputStream, objectOutputStream), 2, "Test");
        godController = new GodControllerConcrete(gamecontroller);
        card = new Card("god", "title", "description", 1, true, godController);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getGod_NoInputGiven_ShouldReturnGodCardName() {
        assertEquals(card.getGod(), "god");
    }

    @Test
    public void getController_NoInputGiven_ShouldReturnGodCardController() {
        assertSame(card.getController(), godController);
    }

    @Test
    public void getTitle_noInputGiven_shouldReturnTheTitleOfTheCard() {
        assertSame(card.getTitle(), "title");
    }

    @Test
    public void getDescription_noInputGiven_shouldReturnTheDescriptionOfTheCard() {
        assertSame(card.getDescription(), "description");

    }

    @Test
    public void getSet_noInputGiven_shouldReturnTheSetOfTheCard() {
        assertEquals(card.getSet(), 1);
    }

    @Test
    public void hasAlwaysActiveModifier_noInputGiven_shouldReturnTRUE() {
        assertTrue(card.hasAlwaysActiveModifier());
    }

    @Test
    public void equals_twoCardsGiven_shouldReturnTrue() {
        Card card1 = new Card("god", "title", "description", 1, false, godController);
        Card card2 = new Card("god", "title", "description", 1, false, godController);

        assertTrue(card1.equals(card2));
    }
}