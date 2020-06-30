package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.PlayerView;
import it.polimi.ingsw.view.UI;

/**
 * Message sent from the server to a client to notify him about the disconnection of a Player.
 */
public class NotifyDisconnection extends ToClientMessage {

    protected PlayerView player;

    /**
     * NotifyDisconnection constructor.
     * Calls the super constructor so that the msgId is set to "notify disconnection".
     * Sets the attribute player to the value received as argument.
     *
     * @param player the PlayerView representing the Player who disconnected
     */
    public NotifyDisconnection(PlayerView player) {
        super("notify disconnection");
        this.player = player;
    }

    /**
     * When called, this method invokes the right method on the client who received this message to perform the action requested by the message.
     * In this case, the client will display on screen a notification about the disconnection of a Player.
     *
     * @param client the User Interface representing the client this message is sent to
     */
    @Override
    public void performAction(UI client) {
        client.notifyDisconnection(player);
    }

}
