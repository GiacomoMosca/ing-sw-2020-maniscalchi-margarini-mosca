package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

/**
 * Message sent from the server to a client to notify him that the server has closed.
 */
public class ServerClosed extends ToClientMessage {

    /**
     * ServerClosed constructor.
     * Calls the super constructor so that the msgId is set to "server closed".
     */
    public ServerClosed() {
        super("server closed");
    }

    /**
     * When called, this method invokes the right method on the client who received this message to perform the action requested by the message.
     * In this case, the client will display on screen a notification informing that the server is down and then close.
     *
     * @param client the User Interface representing the client this message is sent to
     */
    @Override
    public void performAction(UI client) {
        client.serverClosed();
    }

}
