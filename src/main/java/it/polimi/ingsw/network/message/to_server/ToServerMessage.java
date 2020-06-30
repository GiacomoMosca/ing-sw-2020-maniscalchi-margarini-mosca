package it.polimi.ingsw.network.message.to_server;

import it.polimi.ingsw.network.message.Message;

/**
 * Abstract class that represents a generic message sent from a client to the server.
 */
public class ToServerMessage extends Message {

    /**
     * The ID of the sender.
     */
    protected String playerId;

    /**
     * ToServerMessage constructor.
     * Sets the attributes to the values received as arguments: calls the super constructor to set the value of the attribute msgId, and sets the playerId attribute to the playerId String received as argument.
     *
     * @param msgId    the ID of the message
     * @param playerId the ID of the Player who sent the message
     */
    public ToServerMessage(String msgId, String playerId) {
        super(msgId);
        this.playerId = playerId;
    }

    /**
     * @return the ID of the Player who sent the message
     */
    public String getSender() {
        return playerId;
    }

}
