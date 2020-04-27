package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.PlayerView;
import it.polimi.ingsw.view.UI;

public class NotifyWin extends ToClientMessage {

    public NotifyWin(Object body) {
        super(body);
    }

    @Override
    public void performAction(UI client) {
        client.notifyWin((PlayerView) body);
    }
}
