package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

public class GameOver extends ToClientMessage {

    public GameOver(Object body) {
        super(body);
    }

    @Override
    public void performAction(UI client) {
        client.gameOver();
    }
}
