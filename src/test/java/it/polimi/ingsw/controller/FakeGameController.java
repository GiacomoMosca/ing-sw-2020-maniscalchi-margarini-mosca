package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.turn_controllers.*;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.view.PlayerInterface;

import java.util.ArrayList;

public class FakeGameController extends GameController {

    private Game game;
    private ArrayList<PlayerController> playerControllers;
    private ArrayList<Player> players;
    private ArrayList<String> colors;
    private GodController controller;

    public FakeGameController(PlayerInterface client, int num,GodController controller) {
        this.controller=controller;
        playerControllers = new ArrayList<PlayerController>();
        colors = new ArrayList<String>();
        colors.add("RED");
        colors.add("BLUE");
        colors.add("GREEN");
        Player p1 = new Player(client.getId(), colors.get(0));
        PlayerController p1Controller = new PlayerController(p1, client);
        game = new Game(p1, num);
        playerControllers.add(p1Controller);
    }

    public Game getGame() {
        return game;
    }

    public void addPlayer(PlayerInterface client) {
        Player player = new Player(client.getId(), colors.get(playerControllers.size()));
        PlayerController playerController = new PlayerController(player, client);
        game.addPlayer(player);
        playerControllers.add(playerController);
        gameSetUp();
    }

    public void gameSetUp() {

        Deck deck = game.getDeck();
        deck.addCard(controller.generateCard());

        players = game.getPlayers();
        players.get(0).setGodCard(deck.getCards().get(0));
        playerControllers.get(0).setGodController(controller);

        placeWorkers();

        placeBuildings();

        playGame();
    }

    private void placeWorkers() {
        Worker worker = new Worker(players.get(0));
        worker.setPosition(game.getBoard().getCell(1, 2));
        players.get(0).addWorker(worker);
    }

    private void placeBuildings(){
        game.getBoard().getCell(0,0).setBuildLevel(1);
    }

    private void playGame() {
        String result = playerControllers.get(game.getActivePlayer()).playTurn();
        if(result.equals("WON"))
            game.setWinner(players.get(game.getActivePlayer()));
    }
}
