package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

public class ChooseGameName extends ToClientMessage {

    protected boolean taken;

    public ChooseGameName(boolean taken) {
        super("choose game name");
        this.taken = taken;
    }

    @Override
    public void performAction(UI client) {
        client.chooseGameName(taken);
    }

}
