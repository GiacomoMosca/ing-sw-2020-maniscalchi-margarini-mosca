package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.CardView;
import it.polimi.ingsw.view.GameView;
import it.polimi.ingsw.view.UI;

public class DisplayBoard extends ToClientMessage {

    protected String desc;
    protected CardView godPower;

    public DisplayBoard(Object body, String desc, CardView godPower) {
        super(body);
        this.desc = desc;
        this.godPower = godPower;
    }

    @Override
    public void performAction(UI client) {
        client.displayBoard((GameView) body, desc, godPower);
    }
}
