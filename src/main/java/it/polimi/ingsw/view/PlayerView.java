package it.polimi.ingsw.view;

import it.polimi.ingsw.model.players.Player;

import java.io.Serializable;

/**
 * The PLayerView class represents a view of the Player class.
 * It implements serializable so that it can be serialized in the messages that client and server exchange: in this way, the client won't have access to the Model objects.
 */
public class PlayerView implements Serializable {

    private final String id;
    private final String color;
    private final CardView godCard;
    private final boolean hasLost;

    /**
     * PlayerView constructor.
     * PlayerView attributes are set to the values of the same attributes of the Game Object received as an argument.
     *
     * @param player the Player represented by this PlayerView
     */
    public PlayerView(Player player) {
        this.id = player.getId();
        this.color = player.getColor();
        if (player.getGodCard() == null) this.godCard = null;
        else this.godCard = new CardView(player.getGodCard());
        this.hasLost = player.hasLost();
    }

    /**
     * PlayerView constructor.
     * PlayerView attributes are set to the values received as arguments.
     *
     * @param id      the ID of the Player represented by this PlayerView
     * @param color   the color of the Player represented by this PlayerView
     * @param godCard the God Card assigned to the Player represented by this PlayerView
     * @param hasLost true if the Player represented by this PlayerView has lost, false otherwise
     */
    public PlayerView(String id, String color, CardView godCard, boolean hasLost) {
        this.id = id;
        this.color = color;
        this.godCard = godCard;
        this.hasLost = hasLost;
    }

    /**
     * @return the ID of the Player represented by this PlayerView
     */
    public String getId() {
        return id;
    }

    /**
     * @return the color of the Player represented by this PlayerView
     */
    public String getColor() {
        return color;
    }

    /**
     * @return the CardView representing the God Card assigned to the Player represented by this PlayerView
     */
    public CardView getGodCard() {
        return godCard;
    }

    /**
     * @return true if the Player represented by this PlayerView has lost, false otherwise
     */
    public boolean hasLost() {
        return hasLost;
    }

}
