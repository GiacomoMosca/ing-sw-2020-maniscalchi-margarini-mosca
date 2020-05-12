package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.PlayerView;
import it.polimi.ingsw.view.UI;

import java.util.ArrayList;

public class ChooseStartingPlayer extends ToClientMessage {

    public ChooseStartingPlayer(Object body) {
        super(body);
    }

    @Override
    public void performAction(UI client) {
        client.chooseStartingPlayer((ArrayList<PlayerView>) body);
    }

}
