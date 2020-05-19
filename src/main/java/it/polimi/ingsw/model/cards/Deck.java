package it.polimi.ingsw.model.cards;

import java.util.ArrayList;
import java.util.Collections;

/**
 * do we need to write a JavaDoc comment for every single class?
 */
public class Deck {

    private final ArrayList<Card> cards;
    private final ArrayList<Card> pickedCards;

    /**
     * Creates a Deck object, instantiating two ArrayList: one for all the God Cards and one for the cards effectively
     * used in the current Game.
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
     * @param card adds the God Card received as an argument to the deck
     */
    public void addCard(Card card) {
        cards.add(card);
    }

    /**
     * @return an ArrayList of all the God Cards picked up for this game
     */
    public ArrayList<Card> getPickedCards() {
        return new ArrayList<Card>(pickedCards);
    }

    /**
     * Adds a Card object to the list of the picked cards to use for the current Game.
     *
     * @param card the Card we want to use
     * @throws IllegalArgumentException if the Card object received as an argument is already part of the picked cards
     */
    public void pickCard(Card card) throws IllegalArgumentException {
        if (pickedCards.contains(card)) throw new IllegalArgumentException();
        pickedCards.add(card);
    }

    /**
     * Removes the Card object received as an argument from the deck of the picked cards.
     *
     * @param card the Card object we want to remove
     */
    public void removePickedCard(Card card) {
        pickedCards.remove(card);
    }

    /**
     * Randomly picks n Card objects from the deck. Before doing this, checks that the requested cards aren't more than
     * those available.
     *
     * @param n the number of cards to pick from the deck
     * @throws IndexOutOfBoundsException when trying to pick more cards than those available
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
     * Indicates whether some other deck is "equal to" this one.
     *
     * @param obj   the reference object with which to compare
     * @return      true if this deck is the same as the obj argument, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        Deck deck = (Deck) obj;
        return this.getPickedCards().equals(deck.getPickedCards()) && this.getCards().equals(deck.getCards());
    }

}
