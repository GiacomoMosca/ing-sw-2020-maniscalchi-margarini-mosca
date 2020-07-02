package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.CardView;
import it.polimi.ingsw.view.CellView;
import it.polimi.ingsw.view.UI;

/**
 * Message sent from the server to a client to display a new build.
 */
public class DisplayBuild extends ToClientMessage {

    /**
     * The CellView representing the position of the build to show.
     */
    protected CellView buildPosition;
    /**
     * The CardView representing the God Power which eventually allowed this build.
     */
    protected CardView godCard;

    /**
     * DisplayBuild constructor.
     * Calls the super constructor so that the msgId is set to "display build".
     * Sets the attributes to the values received as arguments.
     *
     * @param buildPosition the CellView representing the position of the build to show
     * @param godCard       the CardView representing the God Power which eventually allowed this build
     */
    public DisplayBuild(CellView buildPosition, CardView godCard) {
        super("display build");
        this.buildPosition = buildPosition;
        this.godCard = godCard;
    }

    /**
     * When called, this method invokes the right method on the client who received this message to perform the action requested by the message.
     * In this case, the client will display on screen the new build.
     *
     * @param client the User Interface representing the client this message is sent to
     */
    @Override
    public void performAction(UI client) {
        client.displayBuild(buildPosition, godCard);
    }

}
