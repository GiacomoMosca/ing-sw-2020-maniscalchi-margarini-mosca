package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

/**
 * Message sent from the server to a client to ask him to choose the number of players for the Game.
 */
public class ChoosePlayersNumber extends ToClientMessage {

    /**
     * ChoosePlayersNumber constructor.
     * Calls the super constructor so that the msgId is set to "choose players number".
     */
    public ChoosePlayersNumber() {
        super("choose players number");
    }

    /**
     * When called, this method invokes the right method on the client who received this message to perform the action requested by the message.
     * In this case, the client will be asked to choose the number of players.
     *
     * @param client the User Interface representing the client this message is sent to
     */
    @Override
    public void performAction(UI client) {
        client.choosePlayersNumber();
    }

}
