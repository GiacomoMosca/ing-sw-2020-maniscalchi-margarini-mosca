package it.polimi.ingsw.network.client;

import it.polimi.ingsw.view.UI;
import it.polimi.ingsw.view.gui.GUI;

/**
 * Starts a Client using the Graphical User Interface.
 */
public class ClientGUI {

    private static UI userInterface;

    /**
     * Creates a new GUI.
     *
     * @param args no arguments
     */
    public static void main(String[] args) {
        userInterface = new GUI();
        userInterface.run();
    }

}
