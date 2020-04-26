package it.polimi.ingsw.view;

import it.polimi.ingsw.model.players.Player;

public class PlayerView {

    private final String id;
    private final String color;
    private final String godCard;

    public PlayerView (Player player) {
        this.id = player.getId();
        this.color = player.getColor();
        this.godCard = player.getGodCard().getGod();
    }

    public PlayerView (String id, String color, String godCard) {
        this.id = id;
        this.color = color;
        this.godCard = godCard;
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
}
