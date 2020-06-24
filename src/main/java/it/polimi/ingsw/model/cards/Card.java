package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.controller.turn_controllers.GodController;

/**
 * The Card class keeps all the information about a Card: the name of the God associated with it, the title, the description, the set it belongs to, if the card is associated with an always active Modifier, the Controller associated with the Card.
 * It also allows interaction with the Card attributes with getter and setter methods.
 */
public class Card {

    private final String god;
    private final String title;
    private final String description;
    private final int set;
    private final boolean alwaysActiveModifier;
    private final GodController controller;

    /**
     * Card constructor. Sets the attributes of the Card to the values received as arguments.
     *
     * @param god                  the name of the God represented by the card
     * @param title                the title of this Card
     * @param description          the description of this Card
     * @param set                  the set this Card belongs to (1 or 2)
     * @param alwaysActiveModifier true if the Modifier associated with this Card is always active, false otherwise
     * @param controller           the God Controller associated with the card
     */
    public Card(String god, String title, String description, int set, boolean alwaysActiveModifier, GodController controller) {
        this.god = god;
        this.title = title;
        this.description = description;
        this.set = set;
        this.alwaysActiveModifier = alwaysActiveModifier;
        this.controller = controller;
    }

    /**
     * @return the God associated with this Card
     */
    public String getGod() {
        return god;
    }

    /**
     * @return the title of the God Card
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the Card's set number (1 for Simple Gods, 2 for Advanced Gods)
     */
    public int getSet() {
        return set;
    }

    /**
     * @return the description of this Card
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return true if the Modifier associated with this Card is always active, false otherwise
     */
    public boolean hasAlwaysActiveModifier() {
        return alwaysActiveModifier;
    }

    /**
     * @return the God Controller associated with this Card
     */
    public GodController getController() {
        return controller;
    }

    /**
     * Indicates whether some other Card is equal to this one.
     *
     * @param obj the object to compare with the Card
     * @return true if the parameter and the Card represent the same card, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        Card card = (Card) obj;
        return this.god.equals(card.getGod()) &&
                this.title.equals(card.getTitle()) &&
                this.description.equals(card.getDescription()) &&
                this.set == getSet() &&
                this.alwaysActiveModifier == card.hasAlwaysActiveModifier() &&
                this.controller == card.getController();

    }

}
