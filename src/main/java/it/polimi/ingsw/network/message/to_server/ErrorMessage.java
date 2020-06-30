package it.polimi.ingsw.network.message.to_server;

/**
 * Error message sent from a client to the server.
 */
public class ErrorMessage extends ToServerMessage {

    /**
     * ErrorMessage constructor.
     * Calls the super constructor so that the msgId is set to "error".
     *
     * @param description the body of the message, ie the String describing the error sent with this SendError Message
     */
    public ErrorMessage(String description) {
        super("error", description);
    }

}
