package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.PlayerView;
import it.polimi.ingsw.view.UI;

public class NotifyLoss extends ToClientMessage {

    protected String reason;

    public NotifyLoss(Object body, String reason) {
        super(body);
        this.reason = reason;
    }

    @Override
    public void performAction(UI client) {
        client.notifyLoss((PlayerView) body, reason);
    }

}
