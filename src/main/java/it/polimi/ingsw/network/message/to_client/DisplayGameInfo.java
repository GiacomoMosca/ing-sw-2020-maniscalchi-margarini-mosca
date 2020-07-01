package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.GameView;
import it.polimi.ingsw.view.UI;

/**
 * Message sent from the server to a client to display new information about the current Game.
 */
public class DisplayGameInfo extends ToClientMessage {

    /**
     * The GameView representing the current state of the Game.
     */
    protected GameView game;
    /**
     * The String describing the information sent with this DisplayGameInfo message.
     */
    protected String desc;

    /**
     * DisplayGameInfo constructor.
     * Calls the super constructor so that the msgId is set to "display game info".
     * Sets the attributes to the values received as arguments.
     *
     * @param game the GameView representing the current state of the Game
     * @param desc the String describing the information sent with this DisplayGameInfo message
     */
    public DisplayGameInfo(GameView game, String desc) {
        super("display game info");
        this.game = game;
        this.desc = desc;
    }

    /**
     * When called, this method invokes the right method on the client who received this message to perform the action requested by the message.
     * In this case, the client will display on screen the Game information.
     *
     * @param client the User Interface representing the client this message is sent to
     */
    @Override
    public void performAction(UI client) {
        client.displayGameInfo(game, desc);
    }

}
