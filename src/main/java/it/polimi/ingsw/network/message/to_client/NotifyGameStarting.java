package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

/**
 * The NotifyDisconnection message is used to send a message from the server to a client, to notify him the disconnection of a Player.
 */
public class NotifyGameStarting extends ToClientMessage {

    /**
     * NotifyGameStarting constructor.
     * Calls the super constructor so that the msgId is set to "notify game starting".
     */
    public NotifyGameStarting() {
        super("notify game starting");
    }

    /**
     * When called, this method invokes the right method on the client who received this message to perform the action requested by the message.
     * In this case, the client will display on screen a notification informing that the Game is starting.
     *
     * @param client the User Interface representing the client this message is sent to
     */
    @Override
    public void performAction(UI client) {
        client.notifyGameStarting();
    }

}