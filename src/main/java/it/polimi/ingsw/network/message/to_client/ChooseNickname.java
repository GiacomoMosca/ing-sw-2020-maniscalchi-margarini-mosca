package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

import java.util.ArrayList;

public class ChooseNickname extends ToClientMessage {

    public ChooseNickname(Object body) {
        super(body);
    }

    @Override
    public void performAction(UI client) {
        client.chooseNickname((ArrayList<String>) body);
    }
}
