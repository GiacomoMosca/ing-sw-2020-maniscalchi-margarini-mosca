package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.CardView;
import it.polimi.ingsw.view.CellView;
import it.polimi.ingsw.view.UI;

import java.util.HashMap;

/**
 * Message sent from the server to a client to display a new move.
 */
public class DisplayMove extends ToClientMessage {

    /**
     * A Hash Map containing a pair (if only one Worker moved) or two pairs (if two Workers moved) of CellViews.
     */
    protected HashMap<CellView, CellView> moves;
    /**
     * The CardView representing the God Power which eventually allowed this build
     */
    protected CardView godCard;

    /**
     * DisplayMove constructor.
     * Calls the super constructor so that the msgId is set to "display move".
     * Sets the attributes to the values received as arguments.
     *
     * @param moves   a HashMap containing:
     *                <ul>
     *                      <li>a pair of CellViews if only one Worker moved;
     *                      <li>two pairs of CellViews if two Workers moved (only allowed thanks to a God Power: godCard will not be null).
     *                </ul>
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
