package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.PlayerView;
import it.polimi.ingsw.view.UI;

public class NotifyWin extends ToClientMessage {

    protected PlayerView player;
    protected String reason;

    public NotifyWin(PlayerView player, String reason) {
        super("notify win");
        this.player = player;
        this.reason = reason;
    }

    @Override
    public void performAction(UI client) {
        client.notifyWin(player, reason);
    }

}
