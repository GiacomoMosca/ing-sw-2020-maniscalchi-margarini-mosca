package it.polimi.ingsw.view;

import it.polimi.ingsw.model.cards.Card;

import java.io.Serializable;

/**
 * Represents a view of the Card class to the Client.
 * It implements serializable so that it can be serialized in the messages that client and server exchange: this way, the client won't have access to the Model objects.
 */
public class CardView implements Serializable {

    private final String god;
    private final String title;
    private final String description;

    /**
     * CardView constructor.
     * CardView attributes are set to the values of the same attributes of the Card Object received as an argument.
     *
     * @param card the Card represented by this CardView
     */
    public CardView(Card card) {
        this.god = card.getGod();
        this.title = card.getTitle();
        this.description = card.getDescription();
    }

    /**
     * CardView constructor.
     * CardView attributes are set to the values received as arguments.
     *
     * @param god         the God associated with the Card this CardView represents
     * @param title       the title of the Card this CardView represents
     * @param description the description of the Card this CardView represents
     */
    public CardView(String god, String title, String description) {
        this.god = god;
        this.title = title;
        this.description = description;
    }

    /**
     * @return the God associated with the Card this CardView represents
     */
    public String getGod() {
        return god;
    }

    /**
     * @return the title of the Card this CardView represents
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the description of the Card this CardView represents
     */
    public String getDescription() {
        return description;
    }

}
