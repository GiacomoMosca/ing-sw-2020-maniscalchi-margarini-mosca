package it.polimi.ingsw.network.message.to_server;

/**
 * Ping reply sent from a client to the server.
 */
public class Pong extends ToServerMessage {

    /**
     * @param playerId the ID of the sender
     */
    public Pong(String playerId) {
        super("pong", playerId);
    }

}
