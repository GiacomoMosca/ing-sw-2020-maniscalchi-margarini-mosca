package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.view.CardView;
import it.polimi.ingsw.view.UI;

import java.util.ArrayList;

/**
 * The ChooseCards message is used to send a message from the server to a client, to ask him to choose among some cards.
 */
public class ChooseCards extends ToClientMessage {

    protected ArrayList<CardView> possibleCards;
    protected int num;
    protected ArrayList<CardView> pickedCards;

    /**
     * ChooseCards constructor.
     * Calls the super constructor so that the msgId is set to "choose cards".
     * Sets all the attributes as the values received as arguments.
     *
     * @param possibleCards an ArrayList containing all the Cards to choose among
     * @param num           the number of Cards to pick, can be
     *                      • 1 if the player is asked to choose his own card for the Game among the pickedCards
     *                      • 2 if the player is asked to choose the 2 cards to use for a 2-players Game
     *                      • 3 if the player is asked to choose the 3 cards to use for a 3-players Game
     * @param pickedCards   an ArrayList containing the picked Cards, needed when num has the value 1
     */
    public ChooseCards(ArrayList<CardView> possibleCards, int num, ArrayList<CardView> pickedCards) {
        super("choose cards");
        this.possibleCards = possibleCards;
        this.num = num;
        this.pickedCards = pickedCards;
    }

    /**
     * When called, this method invokes the right method on the client who received this message to perform the action requested by the message.
     * In this case, the client will be asked to choose the Cards to play with or to choose his own Card.
     *
     * @param client the User Interface representing the client this message is sent to
     */
    @Override
    public void performAction(UI client) {
        client.chooseCards(possibleCards, num, pickedCards);
    }

}
