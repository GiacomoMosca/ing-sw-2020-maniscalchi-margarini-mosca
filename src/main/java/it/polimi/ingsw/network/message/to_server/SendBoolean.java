package it.polimi.ingsw.network.message.to_server;

public class SendBoolean extends ToServerMessage {

    protected boolean body;

    public SendBoolean(String playerId, boolean body) {
        super("send int", playerId);
        this.body = body;
    }

    public boolean getBody() {
        return body;
    }

}
