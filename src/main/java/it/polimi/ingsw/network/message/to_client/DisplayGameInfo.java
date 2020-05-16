package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.GameView;
import it.polimi.ingsw.view.UI;

public class DisplayGameInfo extends ToClientMessage {

    protected GameView game;
    protected String desc;

    public DisplayGameInfo(GameView game, String desc) {
        super("display game info");
        this.game = game;
        this.desc = desc;
    }

    @Override
    public void performAction(UI client) {
        client.displayGameInfo(game, desc);
    }

}
