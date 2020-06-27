package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.CellView;
import it.polimi.ingsw.view.UI;

import java.util.ArrayList;

/**
 * The ChoosePosition message is used to send a message from the server to a client, to ask him to choose a position between those available.
 */
public class ChoosePosition extends ToClientMessage {

    protected ArrayList<CellView> positions;
    protected String desc;

    /**
     * ChoosePosition constructor.
     * Calls the super constructor so that the msgId is set to "choose position".
     * Sets the attributes to the values received as arguments.
     *
     * @param positions an ArrayList containing all the positions the client can choose among
     * @param desc      the reason why he has to make this choice, can be
     *                  <p><ul>
     *                  <li> "start1", if the Player is asked to choose the starting position for his first Worker
     *                  <li> "start2", if the Player is asked to choose the starting position for his second Worker
     *                  <li> "worker", if the Player is asked to choose the Worker to use
     *                  <li> "move", if the Player is asked to choose the position to move to
     *                  <li> "build", if the Player is asked to choose the position to build in
     *                  </ul></p>
     */
    public ChoosePosition(ArrayList<CellView> positions, String desc) {
        super("choose position");
        this.positions = positions;
        this.desc = desc;
    }

    /**
     * When called, this method invokes the right method on the client who received this message to perform the action requested by the message.
     * In this case, the client will be asked to choose the position between those available.
     *
     * @param client the User Interface representing the client this message is sent to
     */
    @Override
    public void performAction(UI client) {
        client.choosePosition(positions, desc);
    }

}
