package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.CardView;
import it.polimi.ingsw.view.GameView;
import it.polimi.ingsw.view.UI;

public class UpdateGame extends ToClientMessage {

    protected String desc;
    protected CardView godPower;

    public UpdateGame(Object body, String desc, CardView godPower) {
        super(body);
        this.desc = desc;
        this.godPower = godPower;
    }

    @Override
    public void performAction(UI client) {
        client.updateGame((GameView) body, desc, godPower);
    }
}
