package it.polimi.ingsw.network.message.to_server;

public class SendString extends ToServerMessage {

    protected String body;

    public SendString(String playerId, String body) {
        super("send int", playerId);
        this.body = body;
    }

    public String getBody() {
        return body;
    }

}
