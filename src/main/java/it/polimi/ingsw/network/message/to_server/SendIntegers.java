package it.polimi.ingsw.network.message.to_server;

import java.util.ArrayList;

/**
 * Message sent from a client to the server to send an ArrayList of Integers.
 */
public class SendIntegers extends ToServerMessage {

    /**
     * The body of the message.
     */
    protected ArrayList<Integer> body;

    /**
     * SendIntegers constructor.
     * Calls the super constructor so that the msgId is set to "send ints".
     *
     * @param playerId the ID of the sender
     * @param body     the body of the message, ie the ArrayList of Integers sent with this SendIntegers Message
     */
    public SendIntegers(String playerId, ArrayList<Integer> body) {
        super("send ints", playerId);
        this.body = body;
    }

    /**
     * @return the body of the message, ie the ArrayList of Integers sent with this SendIntegers Message
     */
    public ArrayList<Integer> getBody() {
        return body;
    }

}
