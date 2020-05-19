package it.polimi.ingsw;

import it.polimi.ingsw.network.server.ServerTestGUI;

public class TestGUI {

    public static void main(String[] args) {
        ServerTestGUI server = new ServerTestGUI(8000);
        server.start();
    }

}
