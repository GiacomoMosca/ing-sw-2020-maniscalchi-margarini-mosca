package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.PlayerView;
import it.polimi.ingsw.view.UI;

public class NotifyWin extends ToClientMessage {

    protected String reason;

    public NotifyWin(String reason) {
        super("notify win");
        this.reason = reason;
    }

    @Override
    public void performAction(UI client) {
        client.notifyWin(reason);
    }

}
