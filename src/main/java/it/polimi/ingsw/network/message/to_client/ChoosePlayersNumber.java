package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

public class ChoosePlayersNumber extends ToClientMessage {

    public ChoosePlayersNumber() {
        super("choose players number");
    }

    @Override
    public void performAction(UI client) {
        client.choosePlayersNumber();
    }

}
