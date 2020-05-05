package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.CardView;
import it.polimi.ingsw.view.UI;

import java.util.ArrayList;

public class ChooseCards extends ToClientMessage {

    protected int num;
    protected ArrayList<CardView> pickedCards;

    public ChooseCards(Object body, int num, ArrayList<CardView> pickedCards) {
        super(body);
        this.num = num;
        this.pickedCards = pickedCards;
    }

    @Override
    public void performAction(UI client) {
        client.chooseCards((ArrayList<CardView>) body, num, pickedCards);
    }
}
