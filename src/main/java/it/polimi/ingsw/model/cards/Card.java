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
     * @param god the name of the God represented by the card
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
     *
     * @return the god associated with this card
     */
    public String getGod() {
        return god;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasAlwaysActiveModifier() {
        return alwaysActiveModifier;
    }

    /**
     *
     * @return the God Controller associated with this card
     */
    public GodController getController() {
        return controller;
    }

}
