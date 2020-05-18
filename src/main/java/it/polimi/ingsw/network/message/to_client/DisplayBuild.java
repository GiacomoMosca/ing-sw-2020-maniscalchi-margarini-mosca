package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.CardView;
import it.polimi.ingsw.view.CellView;
import it.polimi.ingsw.view.UI;

import java.util.HashMap;

public class DisplayBuild extends ToClientMessage {

    protected CellView buildPosition;
    protected CardView godCard;

    public DisplayBuild(CellView buildPosition, CardView godCard) {
        super("display builds");
        this.buildPosition = buildPosition;
        this.godCard = godCard;
    }

    @Override
    public void performAction(UI client) {
        client.displayBuild(buildPosition, godCard);
    }

}
