package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.CardView;
import it.polimi.ingsw.view.UI;

import java.util.ArrayList;

public class ChooseCards extends ToClientMessage {

    protected ArrayList<CardView> possibleCards;
    protected int num;
    protected ArrayList<CardView> pickedCards;

    public ChooseCards(ArrayList<CardView> possibleCards, int num, ArrayList<CardView> pickedCards) {
        super("choose cards");
        this.possibleCards = possibleCards;
        this.num = num;
        this.pickedCards = pickedCards;
    }

    @Override
    public void performAction(UI client) {
        client.chooseCards(possibleCards, num, pickedCards);
    }

}
