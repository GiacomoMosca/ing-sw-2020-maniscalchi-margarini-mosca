package it.polimi.ingsw.test.model_test.cards_test;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.turn_controllers.GodController;
import it.polimi.ingsw.controller.turn_controllers.GodControllerConcrete;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.view.CLI;
import it.polimi.ingsw.view.PlayerInterface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DeckTest {

    Deck deck = null;
    Card card1 = null;
    Card card2;
    Player p1;
    Game game;
    GameController gamecontroller;
    GodController gc1, gc2;

    @Before
    public void setUp() {
        gamecontroller=new GameController(new PlayerInterface(new CLI()),2);
        gc1=new GodControllerConcrete(gamecontroller);
        deck=new Deck();
        card1=new Card("Rebecca", "a", "b", 1, true, gc1);
        card2=new Card("Giacomo", "v", "x", 1, false, gc2);
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
        deck.addCard(card1);
        assertTrue(deck.getCards().contains(card1));
        deck.addCard(card2);
        assertTrue(deck.getCards().contains(card2));
        assertTrue(deck.getCards().contains(card1));
    }

    @Test
    public void getPickedCards_NoInputGiven_ShouldReturnPickedCard() {
        assertEquals(deck.getPickedCards().size(),0);
        deck.pickCard(card1);
        assertTrue(deck.getPickedCards().contains(card1));
    }

    @Test
    public void pickCard_CardToPickGiven_ShouldAddToPickedCardDeckTheCard() {
        deck.pickCard(card1);
        assertTrue(deck.getPickedCards().contains(card1));
        deck.pickCard(card2);
        assertTrue(deck.getPickedCards().contains(card2));
        assertTrue(deck.getPickedCards().contains(card1));
    }

    @Test (expected = IllegalArgumentException.class)
    public void pickCard_AlreadyPickedCardGiven_ShouldThrowException() {
        deck.pickCard(card1);
        deck.pickCard(card1);
    }

    @Test
    public void removePickedCard_CardToRemoveGiven_ShouldRemoveCardFromPickedCardDeck() {
        deck.pickCard(card1);
        deck.pickCard(card2);
        deck.removePickedCard(card1);
        assertFalse(deck.getPickedCards().contains(card1));
        assertTrue(deck.getPickedCards().contains(card2));
    }

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

}
