package it.polimi.ingsw.network.client;

import it.polimi.ingsw.view.UI;
import it.polimi.ingsw.view.cli.CLI;

public class ClientCLI {

    private static UI userInterface;

    public static void main(String[] args) {
        userInterface = new CLI();
        userInterface.run();
    }

}
