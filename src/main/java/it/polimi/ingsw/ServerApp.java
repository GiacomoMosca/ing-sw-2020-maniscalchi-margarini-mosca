package it.polimi.ingsw;

import it.polimi.ingsw.network.server.Server;

/**
 * Starts a new instance of the Server class.
 */
public class ServerApp {

    public static void main(String[] args) {
        Server server = new Server(8000);
        server.start();
    }

}
