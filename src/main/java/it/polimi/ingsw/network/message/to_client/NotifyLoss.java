package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.PlayerView;
import it.polimi.ingsw.view.UI;

public class NotifyLoss extends ToClientMessage {

    protected String reason;
    protected PlayerView winner;

    public NotifyLoss(String reason, PlayerView winner) {
        super("notify loss");
        this.reason = reason;
        this.winner = winner;
    }

    @Override
    public void performAction(UI client) {
        client.notifyLoss(reason, winner);
    }

}
