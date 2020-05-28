package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.PlayerController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.network.message.to_client.*;
import it.polimi.ingsw.network.message.to_server.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class VirtualView {

    private final AtomicBoolean alive;
    private final Socket socket;
    private final SynchronousQueue<ToServerMessage> messageQueue;
    private final SynchronousQueue<ToServerMessage> pingQueue;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private String id;
    private PlayerController playerController;

    /**
     * creates a VirtualView associated with the Interface received as an argument
     */
    public VirtualView(Socket socket, ObjectInputStream input, ObjectOutputStream output) {
        this.alive = new AtomicBoolean(true);
        this.socket = socket;
        this.input = input;
        this.messageQueue = new SynchronousQueue<ToServerMessage>();
        this.pingQueue = new SynchronousQueue<ToServerMessage>();
        this.output = output;
        this.playerController = null;
        new Thread(this::clientListener).start();
    }

    public void resetStreams() throws IOException {
        input = new ObjectInputStream(socket.getInputStream());
        output = new ObjectOutputStream(socket.getOutputStream());
    }

    public Socket getSocket() {
        return socket;
    }

    /**
     * @return the id associated with this VirtualView (ie the id of the Player associated with it)
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PlayerController getPlayerController() {
        return playerController;
    }

    public void setPlayerController(PlayerController playerController) {
        this.playerController = playerController;
    }

    public boolean isInGame() {
        return (playerController != null);
    }

    public void clientListener() {
        ToServerMessage clientMessage;
        while (alive.get()) {
            try {
                clientMessage = (ToServerMessage) input.readObject();
            } catch (IOException | ClassNotFoundException e) {
                alive.compareAndSet(true, false);
                messageQueue.offer(new ErrorMessage("disconnected"));
                break;
            }
            if (clientMessage instanceof Pong) pingQueue.offer(clientMessage);
            else messageQueue.offer(clientMessage);
        }
    }

    public ToServerMessage takeInput() throws InterruptedException {
        ToServerMessage input = messageQueue.take();
        if (input instanceof ErrorMessage) throw new InterruptedException("disconnected");
        return input;
    }

    public void checkAlive() throws IOException {
        int attempts = 0;
        while (attempts < 2) {
            output.writeObject(new Ping());
            try {
                pingQueue.poll(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                attempts++;
                continue;
            }
            break;
        }
        if (attempts >= 2 || !alive.get()) throw new IOException("timed out");
    }

    public ArrayList<Card> chooseCards(ArrayList<Card> possibleCards, int num, ArrayList<Card> pickedCards) throws IOException, InterruptedException {
        ArrayList<CardView> possibleCardsView = new ArrayList<CardView>();
        for (Card card : possibleCards) {
            possibleCardsView.add(new CardView(card));
        }
        ArrayList<CardView> pickedCardsView = new ArrayList<CardView>();
        if (pickedCards == null) pickedCardsView = null;
        else {
            for (Card card : pickedCards) {
                pickedCardsView.add(new CardView(card));
            }
        }
        output.writeObject(new ChooseCards(possibleCardsView, num, pickedCardsView));
        ArrayList<Integer> choices = ((SendIntegers) takeInput()).getBody();
        ArrayList<Card> chosenCards = new ArrayList<Card>();
        for (int i : choices) {
            chosenCards.add(possibleCards.get(i));
        }
        return chosenCards;
    }

    public String chooseGameName(boolean taken) throws IOException, InterruptedException {
        output.writeObject(new ChooseGameName(taken));
        return ((SendString) takeInput()).getBody();
    }

    public String chooseGameRoom(ArrayList<Game> gameRooms) throws IOException, InterruptedException {
        ArrayList<GameView> gameRoomsView = new ArrayList<GameView>();
        for (Game game : gameRooms) {
            gameRoomsView.add(new GameView(game));
        }
        output.writeObject(new ChooseGameRoom(gameRoomsView));
        return ((SendString) takeInput()).getBody();
    }

    public String chooseNickname(boolean taken) throws IOException, InterruptedException {
        output.writeObject(new ChooseNickname(taken));
        id = ((SendString) takeInput()).getSender();
        return id;
    }

    public int choosePlayersNumber() throws IOException, InterruptedException {
        output.writeObject(new ChoosePlayersNumber());
        return ((SendInteger) takeInput()).getBody();
    }

    public Cell chooseStartPosition(ArrayList<Cell> possiblePositions, int num) throws IOException, InterruptedException {
        ArrayList<CellView> positions = new ArrayList<CellView>();
        for (Cell cell : possiblePositions) {
            positions.add(new CellView(cell));
        }
        String desc = "start" + num;
        output.writeObject(new ChoosePosition(positions, desc));
        return possiblePositions.get(((SendInteger) takeInput()).getBody());
    }

    /**
     * allows the player to choose a worker for his current turn
     *
     * @param workers the workers the player can choose for his turn
     * @return the worker the player chose
     */
    public Worker chooseWorker(ArrayList<Worker> workers) throws IOException, InterruptedException {
        ArrayList<CellView> positions = new ArrayList<CellView>();
        for (Worker worker : workers) {
            positions.add(new CellView(worker.getPosition()));
        }
        output.writeObject(new ChoosePosition(positions, "worker"));
        return workers.get(((SendInteger) takeInput()).getBody());
    }

    /**
     * allows the player to choose a move for one of his workers
     *
     * @param possibleMoves an ArrayList containing all the possible moves a player can do with a worker
     * @return the cell the player decided to move his worker to
     */
    public Cell chooseMovePosition(ArrayList<Cell> possibleMoves) throws IOException, InterruptedException {
        ArrayList<CellView> positions = new ArrayList<CellView>();
        for (Cell cell : possibleMoves) {
            positions.add(new CellView(cell));
        }
        output.writeObject(new ChoosePosition(positions, "move"));
        return possibleMoves.get(((SendInteger) takeInput()).getBody());
    }

    /**
     * allows the player to choose a build for one of his workers
     *
     * @param possibleBuilds an ArrayList containing all the possible builds a player can do with a worker
     * @return the cell the player decided to build on
     */
    public Cell chooseBuildPosition(ArrayList<Cell> possibleBuilds) throws IOException, InterruptedException {
        ArrayList<CellView> positions = new ArrayList<CellView>();
        for (Cell cell : possibleBuilds) {
            positions.add(new CellView(cell));
        }
        output.writeObject(new ChoosePosition(positions, "build"));
        return possibleBuilds.get(((SendInteger) takeInput()).getBody());
    }

    public int chooseStartingPlayer(ArrayList<Player> players) throws IOException, InterruptedException {
        ArrayList<PlayerView> playerViews = new ArrayList<PlayerView>();
        for (Player player : players) {
            playerViews.add(new PlayerView(player));
        }
        output.writeObject(new ChooseStartingPlayer(playerViews));
        return ((SendInteger) takeInput()).getBody();
    }

    public boolean chooseStartJoin() throws IOException, InterruptedException {
        output.writeObject(new ChooseStartJoin());
        return ((SendBoolean) takeInput()).getBody();
    }

    /**
     * @param query the question the player should answer to
     * @return true if the player answered "yes", false if the player answered "no"
     */
    public boolean chooseYesNo(String query) throws IOException, InterruptedException {
        output.writeObject(new ChooseYesNo(query));
        return ((SendBoolean) takeInput()).getBody();
    }

    public void displayBuild(CellView buildPosition, Card godPower) throws IOException {
        CardView godView = (godPower == null) ? null : new CardView(godPower);
        output.writeObject(new DisplayBuild(buildPosition, godView));
    }

    /**
     * shows the Board of the current Game
     *
     * @param game
     * @param desc
     */
    public void displayGameInfo(Game game, String desc) throws IOException {
        GameView gameView = new GameView(game);
        output.writeObject(new DisplayGameInfo(gameView, desc));
    }

    /**
     * shows the message received as an argument
     *
     * @param message
     */
    public void displayMessage(String message) throws IOException {
        output.writeObject(new DisplayMessage(message));
    }

    public void displayMove(HashMap<CellView, CellView> moves, Card godPower) throws IOException {
        CardView godView = (godPower == null) ? null : new CardView(godPower);
        output.writeObject(new DisplayMove(moves, godView));
    }

    public void displayPlaceWorker(Cell workerPosition) throws IOException {
        CellView cellView = new CellView(workerPosition);
        output.writeObject(new DisplayPlaceWorker(cellView));
    }

    public void notifyGameStarting() throws IOException, InterruptedException {
        output.writeObject(new NotifyGameStarting());
        takeInput();
    }

    public void notifyLoss(String reason, Player winner) throws IOException {
        PlayerView winnerView = (winner == null) ? null : new PlayerView(winner);
        output.writeObject(new NotifyLoss(reason, winnerView));
    }

    public void notifyWin(String reason) throws IOException {
        output.writeObject(new NotifyWin(reason));
    }

    public void notifyDisconnection(Player player) throws IOException {
        PlayerView playerView = new PlayerView(player);
        output.writeObject(new NotifyDisconnection(playerView));
    }

    public void notifyGameOver() throws IOException {
        output.writeObject(new NotifyGameOver());
    }

}
