package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.PlayerView;
import it.polimi.ingsw.view.UI;

import java.util.ArrayList;

/**
 * The ChooseStartingPlayer message is used to send a message from the server to a client, to ask him to choose the starting Player.
 */
public class ChooseStartingPlayer extends ToClientMessage {

    protected ArrayList<PlayerView> players;

    /**
     * ChooseStartingPlayer constructor.
     * Calls the super constructor so that the msgId is set to "choose starting player".
     * Sets the attribute players as the value received as argument.
     *
     * @param players an ArrayList containing all the Players to choose among
     */
    public ChooseStartingPlayer(ArrayList<PlayerView> players) {
        super("choose starting player");
        this.players = players;
    }

    /**
     * When called, this method invokes the right method on the client who received this message to perform the action requested by the message.
     * In this case, the client will be asked to choose the starting Player.
     *
     * @param client the User Interface representing the client this message is sent to
     */
    @Override
    public void performAction(UI client) {
        client.chooseStartingPlayer(players);
    }

}
