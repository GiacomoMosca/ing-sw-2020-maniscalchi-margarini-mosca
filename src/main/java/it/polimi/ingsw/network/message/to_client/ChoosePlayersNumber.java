package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

import java.util.ArrayList;

public class ChoosePlayersNumber extends ToClientMessage {

    public ChoosePlayersNumber(Object body) {
        super(body);
    }

    @Override
    public void performAction(UI client) {
        client.choosePlayersNumber();
    }
}
