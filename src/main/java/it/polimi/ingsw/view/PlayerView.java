package it.polimi.ingsw.view;

import it.polimi.ingsw.model.players.Player;

import java.io.Serializable;

public class PlayerView implements Serializable {

    private final String id;
    private final String color;
    private final String godCard;
    private final boolean hasLost;

    public PlayerView(Player player) {
        this.id = player.getId();
        this.color = player.getColor();
        this.godCard = player.getGodCard().getGod();
        this.hasLost = player.hasLost();
    }

    public PlayerView(String id, String color, String godCard, boolean hasLost) {
        this.id = id;
        this.color = color;
        this.godCard = godCard;
        this.hasLost = hasLost;
    }

    public String getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public String getGodCard() {
        return godCard;
    }

    public boolean hasLost() {
        return hasLost;
    }
}
