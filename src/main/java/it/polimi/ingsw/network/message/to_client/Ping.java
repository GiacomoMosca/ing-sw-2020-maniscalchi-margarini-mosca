package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

public class Ping extends ToClientMessage {

    public Ping(Object body) {
        super(body);
    }

    @Override
    public void performAction(UI client) {
        //
    }

}
