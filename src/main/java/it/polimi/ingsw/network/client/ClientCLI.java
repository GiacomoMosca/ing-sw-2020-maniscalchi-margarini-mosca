package it.polimi.ingsw.network.client;

import it.polimi.ingsw.view.UI;
import it.polimi.ingsw.view.cli.CLI;

/**
 * Starts a Client using the Command Line Interface.
 */
public class ClientCLI {

    private static UI userInterface;

    /**
     * Creates a new CLI.
     *
     * @param args no arguments
     */
    public static void main(String[] args) {
        userInterface = new CLI();
        userInterface.run();
    }

}
