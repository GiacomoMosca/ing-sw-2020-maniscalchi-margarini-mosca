package it.polimi.ingsw.network.message;

import java.io.Serializable;

/**
 * Abstract class that represents information shared between client and server.
 * It describes the basic structure of a message and implements Serializable to allow the serialization of the message on the communication channel.
 */
public abstract class Message implements Serializable {

    protected String msgId;

    /**
     * @param msgId the message ID
     */
    public Message(String msgId) {
        this.msgId = "[" + java.time.LocalDateTime.now() + "] " + msgId;
    }

    /**
     * @return the message ID
     */
    public String getMsgId() {
        return msgId;
    }

}
