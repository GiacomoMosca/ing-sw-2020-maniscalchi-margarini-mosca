package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

/**
 * Message sent from the server to a client to ask him to choose the name of the Game.
 */
public class ChooseGameName extends ToClientMessage {

    protected boolean taken;

    /**
     * ChooseGameName constructor.
     * Calls the super constructor so that the msgId is set to "choose game name".
     * Sets the attribute taken as the value received as argument.
     *
     * @param taken true if the previously chosen Game name is already taken, false otherwise
     */
    public ChooseGameName(boolean taken) {
        super("choose game name");
        this.taken = taken;
    }

    /**
     * When called, this method invokes the right method on the client who received this message to perform the action requested by the message.
     * In this case, the client will be asked to choose the Game name.
     *
     * @param client the User Interface representing the client this message is sent to
     */
    @Override
    public void performAction(UI client) {
        client.chooseGameName(taken);
    }

}
