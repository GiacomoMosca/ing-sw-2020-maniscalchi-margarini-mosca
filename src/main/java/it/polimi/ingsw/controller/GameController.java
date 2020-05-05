package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.turn_controllers.*;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Card;
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
     * @param client the VirtualView associated with the first player
     * @param num    the number of players for the current game
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
     * @return the current Game
     */
    public Game getGame() {
        return game;
    }

    /**
     * adds a new player to the game.
     * creates the new player, associating his id (given by the VirtualView) and a color.
     * creates a PlayerController for the player and associates the player and his VirtualView.
     * <p>
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
     * creates a GodController for every God Card, and adds all the cards to the deck.
     * randomly associates a GodCard to every player, also associating the correct GodController to every PlayerController.
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
        controllers.add(new MedusaController(this));
        controllers.add(new MinotaurController(this));
        controllers.add(new PanController(this));
        controllers.add(new PrometheusController(this));
        controllers.add(new TritonController(this));
        controllers.add(new ZeusController(this));

        Deck deck = game.getDeck();

        for (GodController godController : controllers) {
            deck.addCard(godController.generateCard());
        }

        players = game.getPlayers();

        broadcastMessage("Game started!");
        pickCards();
        chooseStartPlayer();

        broadcastBoard();
        placeWorkers();

        broadcastBoard();
        playGame();
    }

    private void pickCards() {
        Deck deck = game.getDeck();
        boolean randomize = false;
        try {
            randomize = playerControllers.get(0).getClient().chooseYesNo("Do you want to randomize the playable God Cards pool?");
            if (randomize) {
                deck.pickRandom(game.getPlayerNum());
            } else {
                ArrayList<Card> choices = null;
                choices = playerControllers.get(0).getClient().chooseCards(deck.getCards(), game.getPlayerNum(), null);
                for (Card card : choices) {
                    deck.pickCard(card);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        ArrayList<Card> cardPool = deck.getPickedCards();
        ArrayList<Card> chosenCards = new ArrayList<Card>();
        for (int i = 0; i < game.getPlayerNum(); i++) {
            int j = (i == game.getPlayerNum() - 1) ? 0 : i + 1;
            Card chosenCard = null;
            try {
                chosenCard = playerControllers.get(j).getClient().chooseCards(cardPool, 1, chosenCards).get(0);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            cardPool.remove(chosenCard);
            chosenCards.add(chosenCard);
            players.get(j).setGodCard(chosenCard);
            playerControllers.get(j).setGodController(chosenCard.getController());
            broadcastMessage((players.get(j).getId() + " is " + chosenCard.getGod() + " (" + players.get(j).getColor() + ")\n"));
        }
    }

    private void chooseStartPlayer() {
        try {
            game.setActivePlayer(playerControllers.get(0).getClient().chooseStartingPlayer(players));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * place the workers of all the players, asking them the localizations and then moving the workers there.
     */
    private void placeWorkers() {
        ArrayList<Cell> freePositions = game.getBoard().getAllCells();
        for (int i = 0; i < game.getPlayerNum(); i++) {
            int p = game.getActivePlayer() + i;
            if (p >= game.getPlayerNum()) p = p - game.getPlayerNum();
            PlayerController controller = playerControllers.get(p);
            for (int j = 0; j < 2; j++) {
                Cell position = null;
                int w = j + 1;
                try {
                    controller.getClient().displayMessage("(Worker " + w + ") ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    position = controller.getClient().chooseStartPosition(freePositions);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    continue;
                }
                freePositions.remove(position);
                Worker worker = new Worker(players.get(p));
                worker.setPosition(game.getBoard().getCell(position.getPosX(), position.getPosY()));
                players.get(p).addWorker(worker);
                broadcastBoard();
            }
        }
    }


    /**
     * plays out the game and handles wins/losses
     */
    private void playGame() {
        for (Player player : players) {
            if (player.getGodCard().hasAlwaysActiveModifier()) game.addModifier(player.getGodCard());
        }
        while (!game.hasWinner()) {
            Player currentPlayer = players.get(game.getActivePlayer());
            for (Card modifier : game.getActiveModifiers()) {
                if (!modifier.hasAlwaysActiveModifier() && modifier.getController().getPlayer().equals(currentPlayer))
                    game.removeModifier(modifier);
            }

            broadcastMessage("=== " + currentPlayer.getId() + "'s turn === \n");
            switch (playerControllers.get(game.getActivePlayer()).playTurn()) {
                case "NEXT":
                    checkWorkers();
                    game.nextPlayer();
                    break;
                case "LOST":
                    eliminatePlayer(currentPlayer, "outOfMoves");
                    game.nextPlayer();
                    break;
                case "WON":
                    setWinner(currentPlayer, "winConditionAchieved");
                    break;
                default:
                    System.out.println("ERROR: invalid turn");
                    break;
            }
        }
        gameOver();
    }

    /**
     * checks if the game has reached the maximum number of players
     */
    public boolean checkPlayersNumber() {
        return game.getPlayers().size() == game.getPlayerNum();
    }

    /**
     * checks if any player has no workers left and, if so, removes them from the game
     */
    public void checkWorkers() {
        for (Player player : players) {
            if (player.getWorkers().size() == 0) eliminatePlayer(player, "outOfWorkers");
        }
    }

    /**
     * removes a player from the game, then sets the winner if only one player is left
     *
     * @param player the losing player
     * @param reason the reason why the player lost
     */
    private void eliminatePlayer(Player player, String reason) {
        player.setLost();
        notifyLoss(player, reason);
        ArrayList<Player> activePlayers = new ArrayList<Player>();
        for (Player activePlayer : players) {
            if (!activePlayer.hasLost()) activePlayers.add(activePlayer);
        }
        if (activePlayers.size() == 1) {
            setWinner(activePlayers.get(0), "lastPlayerStanding");
            return;
        }
        game.getActiveModifiers().removeIf(
                modifier -> modifier.getController().getPlayer().equals(player)
        );
    }

    /**
     * sets a player as the winner
     *
     * @param player the losing player
     * @param reason the reason why the player lost
     */
    private void setWinner(Player player, String reason) {
        game.setWinner(player);
        notifyWin(player, reason);
    }

    /**
     * shows the Board associated with the current Game
     */
    public void broadcastBoard() {
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

    /**
     * notifies all players that a player has lost
     *
     * @param player the losing player
     * @param reason the reason why the player lost
     */
    public void notifyLoss(Player player, String reason) {
        for (PlayerController p : playerControllers) {
            try {
                p.getClient().notifyLoss(player, reason);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * notifies all players that a player has won
     *
     * @param player the winning player
     * @param reason the reason why the player won
     */
    public void notifyWin(Player player, String reason) {
        for (PlayerController p : playerControllers) {
            try {
                p.getClient().notifyWin(player, reason);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * notifies all players that the game is over
     */
    public void gameOver() {
        for (PlayerController p : playerControllers) {
            try {
                p.getClient().gameOver();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
