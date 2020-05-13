package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.turn_controllers.GodController;
import it.polimi.ingsw.controller.turn_controllers.GodControllerConcrete;
import it.polimi.ingsw.view.VirtualView;
import it.polimi.ingsw.view.cli.CLI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static org.junit.Assert.*;

public class DeckTest {

    Card card1, card2;
    GameController gamecontroller;
    GodController gc1, gc2;
    Socket socket;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    @Before
    public void setUp() {
        gamecontroller=new GameController(new VirtualView(socket, objectInputStream, objectOutputStream),2, "game");
        gc1=new GodControllerConcrete(gamecontroller);
        gc2=new GodControllerConcrete(gamecontroller);
        card1=new Card("god1", "title1", "description1", 1, true, gc1);
        card2=new Card("god2", "title2", "description2", 2, false, gc2);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getCards_NoInputGiven_ShouldReturnAllTheGodCards() {
        assertEquals(gamecontroller.getGame().getDeck().getCards().size(),0);
        gamecontroller.getGame().getDeck().addCard(card1);
        gamecontroller.getGame().getDeck().addCard(card2);
        assertTrue(gamecontroller.getGame().getDeck().getCards().contains(card1));
        assertTrue(gamecontroller.getGame().getDeck().getCards().contains(card2));
    }

    @Test
    public void addCard_CardToAddGiven_ShouldAddCardToDeck() {
        Deck deck1=new Deck();
        deck1.addCard(card1);
        assertTrue(deck1.getCards().contains(card1));
        deck1.addCard(card2);
        assertTrue(deck1.getCards().contains(card2));
        assertTrue(deck1.getCards().contains(card1));
    }

    @Test
    public void getPickedCards_NoInputGiven_ShouldReturnPickedCard() {
        assertEquals(gamecontroller.getGame().getDeck().getPickedCards().size(),0);
        gamecontroller.getGame().getDeck().pickCard(card1);
        assertTrue(gamecontroller.getGame().getDeck().getPickedCards().contains(card1));
    }

    @Test
    public void pickCard_CardToPickGiven_ShouldAddToPickedCardDeckTheCard() {
        gamecontroller.getGame().getDeck().pickCard(card1);
        assertTrue(gamecontroller.getGame().getDeck().getPickedCards().contains(card1));
        gamecontroller.getGame().getDeck().pickCard(card2);
        assertTrue(gamecontroller.getGame().getDeck().getPickedCards().contains(card2));
        assertTrue(gamecontroller.getGame().getDeck().getPickedCards().contains(card1));
    }

    @Test (expected = IllegalArgumentException.class)
    public void pickCard_AlreadyPickedCardGiven_ShouldThrowException() {
        gamecontroller.getGame().getDeck().pickCard(card1);
        gamecontroller.getGame().getDeck().pickCard(card1);
    }

    @Test
    public void removePickedCard_CardToRemoveGiven_ShouldRemoveCardFromPickedCardDeck() {
        gamecontroller.getGame().getDeck().pickCard(card1);
        gamecontroller.getGame().getDeck().pickCard(card2);
        gamecontroller.getGame().getDeck().removePickedCard(card1);
        assertFalse(gamecontroller.getGame().getDeck().getPickedCards().contains(card1));
        assertTrue(gamecontroller.getGame().getDeck().getPickedCards().contains(card2));
    }

    @Test
    public void pickRandom_NoInputGiven_ShouldReturnARandomPickedCard() {
        gamecontroller.getGame().getDeck().addCard(card1);
        gamecontroller.getGame().getDeck().addCard(card2);
        gamecontroller.getGame().getDeck().pickRandom(2);
        assertTrue(gamecontroller.getGame().getDeck().getPickedCards().contains(card1));
        assertTrue(gamecontroller.getGame().getDeck().getPickedCards().contains(card2));
        assertTrue(gamecontroller.getGame().getDeck().getCards().contains(card1));
        assertTrue(gamecontroller.getGame().getDeck().getCards().contains(card2));
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void pickRandom_NoInput_ShouldThrowException() {
        gamecontroller.getGame().getDeck().pickRandom(1);
    }

    @Test
    public void equals_twoDecksGiven_shouldReturnTrue(){
        Deck deck1=new Deck();
        Deck deck2=new Deck();

        assertTrue(deck1.equals(deck2));
    }
}
