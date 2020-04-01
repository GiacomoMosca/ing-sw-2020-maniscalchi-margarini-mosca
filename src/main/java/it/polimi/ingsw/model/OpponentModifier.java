package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.players.Player;

public class OpponentModifier {

    private final Player player;
    private final Card godCard;
    /**
     * true if the modifier associated with this card is always active
     */
    private final boolean alwaysActive;

    /**
     * sets the Modifier associated with a player
     *
     * @param player the player who owns the God Card associated with this Modifier
     * @param alwaysActive if the Modifier is always active during the Game
     */
    public OpponentModifier(Player player, boolean alwaysActive) {
        this.player = player;
        this.godCard = player.getGodCard();
        this.alwaysActive = alwaysActive;
    }

    /**
     *
     * @return the player associated with this Modifier
     */
    public Player getPlayer() {
        return player;
    }

    /**
     *
     * @return the Card associated with this Modifier
     */
    public Card getGodCard() {
        return godCard;
    }

    /**
     *
     * @return true if the Modifier is always active, false otherwise
     */
    public boolean isAlwaysActive() {
        return alwaysActive;
    }

}
