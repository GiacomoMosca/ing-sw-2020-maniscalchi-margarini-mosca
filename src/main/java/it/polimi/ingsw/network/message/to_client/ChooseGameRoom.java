package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.GameView;
import it.polimi.ingsw.view.UI;

import java.util.ArrayList;


public class ChooseGameRoom extends ToClientMessage {

    protected ArrayList<GameView> games;

    public ChooseGameRoom(ArrayList<GameView> games) {
        super("choose game room");
        this.games = games;
    }

    @Override
    public void performAction(UI client) {
        client.chooseGameRoom(games);
    }

}
