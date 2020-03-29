package it.polimi.ingsw.model.Cards;

import it.polimi.ingsw.controller.GodController;

public class Card {

    private final String god;
    private final GodController controller;

    public Card(String god, GodController controller) {
        this.god = god;
        this.controller = controller;
    }

    public String getGod() {
        return god;
    }

    public GodController getController() {
        return controller;
    }

}
