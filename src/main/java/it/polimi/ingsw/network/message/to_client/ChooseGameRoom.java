package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.GameView;
import it.polimi.ingsw.view.UI;

import java.util.ArrayList;


public class ChooseGameRoom extends ToClientMessage {

    public ChooseGameRoom(Object body) {
        super(body);
    }

    @Override
    public void performAction(UI client) {
        client.chooseGameRoom((ArrayList<GameView>) body);
    }

}
