package it.polimi.ingsw;

import it.polimi.ingsw.network.server.Server;

public class TestApp {

    public static void main(String[] args) {
        Server server = new Server(8000);
        server.start();
    }

}
