package it.polimi.ingsw.model.cards;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {

    private final ArrayList<Card> cards;
    private final ArrayList<Card> pickedCards;

    /**
     * creates two ArrayList, one for all the God Power Cards and one for the cards used in the current Game
     */
    public Deck() {
        cards = new ArrayList<Card>();
        pickedCards = new ArrayList<Card>();
    }

    /**
     *
     * @return a list of all the God Power Cards
     */
    public ArrayList<Card> getCards() {
        return new ArrayList<Card>(cards);
    }

    /**
     *
     * @param card adds a God Power Card to the deck
     */
    public void addCard(Card card) {
        cards.add(card);
    }

    /**
     *
     * @return a list of all the God Power Cards picked up for this game
     */
    public ArrayList<Card> getPickedCards() {
        return new ArrayList<Card>(pickedCards);
    }

    /**
     * adds a card to the list of the picked cards to use for the current Game
     *
     * @param card the Card we want to use
     * @throws IllegalArgumentException if the card is already part of the picked ones
     */
    public void pickCard(Card card) throws IllegalArgumentException {
        if (pickedCards.contains(card)) throw new IllegalArgumentException();
        pickedCards.add(card);
    }

    /**
     * removes the card from the deck of the picked cards
     *
     * @param card
     */
    public void removePickedCard(Card card) {
        pickedCards.remove(card);
    }

    /**
     * randomly pick n cards from the deck
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
     * @param obj the reference object with which to compare
     * @return true if this deck is the same as the obj argument, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        Deck deck= (Deck)obj;
        return this.getPickedCards().equals(deck.getPickedCards()) && this.getCards().equals(deck.getCards());
    }
}
