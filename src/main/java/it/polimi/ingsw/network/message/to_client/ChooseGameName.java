package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

public class ChooseGameName extends ToClientMessage {

    public ChooseGameName(Object body) {
        super(body);
    }

    @Override
    public void performAction(UI client) {client.chooseGameName();}
}
