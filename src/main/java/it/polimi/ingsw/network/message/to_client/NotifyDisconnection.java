package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.PlayerView;
import it.polimi.ingsw.view.UI;

public class NotifyDisconnection extends ToClientMessage {

    protected PlayerView player;

    public NotifyDisconnection(PlayerView player) {
        super("notify disconnection");
        this.player = player;
    }

    @Override
    public void performAction(UI client) {
        client.notifyDisconnection(player);
    }

}
