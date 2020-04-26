package it.polimi.ingsw.network.message.to_client;

import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.view.UI;

public abstract class ToClientMessage extends Message {

    public ToClientMessage(Object body) {
        super(body);
    }

    public abstract void performAction(UI client);

}
