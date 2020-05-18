package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

public class ChooseStartJoin extends ToClientMessage {

    public ChooseStartJoin() {
        super("choose start/join");
    }

    @Override
    public void performAction(UI client) {
        client.chooseStartJoin();
    }

}
