package it.polimi.ingsw.network.message.to_server;

import java.util.ArrayList;

public class SendIntegers extends ToServerMessage {

    protected ArrayList<Integer> body;

    public SendIntegers(String playerId, ArrayList<Integer> body) {
        super("send int", playerId);
        this.body = body;
    }

    public ArrayList<Integer> getBody() {
        return body;
    }

}
