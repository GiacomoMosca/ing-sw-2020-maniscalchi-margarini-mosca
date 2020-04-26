package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.turn_controllers.*;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.view.VirtualView;

import java.io.IOException;
import java.util.ArrayList;

public class GameController {

    protected Game game;
    protected ArrayList<PlayerController> playerControllers;
    protected ArrayList<Player> players;
    protected ArrayList<String> colors;

    /**
     * creates a GameController.
     * creates the first player (associated with the VirtualView received as an argument),
     * associating his id and the color "RED".
     * creates a PlayerController for the first player, associating the Player and his VirtualView.
     * adds the PlayerController.
     *
     *
     * @param client the VirtualView associated with the first player
     * @param num the number of players for the current game
     */
    public GameController(VirtualView client, int num) {
        playerControllers = new ArrayList<PlayerController>();
        colors = new ArrayList<String>();
        colors.add("r");
        colors.add("g");
        colors.add("b");
        Player p1 = new Player(client.getId(), colors.get(0));
        PlayerController p1Controller = new PlayerController(p1, client);
        game = new Game(p1, num);
        playerControllers.add(p1Controller);
    }

    /**
     *
     * @return the current Game
     */
    public Game getGame() {
        return game;
    }

    /**
     * adds a second or a third player to the game.
     * creates the new player, associating his id (given by the VirtualView) and a color.
     * creates a PlayerController for the player and associates the player and his VirtualView.
     *
     * and the game controller asso
     *
     * @param client
     */
    public void addPlayer(VirtualView client) {
        if (playerControllers.size() >= game.getPlayerNum()) {
            System.out.println("ERROR: too many players");
            return;
        }
        Player player = new Player(client.getId(), colors.get(playerControllers.size()));
        PlayerController playerController = new PlayerController(player, client);
        game.addPlayer(player);
        playerControllers.add(playerController);
    }

    /**
     *
     * ///// PROBABLY DA CAMBIARE
     *
     * creates a GodController for every God Card, and adds all the cards to the deck.
     * randomly associates a GodCard to every player, also associating the correct GodController to every PlayerController.
     *
     */
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

        pickCards();

        displayBoard();
        placeWorkers();

        displayBoard();
        playGame();
    }

    private void pickCards() {
        Deck deck = game.getDeck();
        deck.pickRandom(game.getPlayerNum());
        for (int i = 0; i < game.getPlayerNum(); i++) {
            players.get(i).setGodCard(deck.getPickedCards().get(i));
            playerControllers.get(i).setGodController(deck.getPickedCards().get(i).getController());
            broadcastMessage((players.get(i).getId() + " is " + deck.getPickedCards().get(i).getGod() + " (" + players.get(i).getColor() + ")\n"));
        }
    }

    /**
     * place the workers of all the players, asking them the localizations and then moving the workers there.
     *
     */
    private void placeWorkers() {
        for (int p = 0; p < game.getPlayerNum(); p++) {
            PlayerController controller = playerControllers.get(p);
            ArrayList<Cell> freePositions = game.getBoard().getAllCells();
            for (int i = 0; i < 2; i++) {
                Cell position = null;
                int j = i + 1;
                try {
                    controller.getClient().displayMessage("(Worker " + j + ") ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    position = controller.getClient().chooseStartPosition(freePositions);
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
                freePositions.remove(position);
                Worker worker = new Worker(players.get(p));
                worker.setPosition(game.getBoard().getCell(position.getPosX(), position.getPosY()));
                players.get(p).addWorker(worker);
                displayBoard();
            }
        }
    }


    /**
     * handles the game, going on until there is no winner
     *
     */
    private void playGame() {
        while(!game.hasWinner()){
            broadcastMessage("=== " + players.get(game.getActivePlayer()).getId() + "'s TURN === \n");
            String result = playerControllers.get(game.getActivePlayer()).playTurn();
            if (result.equals("NEXT"))
                game.getNextPlayer();
            else if (result.equals("LOST"))
                game.setWinner(players.get(game.getNextPlayer()));
            else if(result.equals("WON"))
                game.setWinner(players.get(game.getActivePlayer()));
            else System.out.println("ERROR: invalid turn");
        }
        broadcastMessage(game.getWinner().getId() + " has won! \n\n");
    }

    /**
     * shows the Board associated with the current Game
     *
     */
    public void displayBoard() {
        for (PlayerController p : playerControllers) {
            try {
                p.getClient().displayBoard(game.getPlayers(), game.getBoard());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * shows the message received as an argument
     *
     * @param message the message to show
     */
    public void broadcastMessage(String message) {
        for (PlayerController p : playerControllers) {
            try {
                p.getClient().displayMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkPlayers() {
        return game.getPlayers().size() == game.getPlayerNum();
    }
  
}
