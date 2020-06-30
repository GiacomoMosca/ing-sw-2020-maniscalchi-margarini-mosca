package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.PlayerView;
import it.polimi.ingsw.view.UI;

/**
 * Message sent from the server to a client to notify him that he lost the current Game.
 */
public class NotifyLoss extends ToClientMessage {

    protected String reason;
    protected PlayerView winner;

    /**
     * NotifyLoss constructor.
     * Calls the super constructor so that the msgId is set to "notify loss".
     *
     * @param reason a String describing the reason of the loss, can be:
     *               <ul>
     *                      <li>"outOfWorkers" if the Player lost because he ran out of Workers;
     *                      <li>"outOfMoves" if the Player lost because he ran out of moves;
     *                      <li>"outOfBuilds" if the Player lost because he ran out of builds;
     *                      <li>"godConditionAchieved" if the Player lost because another Worker achieved his God's win condition;
     *                      <li>"winConditionAchieved" if the Player lost because another Worker achieved the normal win condition;
     *               </ul>
     * @param winner the PlayerView of the winning Player if the winner achieved a win condition, null otherwise
     */
    public NotifyLoss(String reason, PlayerView winner) {
        super("notify loss");
        this.reason = reason;
        this.winner = winner;
    }

    /**
     * When called, this method invokes the right method on the client who received this message to perform the action requested by the message.
     * In this case, the client will display on screen a notification informing that he lost.
     *
     * @param client the User Interface representing the client this message is sent to
     */
    @Override
    public void performAction(UI client) {
        client.notifyLoss(reason, winner);
    }

}
