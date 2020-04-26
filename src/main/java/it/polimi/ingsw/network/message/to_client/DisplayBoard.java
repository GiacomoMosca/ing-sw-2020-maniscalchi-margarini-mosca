package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.BoardView;
import it.polimi.ingsw.view.UI;

public class DisplayBoard extends ToClientMessage {

    public DisplayBoard(Object body) {
        super(body);
    }

    @Override
    public void performAction(UI client) {
        client.displayBoard((BoardView) body);
    }
}
