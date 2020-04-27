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

    Deck deck;
    Card card1;
    Card card2;
    GameController gamecontroller;
    GodController gc1, gc2;
    Socket socket;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    @Before
    public void setUp() {
        gamecontroller=new GameController(new VirtualView(socket, objectInputStream, objectOutputStream),2);
        gc1=new GodControllerConcrete(gamecontroller);
        deck=new Deck();
        card1=new Card("god1", "title1", "description1", 1, true, gc1);
        card2=new Card("god2", "title2", "description2", 2, false, gc2);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getCards_NoInputGiven_ShouldReturnAllTheGodCards() {
        assertEquals(deck.getCards().size(),0);
        deck.addCard(card1);
        assertTrue(deck.getCards().contains(card1));
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
        assertEquals(deck.getPickedCards().size(),0);
        deck.pickCard(card1);
        assertTrue(deck.getPickedCards().contains(card1));
    }
/*
    @Test
    public void pickCard_CardToPickGiven_ShouldAddToPickedCardDeckTheCard() {
        Deck deck2=new Deck();
        deck2.pickCard(card1);
        assertTrue(deck2.getPickedCards().contains(card1));
        deck2.pickCard(card2);
        assertTrue(deck2.getPickedCards().contains(card2));
        assertTrue(deck2.getPickedCards().contains(card1));
    }*/

    @Test (expected = IllegalArgumentException.class)
    public void pickCard_AlreadyPickedCardGiven_ShouldThrowException() {
        deck.pickCard(card1);
        deck.pickCard(card1);
    }
/*
    @Test
    public void removePickedCard_CardToRemoveGiven_ShouldRemoveCardFromPickedCardDeck() {
        deck.pickCard(card1);
        deck.pickCard(card2);
        deck.removePickedCard(card1);
        assertFalse(deck.getPickedCards().contains(card1));
        assertTrue(deck.getPickedCards().contains(card2));
    }*/

    @Test
    public void pickRandom_NoInputGiven_ShouldReturnARandomPickedCard() {
        deck.addCard(card1);
        deck.addCard(card2);
        deck.pickRandom(2);
        assertTrue(deck.getPickedCards().contains(card1));
        assertTrue(deck.getPickedCards().contains(card2));
        assertTrue(deck.getCards().contains(card1));
        assertTrue(deck.getCards().contains(card2));
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void pickRandom_NoInput_ShouldThrowException() {
        deck.pickRandom(1);
    }

    @Test
    public void equals_twoDecksGiven_shouldReturnTrue(){
        Card card1=new Card("god", "title", "description", 1, false, gc1);
        Card card2=new Card("god", "title", "description", 1, false, gc1);

        Deck deck1=new Deck();
        Deck deck2=new Deck();

        assertTrue(deck1.equals(deck2));
    }
}
