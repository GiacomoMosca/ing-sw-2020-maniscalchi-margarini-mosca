package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.controller.turn_controllers.GodController;

public class Card {

    private final String god;
    private final String title;
    private final String description;
    private final int set;
    private final boolean alwaysActiveModifier;
    private final GodController controller;

    /**
     * creates a card, setting its attributes to the values passed as arguments
     *
     * @param god        the name of the God represented by the card
     * @param controller the God Controller associated with the card
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
     * @return the god associated with this card
     */
    public String getGod() {
        return god;
    }

    /**
     * @return the description of this card
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return true if the Modifier associated with this card is always active, false otherwise
     */
    public boolean hasAlwaysActiveModifier() {
        return alwaysActiveModifier;
    }

    /**
     * @return the God Controller associated with this card
     */
    public GodController getController() {
        return controller;
    }

    ////For testing only////
    public String getTitle() {
        return title;
    }

    public int getSet() {
        return set;
    }
    ////////////////////////

    @Override
    public boolean equals(Object obj) {
        Card card = (Card) obj;
        return this.god.equals(card.getGod()) || this.title.equals(card.getTitle()) || this.description.equals(card.getDescription()) || this.set == getSet() || this.alwaysActiveModifier == card.hasAlwaysActiveModifier() || this.controller == card.getController();

    }
}
