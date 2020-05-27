package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

public class NotifyGameStarting extends ToClientMessage {

    public NotifyGameStarting() {
        super("notify game starting");
    }

    @Override
    public void performAction(UI client) {
        client.notifyGameStarting();
    }

}