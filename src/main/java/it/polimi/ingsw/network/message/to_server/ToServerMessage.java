package it.polimi.ingsw.network.message.to_server;

import it.polimi.ingsw.network.message.Message;

public class ToServerMessage extends Message {

    protected String playerId;

    public ToServerMessage(String msgId, String playerId) {
        super(msgId);
        this.playerId = playerId;
    }

    public String getSender() {
        return playerId;
    }

}
