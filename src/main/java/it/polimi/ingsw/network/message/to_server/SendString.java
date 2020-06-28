package it.polimi.ingsw.network.message.to_server;

/**
 * The SendString class is used to send a String from a client to the server.
 */
public class SendString extends ToServerMessage {

    protected String body;

    /**
     * SendString constructor.
     * Calls the super constructor so that the msgId is set to "send string".
     *
     * @param playerId the ID of the sender
     * @param body     the body of the message, ie the String sent with this SendString Message
     */
    public SendString(String playerId, String body) {
        super("send string", playerId);
        this.body = body;
    }

    /**
     * @return the body of the message, ie the String sent with this SendString Message
     */
    public String getBody() {
        return body;
    }

}
