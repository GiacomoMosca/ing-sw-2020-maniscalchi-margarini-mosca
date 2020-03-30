package it.polimi.ingsw.model.Cards;

import it.polimi.ingsw.controller.GodController;

public class Card {

    private final String god;
    private final GodController controller;

    /**
     * creates a card, setting its attributes to the values passed as arguments
     *
     * @param god the name of the God represented by the card
     * @param controller the God Controller associated with the card
     */
    public Card(String god, GodController controller) {
        this.god = god;
        this.controller = controller;
    }

    /**
     *
     * @return the god associated with this card
     */
    public String getGod() {
        return god;
    }

    /**
     *
     * @return the God Controller associated with this card
     */
    public GodController getController() {
        return controller;
    }

}
