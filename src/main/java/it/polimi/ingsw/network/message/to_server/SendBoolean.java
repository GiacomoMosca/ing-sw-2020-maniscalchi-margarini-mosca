package it.polimi.ingsw.network.message.to_server;

/**
 * Message sent from a client to the server to send a boolean.
 */
public class SendBoolean extends ToServerMessage {

    /**
     * The body of the message.
     */
    protected boolean body;

    /**
     * SendBoolean constructor.
     * Calls the super constructor so that the msgId is set to "send boolean".
     *
     * @param playerId the ID of the sender
     * @param body     the body of the message, ie the boolean sent with this SendBoolean Message
     */
    public SendBoolean(String playerId, boolean body) {
        super("send boolean", playerId);
        this.body = body;
    }

    /**
     * @return the body of the message, ie the boolean sent with this SendBoolean Message
     */
    public boolean getBody() {
        return body;
    }

}
