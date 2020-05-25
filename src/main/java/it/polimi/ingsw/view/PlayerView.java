package it.polimi.ingsw.view;

import it.polimi.ingsw.model.players.Player;

import java.io.Serializable;

public class PlayerView implements Serializable {

    private final String id;
    private final String color;
    private final CardView godCard;
    private final boolean hasLost;

    public PlayerView(Player player) {
        this.id = player.getId();
        this.color = player.getColor();
        if (player.getGodCard() == null) this.godCard = null;
        else this.godCard = new CardView(player.getGodCard());
        this.hasLost = player.hasLost();
    }

    public PlayerView(String id, String color, CardView godCard, boolean hasLost) {
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

    public CardView getGodCard() {
        return godCard;
    }

    public boolean hasLost() {
        return hasLost;
    }

}
