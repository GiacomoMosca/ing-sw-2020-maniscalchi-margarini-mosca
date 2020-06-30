package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

/**
 * Ping message sent from the server to a client.
 */
public class Ping extends ToClientMessage {

    /**
     * Ping constructor.
     * Calls the super constructor so that the msgId is set to "ping".
     */
    public Ping() {
        super("ping");
    }

    /**
     * @param client the User Interface representing the client this message is sent to
     */
    @Override
    public void performAction(UI client) {
    }

}
