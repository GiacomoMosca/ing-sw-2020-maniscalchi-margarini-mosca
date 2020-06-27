package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.PlayerView;
import it.polimi.ingsw.view.UI;

/**
 * The NotifyWin message is used to send a message from the server to a client, to notify a Player he won.
 */
public class NotifyWin extends ToClientMessage {

    protected String reason;

    /**
     * NotifyWin constructor.
     * Calls the super constructor so that the msgId is set to "notify win".
     *
     * @param reason a String, can be:
     *               <p><ul>
     *               <li> "godConditionAchieved", if the Player won by achieving his God's win condition
     *               <li> "winConditionAchieved",  if the Player won by achieving the normal win condition
     *               <li> "outOfWorkers",  if the Player won because the only Player left was eliminated (he ran out of Workers)
     *               <li> "outOfMoves", if the Player won because the only Player left was eliminated (he ran out of moves)
     *               <li> "outOfBuilds", if the Player won because the only Player left was eliminated (he ran out of builds)
     *               </ul></p>
     */
    public NotifyWin(String reason) {
        super("notify win");
        this.reason = reason;
    }

    /**
     * When called, this method invokes the right method on the client who received this message to perform the action requested by the message.
     * In this case, the client will display on screen a notification informing that he won.
     *
     * @param client the User Interface representing the client this message is sent to
     */
    @Override
    public void performAction(UI client) {
        client.notifyWin(reason);
    }

}
