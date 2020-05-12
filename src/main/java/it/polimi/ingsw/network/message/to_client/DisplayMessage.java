package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

public class DisplayMessage extends ToClientMessage {

    public DisplayMessage(Object body) {
        super(body);
    }

    @Override
    public void performAction(UI client) {
        client.displayMessage((String) body);
    }

}
