package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.PlayerView;
import it.polimi.ingsw.view.UI;

import java.util.ArrayList;

public class ChooseStartingPlayer extends ToClientMessage {

    protected ArrayList<PlayerView> players;

    public ChooseStartingPlayer(ArrayList<PlayerView> players) {
        super("choose starting player");
        this.players = players;
    }

    @Override
    public void performAction(UI client) {
        client.chooseStartingPlayer(players);
    }

}
