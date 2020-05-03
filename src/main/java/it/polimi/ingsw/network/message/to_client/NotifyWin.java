package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.PlayerView;
import it.polimi.ingsw.view.UI;

public class NotifyWin extends ToClientMessage {

    protected String reason;

    public NotifyWin(Object body, String reason) {
        super(body);
        this.reason = reason;
    }

    @Override
    public void performAction(UI client) {
        client.notifyWin((PlayerView) body, reason);
    }
}
