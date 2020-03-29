package it.polimi.ingsw.model.TurnData;

import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Players.Player;

public class OpponentModifier {

    private final Player player;
    private final Card godCard;
    private final boolean alwaysActive;

    public OpponentModifier(Player player, boolean alwaysActive) {
        this.player = player;
        this.godCard = player.getGodCard();
        this.alwaysActive = alwaysActive;
    }

    public Player getPlayer() {
        return player;
    }

    public Card getGodCard() {
        return godCard;
    }

    public boolean isAlwaysActive() {
        return alwaysActive;
    }

}
