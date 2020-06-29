package it.polimi.ingsw;

import it.polimi.ingsw.network.server.Server;

/**
 * Allows starting a new Game, creating an instance of the Server class.
 */
public class ServerApp {

    public static void main(String[] args) {
        Server server = new Server(8000);
        server.start();
    }

}
