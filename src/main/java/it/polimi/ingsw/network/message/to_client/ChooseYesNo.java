package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.UI;

public class ChooseYesNo extends ToClientMessage {

    protected String query;

    public ChooseYesNo(String query) {
        super("choose yes/no");
        this.query = query;
    }

    @Override
    public void performAction(UI client) {
        client.chooseYesNo(query);
    }

}
