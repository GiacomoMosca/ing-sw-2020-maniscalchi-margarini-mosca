package it.polimi.ingsw.network.client;

import it.polimi.ingsw.view.UI;
import it.polimi.ingsw.view.gui.GUI;

/**
 * The ClientGUI class represents a client interacting using the Graphical User Interface.
 */
public class ClientGUI {

    private static UI userInterface;

    /**
     * Creates a new GUI.
     *
     * @param args
     */
    public static void main(String[] args) {
        userInterface = new GUI();
        userInterface.run();
    }

}
