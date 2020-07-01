package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.CellView;
import it.polimi.ingsw.view.UI;

/**
 * Message sent from the server to a client to display a new Worker being placed.
 */
public class DisplayPlaceWorker extends ToClientMessage {

    /**
     * The CellView representing the starting position of a Worker.
     */
    protected CellView position;

    /**
     * DisplayPlaceWorker constructor.
     * Calls the super constructor so that the msgId is set to "display place worker".
     * Sets the attribute position to the value received as argument.
     *
     * @param position the CellView representing the starting position of a Worker
     */
    public DisplayPlaceWorker(CellView position) {
        super("display place worker");
        this.position = position;
    }

    /**
     * When called, this method invokes the right method on the client who received this message to perform the action requested by the message.
     * In this case, the client will display on screen the Worker on his starting position.
     *
     * @param client the User Interface representing the client this message is sent to
     */
    @Override
    public void performAction(UI client) {
        client.displayPlaceWorker(position);
    }
}
