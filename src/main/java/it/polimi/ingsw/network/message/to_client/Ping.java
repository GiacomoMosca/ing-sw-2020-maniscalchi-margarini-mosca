package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

public class Ping extends ToClientMessage {

    public Ping() {
        super("ping");
    }

    @Override
    public void performAction(UI client) {
    }

}
