package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

/**
 * Message sent from the server to a client to ask him to choose his nickname.
 */
public class ChooseNickname extends ToClientMessage {

    protected boolean taken;

    /**
     * ChooseNickname constructor.
     * Calls the super constructor so that the msgId is set to "choose nickname".
     * Sets the attribute taken as the value received as argument.
     *
     * @param taken true if the previously chosen nickname is already taken, false otherwise
     */
    public ChooseNickname(boolean taken) {
        super("choose nickname");
        this.taken = taken;
    }

    /**
     * When called, this method invokes the right method on the client who received this message to perform the action requested by the message.
     * In this case, the client will be asked to choose his nickname.
     *
     * @param client the User Interface representing the client this message is sent to
     */
    @Override
    public void performAction(UI client) {
        client.chooseNickname(taken);
    }

}
