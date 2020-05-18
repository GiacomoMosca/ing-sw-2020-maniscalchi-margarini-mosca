package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

public class NotifyGameOver extends ToClientMessage {

    public NotifyGameOver() {
        super("notify game over");
    }

    @Override
    public void performAction(UI client) {
        client.notifyGameOver();
    }

}
