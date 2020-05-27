package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.turn_controllers.*;
import it.polimi.ingsw.exceptions.GameEndedException;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.view.CellView;
import it.polimi.ingsw.view.VirtualView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameController {

    protected final AtomicBoolean running;
    protected final AtomicBoolean setup;
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
    public GameController(VirtualView client, int num, String gameName) {
        running = new AtomicBoolean(true);
        setup = new AtomicBoolean(true);
        playerControllers = new ArrayList<PlayerController>();
        colors = new ArrayList<String>();
        colors.add("r");
        colors.add("g");
        colors.add("b");
        Player p1 = new Player(client.getId(), colors.get(0));
        PlayerController p1Controller = new PlayerController(p1, client, this);
        game = new Game(gameName, p1, num);
        playerControllers.add(p1Controller);
        client.setPlayerController(p1Controller);
    }

    public boolean isRunning() {
        return running.get();
    }

    public boolean isSetup() {
        return setup.get();
    }

    /**
     * @return the current Game
     */
    public Game getGame() {
        return game;
    }

    public ArrayList<PlayerController> getControllers() {
        return new ArrayList<PlayerController>(playerControllers);
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
    public void addPlayer(VirtualView client) throws GameEndedException {
        if (!running.get() || !setup.get()) throw new GameEndedException("game ended");
        if (playerControllers.size() >= game.getPlayerNum()) {
            System.out.println("ERROR: too many players");
            return;
        }
        Player player = new Player(client.getId(), colors.get(playerControllers.size()));
        PlayerController playerController = new PlayerController(player, client, this);
        game.addPlayer(player);
        playerControllers.add(playerController);
        client.setPlayerController(playerController);
        try {
            broadcastMessage(client.getId() + " joined the game (" + game.getPlayers().size() + "/" + game.getPlayerNum() + ")");
        } catch (IOExceptionFromController e) {
            handleDisconnection(e.getController());
        }
    }

    /**
     * creates a GodController for every God Card, and adds all the cards to the deck.
     * randomly associates a GodCard to every player, also associating the correct GodController to every PlayerController.
     */
    public void gameSetUp() {
        if (!setup.compareAndSet(true, false)) return;
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

        try {
            broadcastMessage("Game started!");
            broadcastGameInfo("gameSetup1");
            pickCards();
            broadcastGameInfo("gameSetup2");
            chooseStartPlayer();
            placeWorkers();

            broadcastGameInfo("gameStart");
            playGame();
        } catch (IOExceptionFromController e) {
            handleDisconnection(e.getController());
        }
    }

    private void pickCards() throws IOExceptionFromController {
        Deck deck = game.getDeck();
        try {
            if (playerControllers.get(0).getClient().chooseYesNo("Do you want to randomize the playable God Powers pool?")) {
                deck.pickRandom(game.getPlayerNum());
            } else {
                ArrayList<Card> choices = playerControllers.get(0).getClient().chooseCards(deck.getCards(), game.getPlayerNum(), null);
                for (Card card : choices) {
                    deck.pickCard(card);
                }
            }
        } catch (IOException e) {
            throw new IOExceptionFromController(e, playerControllers.get(0));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        ArrayList<Card> cardPool = deck.getPickedCards();
        ArrayList<Card> chosenCards = new ArrayList<Card>();
        for (int i = 0; i < game.getPlayerNum(); i++) {
            int j = (i == game.getPlayerNum() - 1) ? 0 : i + 1;
            Card chosenCard;
            try {
                chosenCard = playerControllers.get(j).getClient().chooseCards(cardPool, 1, chosenCards).get(0);
            } catch (IOException e) {
                throw new IOExceptionFromController(e, playerControllers.get(j));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }
            cardPool.remove(chosenCard);
            chosenCards.add(chosenCard);
            players.get(j).setGodCard(chosenCard);
            playerControllers.get(j).setGodController(chosenCard.getController());
            broadcastMessage((players.get(j).getId() + " is " + chosenCard.getGod() + " (" + players.get(j).getColor() + ")\n"));
        }
    }

    private void chooseStartPlayer() throws IOExceptionFromController {
        try {
            game.setActivePlayer(playerControllers.get(0).getClient().chooseStartingPlayer(players));
        } catch (IOException e) {
            throw new IOExceptionFromController(e, playerControllers.get(0));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * place the workers of all the players, asking them the localizations and then moving the workers there.
     */
    private void placeWorkers() throws IOExceptionFromController {
        ArrayList<Cell> freePositions = game.getBoard().getAllCells();
        for (int i = 0; i < game.getPlayerNum(); i++) {
            int p = game.getActivePlayer() + i;
            if (p >= game.getPlayerNum()) p = p - game.getPlayerNum();
            PlayerController controller = playerControllers.get(p);
            for (int j = 0; j < 2; j++) {
                Cell position;
                int w = j + 1;
                try {
                    position = controller.getClient().chooseStartPosition(freePositions, w);
                } catch (IOException e) {
                    throw new IOExceptionFromController(e, controller);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
                freePositions.remove(position);
                Worker worker = new Worker(players.get(p), w);
                worker.setPosition(game.getBoard().getCell(position.getPosX(), position.getPosY()));
                players.get(p).addWorker(worker);
                broadcastPlaceWorker(worker.getPosition());
            }
        }
    }


    /**
     * plays out the game and handles wins/losses
     */
    private void playGame() throws IOExceptionFromController {
        for (Player player : players) {
            if (player.getGodCard().hasAlwaysActiveModifier()) game.addModifier(player.getGodCard());
        }
        while (!game.hasWinner()) {
            if (!running.get()) return;
            Player currentPlayer = players.get(game.getActivePlayer());
            for (Card modifier : game.getActiveModifiers()) {
                if (!modifier.hasAlwaysActiveModifier() && modifier.getController().getPlayer().equals(currentPlayer))
                    game.removeModifier(modifier);
            }

            broadcastGameInfo("turnStart");
            String result = playerControllers.get(game.getActivePlayer()).playTurn();
            switch (result) {
                case "next":
                    checkWorkers();
                    game.nextPlayer();
                    break;
                case "outOfMoves":
                case "outOfBuilds":
                    eliminatePlayer(currentPlayer, result);
                    game.nextPlayer();
                    break;
                case "winConditionAchieved":
                case "godConditionAchieved":
                    setWinner(currentPlayer, result);
                    break;
                default:
                    System.out.println("ERROR: invalid turn");
                    break;
            }
        }
        if (!running.compareAndSet(true, false)) return;
        gameOver();
    }

    /**
     * checks if the game has reached the maximum number of players
     */
    public boolean checkPlayersNumber() {
        return game.getPlayers().size() >= game.getPlayerNum();
    }

    /**
     * checks if any player has no workers left and, if so, removes them from the game
     */
    public void checkWorkers() throws IOExceptionFromController {
        for (Player player : players) {
            if (player.getWorkers().size() == 0) eliminatePlayer(player, "outOfWorkers");
        }
    }

    public void checkDisconnection(IOException e, PlayerController controller) throws IOExceptionFromController {
        if (controller == null) return;
        if (controller.getPlayer().hasLost()) {
            playerControllers.set(playerControllers.indexOf(controller), null);
        } else
            throw new IOExceptionFromController(e, controller);
    }

    public void handleDisconnection(PlayerController controller) {
        if (!running.compareAndSet(true, false)) return;
        playerControllers.remove(controller);
        notifyDisconnection(controller.getPlayer());
        gameOver();
    }

    /**
     * removes a player from the game, then sets the winner if only one player is left
     *
     * @param player the losing player
     * @param reason the reason why the player lost
     */
    private void eliminatePlayer(Player player, String reason) throws IOExceptionFromController {
        player.setLost();
        ArrayList<Player> activePlayers = new ArrayList<Player>();
        for (Player activePlayer : players) {
            if (!activePlayer.hasLost()) activePlayers.add(activePlayer);
        }
        if (activePlayers.size() == 1) {
            setWinner(activePlayers.get(0), reason);
            return;
        }
        for (Card modifier : game.getActiveModifiers()) {
            if (modifier.getController().getPlayer().equals(player))
                game.removeModifier(modifier);
        }
        for (Worker worker : player.getWorkers()) {
            player.removeWorker(worker);
        }
        PlayerController controller = playerControllers.get(players.indexOf(player));
        if (controller != null) {
            try {
                playerControllers.get(players.indexOf(player)).getClient().notifyLoss(reason, null);
            } catch (IOException e) {
                checkDisconnection(e, controller);
            }
        }
        broadcastGameInfo(reason);
    }

    public void broadcastBuild(CellView buildPosition, Card godPower) throws IOExceptionFromController {
        for (PlayerController controller : playerControllers) {
            if (controller == null) continue;
            try {
                controller.getClient().displayBuild(buildPosition, godPower);
            } catch (IOException e) {
                checkDisconnection(e, controller);
            }
        }
    }

    /**
     * broadcasts the Board associated with the current Game to all players and spectators
     */
    public void broadcastGameInfo(String desc) throws IOExceptionFromController {
        for (PlayerController controller : playerControllers) {
            if (controller == null) continue;
            try {
                controller.getClient().displayGameInfo(game, desc);
            } catch (IOException e) {
                checkDisconnection(e, controller);
            }
        }
    }

    /**
     * broadcasts the message received as an argument to all players and spectators
     *
     * @param message the message to show
     */
    public void broadcastMessage(String message) throws IOExceptionFromController {
        for (PlayerController controller : playerControllers) {
            if (controller == null) continue;
            try {
                controller.getClient().displayMessage(message);
            } catch (IOException e) {
                checkDisconnection(e, controller);
            }
        }
    }

    public void broadcastMove(HashMap<CellView, CellView> moves, Card godPower) throws IOExceptionFromController {
        for (PlayerController controller : playerControllers) {
            if (controller == null) continue;
            try {
                controller.getClient().displayMove(moves, godPower);
            } catch (IOException e) {
                checkDisconnection(e, controller);
            }
        }
    }

    public void broadcastPlaceWorker(Cell workerPosition) throws IOExceptionFromController {
        for (PlayerController controller : playerControllers) {
            if (controller == null) continue;
            try {
                controller.getClient().displayPlaceWorker(workerPosition);
            } catch (IOException e) {
                checkDisconnection(e, controller);
            }
        }
    }

    public void notifyDisconnection(Player player) {
        for (PlayerController controller : playerControllers) {
            if (controller == null) continue;
            try {
                controller.getClient().notifyDisconnection(player);
            } catch (IOException e) {
                // no need to handle disconnection, game is over
            }
        }
    }

    /**
     * sets a player as the winner
     *
     * @param player the losing player
     * @param reason the reason why the player lost
     */
    private void setWinner(Player player, String reason) {
        game.setWinner(player);
        for (PlayerController controller : playerControllers) {
            if (controller == null) continue;
            try {
                if (controller.getPlayer().equals(player)) {
                    controller.getClient().notifyWin(reason);
                } else {
                    controller.getClient().notifyLoss(reason, player);
                }
            } catch (IOException e) {
                // no need to handle disconnection, game is over
            }
        }
    }

    /**
     * notifies all players and spectators that the game is over
     */
    public void gameOver() {
        if (running.get()) return;
        for (PlayerController controller : playerControllers) {
            if (controller == null) continue;
            controller.getClient().setPlayerController(null);
            try {
                controller.getClient().notifyGameOver();
            } catch (IOException e) {
                // no need to handle disconnection, game is over
            }
        }
    }

}
