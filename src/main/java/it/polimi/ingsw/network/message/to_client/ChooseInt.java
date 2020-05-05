package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

public class ChooseInt extends ToClientMessage {

    int max;

    public ChooseInt(Object body,int max) {
        super(body);
        this.max = max;
    }

    @Override
    public void performAction(UI client) {
        client.chooseInt((String) body, max);
    }
}
