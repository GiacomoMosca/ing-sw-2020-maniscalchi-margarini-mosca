package it.polimi.ingsw.model.cards;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Describes all the Cards available for the Game and the 2 or 3 cards effectively picked up and used.
 * Cards can be picked manually or at random.
 */
public class Deck {

    /**
     * List of all the Cards composing the Deck.
     */
    private final ArrayList<Card> cards;
    /**
     * List of all the picked Cards for the Game.
     */
    private final ArrayList<Card> pickedCards;

    /**
     * Deck constructor. Instantiates two ArrayList: one for all the God Cards and one for the cards effectively used in the current Game.
     */
    public Deck() {
        cards = new ArrayList<Card>();
        pickedCards = new ArrayList<Card>();
    }

    /**
     * @return an ArrayList containing all the God Cards
     */
    public ArrayList<Card> getCards() {
        return new ArrayList<Card>(cards);
    }

    /**
     * @param card adds the God Card received as an argument to the Deck
     */
    public void addCard(Card card) {
        cards.add(card);
    }

    /**
     * @return an ArrayList of all the God Cards picked up for this Game
     */
    public ArrayList<Card> getPickedCards() {
        return new ArrayList<Card>(pickedCards);
    }

    /**
     * Adds a Card object to the list of the picked Cards to use for the current Game.
     *
     * @param card the Card we want to use
     * @throws IllegalArgumentException if the Card object received as an argument is already part of the picked Cards
     */
    public void pickCard(Card card) throws IllegalArgumentException {
        if (pickedCards.contains(card)) throw new IllegalArgumentException();
        pickedCards.add(card);
    }

    /**
     * Removes the Card object received as an argument from the Deck of the picked Cards.
     *
     * @param card the Card object we want to remove
     */
    public void removePickedCard(Card card) {
        pickedCards.remove(card);
    }

    /**
     * Randomly picks n Cards from the Deck. Before doing this, checks that the requested Cards aren't more than those available.
     *
     * @param n the number of Cards to pick from the Deck
     * @throws IndexOutOfBoundsException when trying to pick more Cards than those available
     */
    public void pickRandom(int n) throws IndexOutOfBoundsException {
        if (n > cards.size()) throw new IndexOutOfBoundsException();
        ArrayList<Card> shuffledDeck = new ArrayList<Card>(cards);
        Collections.shuffle(shuffledDeck);
        pickedCards.clear();
        for (int i = 0; i < n; i++)
            pickedCards.add(shuffledDeck.get(i));
    }

    /**
     * Indicates whether some other Deck is "equal to" this one.
     *
     * @param obj the reference object with which to compare
     * @return true if this deck is the same as the obj argument, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        Deck deck = (Deck) obj;
        return this.getPickedCards().equals(deck.getPickedCards()) && this.getCards().equals(deck.getCards());
    }

}
