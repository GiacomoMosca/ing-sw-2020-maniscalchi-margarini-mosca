package it.polimi.ingsw.network.message;

import java.io.Serializable;

public abstract class Message implements Serializable {

    protected String msgId;

    public Message(String msgId) {
        this.msgId = "[" + java.time.LocalDateTime.now() + "] " + msgId;
    }

    public String getMsgId() {
        return msgId;
    }

}
