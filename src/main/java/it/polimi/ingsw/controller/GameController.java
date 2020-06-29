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
import it.polimi.ingsw.network.server.Logger;
import it.polimi.ingsw.view.CellView;
import it.polimi.ingsw.view.VirtualView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameController {

    protected final AtomicBoolean running;
    protected final AtomicBoolean setup;
    protected Logger logger;
    protected Game game;
    protected ArrayList<PlayerController> playerControllers;
    protected ArrayList<Player> players;
    protected ArrayList<String> colors;

    /**
     * GameController constructor.
     * Creates the first Player (associated with the VirtualView received as an argument), associating him his ID and the color "r" (red).
     * Creates a PlayerController for the first Player, associating the Player and his VirtualView.
     * Creates the Game.
     *
     * @param client   the VirtualView associated with the first player
     * @param num      the number of players for the current game
     * @param gameName the name of the Game
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

    /**
     * @return "true" if the attribute running is true, false otherwise
     */
    public boolean isRunning() {
        return running.get();
    }

    /**
     * @return "true" if the attribute setup is true, false otherwise
     */
    public boolean isSetup() {
        return setup.get();
    }

    /**
     * Sets the Logger associated with the server.
     *
     * @param logger the Logger to set
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * Adds the Game's name at the beginning of the given message and logs it to the Logger.
     *
     * @param message the message to log
     */
    public void log(String message) {
        logger.log("[" + game.getName() + "] " + message);
    }

    /**
     * Adds the Game's name at the beginning of the given message and logs it to the Logger as an error message.
     *
     * @param message the error message to log
     */
    public void logError(String message) {
        logger.logError("[" + game.getName() + "] " + message);
    }

    /**
     * @return the current Game
     */
    public Game getGame() {
        return game;
    }

    /**
     * @return an ArrayList containing all the PlayerControllers for this Game
     */
    public ArrayList<PlayerController> getControllers() {
        return new ArrayList<PlayerController>(playerControllers);
    }

    /**
     * Adds a new Player to the Game.
     * Creates the new player, associating him his ID and a color ("g" as green for the second player and "b" as blue for the third player).
     * Creates a PlayerController for the new Player, associating the player and his VirtualView.
     *
     * @param client the VirtualView associated with the Player to add
     * @throws GameEndedException when the Game is unexpectedly ended
     */
    public void addPlayer(VirtualView client) throws GameEndedException {
        if (!running.get() || !setup.get()) throw new GameEndedException("game ended");
        if (playerControllers.size() >= game.getPlayerNum()) {
            logError("too many players");
            return;
        }
        Player player = new Player(client.getId(), colors.get(playerControllers.size()));
        PlayerController playerController = new PlayerController(player, client, this);
        game.addPlayer(player);
        playerControllers.add(playerController);
        client.setPlayerController(playerController);
        try {
            broadcastGameInfo("playerJoined");
            broadcastMessage(client.getId() + " joined! (" + game.getPlayers().size() + "/" + game.getPlayerNum() + ") ");
        } catch (IOExceptionFromController e) {
            handleDisconnection(e.getController());
        }
    }

    /**
     * Handles the setting up of the Game:
     * creating a GodController for each God Card,
     * adding all the God Cards to the Deck,
     * picking cards,
     * choosing the starting player,
     * placing workers.
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
            broadcastGameInfo("gameSetup");
            broadcastMessage("Game started!");
            broadcastMessage("Picking cards...");
            pickCards();
            broadcastGameInfo("boardSetup");
            chooseStartPlayer();
            placeWorkers();

            broadcastGameInfo("gameStart");
            playGame();
        } catch (IOExceptionFromController e) {
            handleDisconnection(e.getController());
        }
    }

    /**
     * Allows picking Cards from the Deck containing all the 14 God Power Cards.
     * If the Player who first signed up chooses to randomize the playable God Powers pool, the Cards are randomly picked from the Deck; otherwise, he chooses the Cards to use.
     * In both cases, the Cards are assigned to the Players by asking them which one they want to use (starting from the player who was the second to sign up).
     *
     * @throws IOExceptionFromController when an IOException from a specific PlayerController occurs
     */
    private void pickCards() throws IOExceptionFromController {
        Deck deck = game.getDeck();
        try {
            if (playerControllers.get(0).getClient().chooseYesNo("Do you want to randomize the playable God Powers pool?")) {
                deck.pickRandom(game.getPlayerNum());
                playerControllers.get(0).getClient().displayMessage("Picking cards...");
            } else {
                ArrayList<Card> choices = playerControllers.get(0).getClient().chooseCards(deck.getCards(), game.getPlayerNum(), null);
                for (Card card : choices) {
                    deck.pickCard(card);
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new IOExceptionFromController(e, playerControllers.get(0));
        }
        ArrayList<Card> cardPool = deck.getPickedCards();
        ArrayList<Card> chosenCards = new ArrayList<Card>();
        for (int i = 0; i < game.getPlayerNum(); i++) {
            int j = (i == game.getPlayerNum() - 1) ? 0 : i + 1;
            Card chosenCard;
            try {
                chosenCard = playerControllers.get(j).getClient().chooseCards(cardPool, 1, chosenCards).get(0);
            } catch (IOException | InterruptedException e) {
                throw new IOExceptionFromController(e, playerControllers.get(j));
            }
            cardPool.remove(chosenCard);
            chosenCards.add(chosenCard);
            players.get(j).setGodCard(chosenCard);
            playerControllers.get(j).setGodController(chosenCard.getController());
            broadcastMessage((players.get(j).getId() + " is " + chosenCard.getGod() + " (" + players.get(j).getColor() + ")\n"));
            broadcastMessage("Picking cards...");
        }
    }

    /**
     * Asks to the first Player who signed up who will be the starting player, and then sets it.
     *
     * @throws IOExceptionFromController when an IOException from a specific PlayerController occurs
     */
    private void chooseStartPlayer() throws IOExceptionFromController {
        try {
            game.setActivePlayer(playerControllers.get(0).getClient().chooseStartingPlayer(players));
        } catch (IOException | InterruptedException e) {
            throw new IOExceptionFromController(e, playerControllers.get(0));
        }
    }

    /**
     * Allows placing all the Workers on the Board.
     * Asks each Player the starting positions for both his Workers, starting from the Player who was chosen as the first one.
     *
     * @throws IOExceptionFromController when an IOException from a specific PlayerController occurs
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
                } catch (IOException | InterruptedException e) {
                    throw new IOExceptionFromController(e, controller);
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
     * Before proceeding to the Game, checks if there is any always active God Power.
     * Then plays out the Game until there's no winner.
     * Each Player's turn is in the end described by a string:
     * • "next" if the Player properly moved and built and the game goes on;
     * • "outOfMoves" if the Player must be eliminated because ran out of moves for both his Workers;
     * • "outOfBuilds" if the Player must be eliminated because ran out of builds for both his Workers;
     * • "winConditionAchieved" if the Player won because he achieved the win condition;
     * • "godConditionAchieved" if the Player won because he achieved his God win condition;
     *
     * @throws IOExceptionFromController when an IOException from a specific PlayerController occurs
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
                case "outOfWorkers":
                    eliminatePlayer(currentPlayer, result);
                    game.nextPlayer();
                    break;
                case "winConditionAchieved":
                case "godConditionAchieved":
                    setWinner(currentPlayer, result);
                    break;
                default:
                    logError("invalid turn");
                    break;
            }
        }
        gameOver();
    }

    /**
     * @return true if the Game has reached the maximum number of players, false otherwise
     */
    public boolean checkPlayersNumber() {
        return game.getPlayers().size() >= game.getPlayerNum();
    }

    /**
     * For every Player in the Game, checks if he has any Worker able to move. If a Player has no Workers left, eliminates him.
     *
     * @throws IOExceptionFromController when an IOException from a specific PlayerController occurs
     */
    public void checkWorkers() throws IOExceptionFromController {
        for (Player player : players) {
            if (!player.hasLost() && player.getWorkers().size() == 0) eliminatePlayer(player, "outOfWorkers");
        }
    }

    /**
     * Checks whether the Player who disconnected is currently in the Game or not.
     *
     * @param e          the caught exception
     * @param controller the controller to check
     * @throws IOExceptionFromController when an IOException from a specific PlayerController occurs
     */
    public void checkDisconnection(Exception e, PlayerController controller) throws IOExceptionFromController {
        if (controller == null) return;
        if (controller.getPlayer().hasLost()) {
            playerControllers.set(playerControllers.indexOf(controller), null);
        } else
            throw new IOExceptionFromController(e, controller);
    }

    /**
     * Handles the disconnection of a Player and, after notifying that disconnection, terminates the Game.
     *
     * @param controller the PlayerController associated to the Player who disconnected
     */
    public void handleDisconnection(PlayerController controller) {
        if (!running.get() || controller == null) return;
        if (controller.getPlayer().hasLost()) {
            playerControllers.set(playerControllers.indexOf(controller), null);
        } else {
            playerControllers.remove(controller);
            notifyDisconnection(controller.getPlayer());
            gameOver();
        }
    }

    /**
     * Removes a Player who was eliminated from the Game: removes his workers and the God Power which was eventually always active and notifies him his loss.
     * If only one Player is left, sets him as the winner. If there are two Players left, notifies them the elimination of the third one.
     *
     * @param player the Player to eliminate
     * @param reason the reason why the Player lost
     * @throws IOExceptionFromController when an IOException from a specific PlayerController is thrown
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

    /**
     * Calls the displayBuild method for each Player, so that the build can be displayed on screen.
     *
     * @param buildPosition the position of the build
     * @param godPower      the God Card that eventually allowed this build
     * @throws IOExceptionFromController when an IOException from a specific PlayerController occurs
     */
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
     * Broadcasts all the information associated with the current Game to all the Players.
     *
     * @param desc the description associated with this broadcast; can be
     *             • playerJoined: sends player info when a new player joins the game
     *             • gameSetup: sends player info after all players have joined
     *             • boardSetup: sends player info with god cards
     *             • gameStart: signals the end of the setup stage
     *             • turnStart: signals the beginning of a new turn
     *             • a notifyLoss description: signals the loss of the first player in a 3 player game
     * @throws IOExceptionFromController when an IOException from a specific PlayerController occurs
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
     * Broadcasts the message received as an argument to all the Players.
     *
     * @param message the message to show
     * @throws IOExceptionFromController when an IOException from a specific PlayerController occurs
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

    /**
     * Calls the displayMove method for each Player, so that the move can be displayed on screen.
     *
     * @param moves    an HashMap containing one or two moves associated with a turn
     * @param godPower the God Card that eventually allowed this move
     * @throws IOExceptionFromController when an IOException from a specific PlayerController occurs
     */
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

    /**
     * Calls the displayPlaceWorker method for each Player, so that the starting positions of the Worker can be displayed on screen.
     *
     * @param workerPosition the position of the Worker
     * @throws IOExceptionFromController when an IOException from a specific PlayerController occurs
     */
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

    /**
     * Notifies to all the other Players the disconnection of the Player received as an argument.
     *
     * @param player the Player who disconnected
     */
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
     * Sets the Player received as an argument as the winner (for the reason received as an argument).
     * Notifies each Player of his victory or of his loss.
     *
     * @param player the winner
     * @param reason the reason why the player won
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
     * Handles the end of the Game.
     * Sets all the playerControllers to null and notifies all the Players that the Game is over.
     */
    public void gameOver() {
        if (!running.compareAndSet(true, false)) return;
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
