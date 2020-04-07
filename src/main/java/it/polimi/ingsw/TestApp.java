package it.polimi.ingsw;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.PlayerController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.view.CLI;
import it.polimi.ingsw.view.PlayerInterface;

public class TestApp {

    public static void main(String[] args) {
        CLI cli = new CLI();
        PlayerInterface p1 = new PlayerInterface(cli);
        p1.setId("a");
        PlayerInterface p2 = new PlayerInterface(cli);
        p2.setId("b");
        p1.displayMessage("\n\nSANTORINI - TEST \n\n");
        GameController gameController = new GameController(p1);
        gameController.addPlayer(p2);
    }

}
