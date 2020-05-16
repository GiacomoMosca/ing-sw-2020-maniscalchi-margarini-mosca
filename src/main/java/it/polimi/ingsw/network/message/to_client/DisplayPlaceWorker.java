package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.CellView;
import it.polimi.ingsw.view.UI;

public class DisplayPlaceWorker extends ToClientMessage {

    protected CellView position;

    public DisplayPlaceWorker(CellView position) {
        super("display place worker");
        this.position = position;
    }

    @Override
    public void performAction(UI client) {
        client.displayPlaceWorker(position);
    }
}
