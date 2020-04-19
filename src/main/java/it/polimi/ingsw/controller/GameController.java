package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.turn_controllers.*;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.view.PlayerInterface;

import java.util.ArrayList;

public class GameController {

    private Game game;
    private ArrayList<PlayerController> playerControllers;
    private ArrayList<Player> players;
    private ArrayList<String> colors;

    public GameController(PlayerInterface client, int num) {
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

    // Perchè devo crearlo?
    public GameController() {
    }
    ///////////////////////

    public Game getGame() {
        return game;
    }

    public void addPlayer(PlayerInterface client) {
        if (playerControllers.size() >= game.getPlayerNum()) {
            System.out.println("ERROR: too many players");
            return;
        }
        Player player = new Player(client.getId(), colors.get(playerControllers.size()));
        PlayerController playerController = new PlayerController(player, client);
        game.addPlayer(player);
        playerControllers.add(playerController);
        if (playerControllers.size() == game.getPlayerNum()) gameSetUp();
    }

    public void gameSetUp() {
        ArrayList<GodController> controllers = new ArrayList<GodController>();
        controllers.add(new ApolloController(this));
        controllers.add(new ArtemisController(this));
        controllers.add(new AthenaController(this));
        controllers.add(new AtlasController(this));
        controllers.add(new DemeterController(this));
        controllers.add(new HephaestusController(this));
        controllers.add(new HestiaController(this));
        controllers.add(new LimusController(this));
        controllers.add(new MinotaurController(this));
        controllers.add(new PanController(this));
        controllers.add(new PrometheusController(this));
        controllers.add(new ZeusController(this));

        Deck deck = game.getDeck();
        for (GodController godController : controllers) {
            deck.addCard(godController.generateCard());
        }

        players = game.getPlayers();
        deck.pickRandom(game.getPlayerNum());
        for (int i = 0; i < game.getPlayerNum(); i++) {
            players.get(i).setGodCard(deck.getPickedCards().get(i));
            playerControllers.get(i).setGodController(deck.getPickedCards().get(i).getController());
            playerControllers.get(i).getClient().displayMessage(players.get(i).getId() + " is " + deck.getPickedCards().get(i).getGod() + "\n");
        }

        displayBoard();
        placeWorkers();

        displayBoard();
        playGame();
    }

    private void placeWorkers() {
        for (int p = 0; p < game.getPlayerNum(); p++) {
            PlayerController controller = playerControllers.get(p);
            for (int i = 0; i < 2; ) {
                int j = i + 1;
                int posY = controller.getClient().chooseInt(5, players.get(p).getId() + ": Choose worker " + j + "'s starting position (X, then Y):");
                int posX = controller.getClient().chooseInt(5, null);
                if (game.getBoard().getCell(posX, posY).hasWorker()) {
                    controller.getClient().displayMessage("Cell is full. \n");
                }
                else {
                    Worker worker = new Worker(players.get(p));
                    worker.setPosition(game.getBoard().getCell(posX, posY));
                    players.get(p).addWorker(worker);
                    displayBoard();
                    i++;
                }
            }
        }
    }

    private void playGame() {
        while(!game.hasWinner()){
            displayMessage("=== " + players.get(game.getActivePlayer()).getId() + "'s TURN === \n");
            String result = playerControllers.get(game.getActivePlayer()).playTurn();
            if (result.equals("NEXT"))
                game.getNextPlayer();
            else if (result.equals("LOST"))
                game.setWinner(players.get(game.getNextPlayer()));
            else if(result.equals("WON"))
                game.setWinner(players.get(game.getActivePlayer()));
            else System.out.println("ERROR: invalid turn");
        }
        displayMessage(game.getWinner().getId() + " has won! \n\n");
    }

    public void displayBoard() {
        for (PlayerController p : playerControllers)
            p.getClient().displayBoard(game.getBoard());
    }

    public void displayMessage(String message) {
        for (PlayerController p : playerControllers)
            p.getClient().displayMessage(message);
    }
  
}
