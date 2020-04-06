package it.polimi.ingsw;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.PlayerController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.view.CLI;
import it.polimi.ingsw.view.PlayerInterface;

public class TestApp {

    public static void main(String[] args) {
        Player p1 = new Player("a","yellow");
        Player p2 = new Player("b","blue");
        CLI cli = new CLI();
        PlayerInterface client = new PlayerInterface(cli);
        client.displayMessage("SANTORINI - TEST \n\n\n\n");
        PlayerController p1c = new PlayerController(p1, client);
        PlayerController p2c = new PlayerController(p2, client);
        Game game = new Game(p1, 2);
        game.addPlayer(p2);
        GameController gameController = new GameController(game);
        gameController.gameSetUp();
    }

}
