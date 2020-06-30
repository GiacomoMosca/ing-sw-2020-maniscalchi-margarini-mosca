package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.view.UI;

/**
 * Abstract class that represents a generic message sent from the server to a client.
 */
public abstract class ToClientMessage extends Message {

    /**
     * ToClientMessage constructor.
     * Sets the msgId attribute to the value received as argument.
     *
     * @param msgId the ID of the message
     */
    public ToClientMessage(String msgId) {
        super(msgId);
    }

    /**
     * Allows every message from the server to be related to an action on the client.
     *
     * @param client the User Interface representing the client this message is sent to
     */
    public abstract void performAction(UI client);

}
