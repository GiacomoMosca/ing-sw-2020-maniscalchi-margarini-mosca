package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.CardView;
import it.polimi.ingsw.view.CellView;
import it.polimi.ingsw.view.UI;

import java.util.HashMap;

public class DisplayMove extends ToClientMessage {

    protected HashMap<CellView, CellView> moves;
    protected CardView godCard;

    public DisplayMove(HashMap<CellView, CellView> moves, CardView godCard) {
        super("display move");
        this.moves = moves;
        this.godCard = godCard;
    }

    @Override
    public void performAction(UI client) {
        client.displayMove(moves, godCard);
    }

}
