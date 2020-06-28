package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

/**
 * The ChooseYesNo message is used to send a message from the server to a client, to ask him to choose a position between those available.
 */
public class ChooseYesNo extends ToClientMessage {

    protected String query;

    /**
     * ChooseYesNo constructor.
     * Calls the super constructor so that the msgId is set to "choose yes/no".
     * Sets the attribute query to the value received as argument.
     *
     * @param query the question to ask to the client
     */
    public ChooseYesNo(String query) {
        super("choose yes/no");
        this.query = query;
    }

    /**
     * When called, this method invokes the right method on the client who received this message to perform the action requested by the message.
     * In this case, the client will be asked to answer to a "yes or no question".
     *
     * @param client the User Interface representing the client this message is sent to
     */
    @Override
    public void performAction(UI client) {
        client.chooseYesNo(query);
    }

}
