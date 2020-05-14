package it.polimi.ingsw.view;

import it.polimi.ingsw.model.cards.Card;

import java.io.Serializable;

public class CardView implements Serializable {

    private final String god;
    private final String title;
    private final String description;

    public CardView(Card card) {
        this.god = card.getGod();
        this.title = card.getTitle();
        this.description = card.getDescription();;
    }

    public CardView(String god, String title, String description) {
        this.god = god;
        this.title = title;
        this.description = description;
    }

    public String getGod() {
        return god;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

}
