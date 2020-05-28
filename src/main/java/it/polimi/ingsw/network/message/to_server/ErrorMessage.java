package it.polimi.ingsw.network.message.to_server;

public class ErrorMessage extends ToServerMessage{

    public ErrorMessage(String description) {
        super("error", description);
    }

}
