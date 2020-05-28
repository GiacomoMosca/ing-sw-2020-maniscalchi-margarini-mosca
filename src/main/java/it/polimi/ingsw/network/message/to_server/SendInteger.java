package it.polimi.ingsw.network.message.to_server;

public class SendInteger extends ToServerMessage {

    protected int body;

    public SendInteger(String playerId, int body) {
        super("send int", playerId);
        this.body = body;
    }

    public int getBody() {
        return body;
    }

}
