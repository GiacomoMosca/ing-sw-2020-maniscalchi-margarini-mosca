package it.polimi.ingsw.network.message;

import java.io.Serializable;

public abstract class Message implements Serializable {

    protected Object body;

    public Message(Object body) {
        this.body = body;
    }

    public Object getBody() {
        return body;
    }

}
