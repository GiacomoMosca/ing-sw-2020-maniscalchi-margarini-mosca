package it.polimi.ingsw.network.client;

import it.polimi.ingsw.view.UI;
import it.polimi.ingsw.view.gui.GUI;

public class ClientGUI {

    private static UI userInterface;

    public static void main(String[] args) {
        userInterface = new GUI();
        userInterface.run();
    }

}
