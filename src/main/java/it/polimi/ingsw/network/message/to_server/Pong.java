package it.polimi.ingsw.network.message.to_server;

public class Pong extends ToServerMessage {

    public Pong(String playerId) {
        super("pong", playerId);
    }

}
