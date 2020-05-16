package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

public class DisplayMessage extends ToClientMessage {

    protected String message;

    public DisplayMessage(String message) {
        super("display message");
        this.message = message;
    }

    @Override
    public void performAction(UI client) {
        client.displayMessage(message);
    }

}
