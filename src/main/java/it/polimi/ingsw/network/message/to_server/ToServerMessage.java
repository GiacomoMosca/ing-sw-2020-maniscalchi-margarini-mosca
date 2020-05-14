package it.polimi.ingsw.network.message.to_server;

import it.polimi.ingsw.network.message.Message;

public class ToServerMessage extends Message {

    private final String playerId;

    public ToServerMessage(Object body, String playerId) {
        super(body);
        this.playerId = playerId;
    }

    public String getSender() {
        return playerId;
    }

}
