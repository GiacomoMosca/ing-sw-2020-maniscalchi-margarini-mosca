package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

/**
 * The NotifyGameOver message is used to send a message from the server to a client, to notify him the Game is over.
 */
public class NotifyGameOver extends ToClientMessage {

    /**
     * NotifyGameOver constructor.
     * Calls the super constructor so that the msgId is set to "notify game over".
     */
    public NotifyGameOver() {
        super("notify game over");
    }

    /**
     * When called, this method invokes the right method on the client who received this message to perform the action requested by the message.
     * In this case, the client will display on screen a notification to inform that the Game is over.
     *
     * @param client the User Interface representing the client this message is sent to
     */
    @Override
    public void performAction(UI client) {
        client.notifyGameOver();
    }

}
