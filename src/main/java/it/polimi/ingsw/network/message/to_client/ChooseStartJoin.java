package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

/**
 * Message sent from the server to a client to ask him to choose between starting a new Game or joining an existing one.
 */
public class ChooseStartJoin extends ToClientMessage {

    /**
     * ChooseStartingPlayer constructor.
     * Calls the super constructor so that the msgId is set to "choose starting player".
     */
    public ChooseStartJoin() {
        super("choose start/join");
    }

    /**
     * When called, this method invokes the right method on the client who received this message to perform the action requested by the message.
     * In this case, the client will be asked to choose between starting a new Game or joining an existing one.
     *
     * @param client the User Interface representing the client this message is sent to
     */
    @Override
    public void performAction(UI client) {
        client.chooseStartJoin();
    }

}
