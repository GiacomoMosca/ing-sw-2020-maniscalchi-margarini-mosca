package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

public class ChooseNickname extends ToClientMessage {

    protected boolean taken;

    public ChooseNickname(boolean taken) {
        super("choose nickname");
        this.taken = taken;
    }

    @Override
    public void performAction(UI client) {
        client.chooseNickname(taken);
    }

}
