package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

public class ChooseStartJoin extends ToClientMessage {

    public ChooseStartJoin(Object body) {
        super(body);
    }

    @Override
    public void performAction(UI client) {
        client.chooseStartJoin();
    }
}
