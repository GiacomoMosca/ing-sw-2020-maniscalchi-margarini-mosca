package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.CellView;
import it.polimi.ingsw.view.UI;

import java.util.ArrayList;

public class ChoosePosition extends ToClientMessage {

    protected String desc;

    public ChoosePosition(Object body, String desc) {
        super(body);
        this.desc = desc;
    }

    @Override
    public void performAction(UI client) {
        client.choosePosition((ArrayList<CellView>) body, desc);
    }
}
