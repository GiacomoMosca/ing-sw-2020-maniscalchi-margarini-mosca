package it.polimi.ingsw.network.message.to_server;

/**
 * The Pong class is used to send a Pong from a client to notify to the server that he is active.
 * Generally a Pong is a reply to a Ping sent by the server.
 */
public class Pong extends ToServerMessage {

    /**
     * @param playerId the ID of the sender
     */
    public Pong(String playerId) {
        super("pong", playerId);
    }

}
