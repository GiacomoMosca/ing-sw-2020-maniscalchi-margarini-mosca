package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.GameView;
import it.polimi.ingsw.view.UI;

import java.util.ArrayList;

/**
 * Message sent from the server to a client to ask him to choose the Game room to play in.
 */
public class ChooseGameRoom extends ToClientMessage {

    protected ArrayList<GameView> games;

    /**
     * ChooseGameRoom constructor.
     * Calls the super constructor so that the msgId is set to "choose game room".
     * Sets the attribute games as the value received as argument.
     *
     * @param games an ArrayList containing all the currently active Game rooms
     */
    public ChooseGameRoom(ArrayList<GameView> games) {
        super("choose game room");
        this.games = games;
    }

    /**
     * When called, this method invokes the right method on the client who received this message to perform the action requested by the message.
     * In this case, the client will be asked to choose the Game room to play in.
     *
     * @param client the User Interface representing the client this message is sent to
     */
    @Override
    public void performAction(UI client) {
        client.chooseGameRoom(games);
    }

}
