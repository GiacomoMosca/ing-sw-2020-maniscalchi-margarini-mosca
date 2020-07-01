package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

/**
 * Message sent from the server to a client to display a message from the server.
 */
public class DisplayMessage extends ToClientMessage {

    /**
     * The message sent.
     */
    protected String message;

    /**
     * DisplayMessage constructor.
     * Calls the super constructor so that the msgId is set to "display message".
     * Sets the attributes to the values received as arguments.
     *
     * @param message the String describing the message sent with this DisplayMessage message
     */
    public DisplayMessage(String message) {
        super("display message");
        this.message = message;
    }

    /**
     * When called, this method invokes the right method on the client who received this message to perform the action requested by the message.
     * In this case, the client will display on screen the message.
     *
     * @param client the User Interface representing the client this message is sent to
     */
    @Override
    public void performAction(UI client) {
        client.displayMessage(message);
    }

}
