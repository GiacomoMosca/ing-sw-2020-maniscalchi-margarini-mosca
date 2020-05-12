package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.PlayerView;
import it.polimi.ingsw.view.UI;

public class NotifyDisconnection extends ToClientMessage {

    public NotifyDisconnection(Object body) {
        super(body);
    }

    @Override
    public void performAction(UI client) {
        client.notifyDisconnection((PlayerView) body);
    }

}
