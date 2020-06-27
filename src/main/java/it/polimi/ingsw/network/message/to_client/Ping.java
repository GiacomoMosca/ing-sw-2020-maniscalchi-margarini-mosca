package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

/**
 * The Ping message is used to send a message from the server to a client, to receive an answer from that client to prove he is still active.
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
