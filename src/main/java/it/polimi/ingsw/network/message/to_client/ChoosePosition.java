package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.CellView;
import it.polimi.ingsw.view.UI;

import java.util.ArrayList;

public class ChoosePosition extends ToClientMessage {

    protected ArrayList<CellView> positions;
    protected String desc;

    public ChoosePosition(ArrayList<CellView> positions, String desc) {
        super("choose position");
        this.positions = positions;
        this.desc = desc;
    }

    @Override
    public void performAction(UI client) {
        client.choosePosition(positions, desc);
    }

}
