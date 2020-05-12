package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

public class ChooseYesNo extends ToClientMessage {

    public ChooseYesNo(Object body) {
        super(body);
    }

    @Override
    public void performAction(UI client) {
        client.chooseYesNo((String) body);
    }

}
