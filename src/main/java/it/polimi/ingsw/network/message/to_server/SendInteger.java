package it.polimi.ingsw.network.message.to_server;

/**
 * Message sent from a client to the server to send an integer.
 */
public class SendInteger extends ToServerMessage {

    /**
     * The body of the message.
     */
    protected int body;

    /**
     * SendInteger constructor.
     * Calls the super constructor so that the msgId is set to "send int".
     *
     * @param playerId the ID of the sender
     * @param body     the body of the message, ie the Integer sent with this SendInteger Message
     */
    public SendInteger(String playerId, int body) {
        super("send int", playerId);
        this.body = body;
    }

    /**
     * @return the body of the message, ie the Integer sent with this SendInteger Message
     */
    public int getBody() {
        return body;
    }

}
