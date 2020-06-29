package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.CardView;
import it.polimi.ingsw.view.CellView;
import it.polimi.ingsw.view.UI;

import java.util.HashMap;

/**
 * The DisplayMove message is used to send a message from the server to a client, to notify him a new move occurred.
 */
public class DisplayMove extends ToClientMessage {

    protected HashMap<CellView, CellView> moves;
    protected CardView godCard;

    /**
     * DisplayMove constructor.
     * Calls the super constructor so that the msgId is set to "display move".
     * Sets the attributes to the values received as arguments.
     *
     * @param moves   an HashMap containing:
     *                • a pair of CellViews if only one Worker moved;
     *                • two pairs of CellViews if two Workers moved (in this case the godPower parameter mustn't be null: this kind of double move is only allowed thanks to a God Power).
     * @param godCard the CardView representing the God Power which eventually allowed this build
     */
    public DisplayMove(HashMap<CellView, CellView> moves, CardView godCard) {
        super("display move");
        this.moves = moves;
        this.godCard = godCard;
    }

    /**
     * When called, this method invokes the right method on the client who received this message to perform the action requested by the message.
     * In this case, the client will display on screen the new moves.
     *
     * @param client the User Interface representing the client this message is sent to
     */
    @Override
    public void performAction(UI client) {
        client.displayMove(moves, godCard);
    }

}
