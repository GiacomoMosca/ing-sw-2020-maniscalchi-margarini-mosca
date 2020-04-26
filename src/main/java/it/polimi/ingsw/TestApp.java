package it.polimi.ingsw;

import it.polimi.ingsw.network.server.Server;

public class TestApp {

    public static void main(String[] args) {
        Server server = new Server(8000);
        server.start();
        /*
        CLI cli = new CLI();
        VirtualView p1 = new VirtualView(cli);
        p1.setId("a");
        VirtualView p2 = new VirtualView(cli);
        p2.setId("b");
        //VirtualView p3 = new VirtualView(cli);
        //p3.setId("c");
        p1.displayMessage("\n\nSANTORINI - TEST \n\n");
        GameController gameController = new GameController(p1, 2);
        gameController.addPlayer(p2);
        //gameController.addPlayer(p3);
        */
    }

}
