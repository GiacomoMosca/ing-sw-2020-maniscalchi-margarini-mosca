package it.polimi.ingsw;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.view.client.CLI;
import it.polimi.ingsw.view.PlayerInterface;

public class TestApp {

    public static void main(String[] args) {
        CLI cli = new CLI();
        PlayerInterface p1 = new PlayerInterface(cli);
        p1.setId("P1");
        PlayerInterface p2 = new PlayerInterface(cli);
        p2.setId("P2");
        //PlayerInterface p3 = new PlayerInterface(cli);
        //p3.setId("c");
        p1.displayMessage("\n\nSANTORINI - TEST \n\n");
        GameController gameController = new GameController(p1, 2);
        gameController.addPlayer(p2);
        //gameController.addPlayer(p3);
    }

}
