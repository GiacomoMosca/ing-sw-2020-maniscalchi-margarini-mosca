package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.PlayerView;
import it.polimi.ingsw.view.UI;

public class NotifyLoss extends ToClientMessage {

    protected PlayerView player;
    protected String reason;

    public NotifyLoss(PlayerView player, String reason) {
        super("notify loss");
        this.player = player;
        this.reason = reason;
    }

    @Override
    public void performAction(UI client) {
        client.notifyLoss(player, reason);
    }

}
