package it.polimi.ingsw.model.Cards;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {

    private final ArrayList<Card> cards;
    private final ArrayList<Card> pickedCards;

    public Deck() {
        cards = new ArrayList<Card>();
        pickedCards = new ArrayList<Card>();
    }

    public ArrayList<Card> getCards() {
        return new ArrayList<Card>(cards);
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public ArrayList<Card> getPickedCards() {
        return new ArrayList<Card>(pickedCards);
    }

    public void pickCard(Card card) throws IllegalArgumentException {
        if (pickedCards.contains(card)) throw new IllegalArgumentException();
        pickedCards.add(card);
    }

    public void removePickedCard(Card card) {
        pickedCards.remove(card);
    }

    public void pickRandom(int n) throws IndexOutOfBoundsException {
        if (n > cards.size()) throw new IndexOutOfBoundsException();
        ArrayList<Card> shuffledDeck = new ArrayList<Card>(cards);
        Collections.shuffle(shuffledDeck);
        pickedCards.clear();
        for (int i = 0; i < n; i++)
            pickedCards.add(shuffledDeck.get(i));
    }

}
