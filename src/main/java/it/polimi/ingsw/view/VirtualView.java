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

public class VirtualView {

    private final Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private String id;
    private PlayerController playerController;

    /**
     * creates a VirtualView associated with the Interface received as an argument
     */
    public VirtualView(Socket socket, ObjectInputStream input, ObjectOutputStream output) {
        this.socket = socket;
        this.input = input;
        this.output = output;
        this.playerController = null;
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

    public void checkAlive() throws IOException {
        output.writeObject(new Ping());
    }

    public ArrayList<Card> chooseCards(ArrayList<Card> possibleCards, int num, ArrayList<Card> pickedCards) throws IOException, ClassNotFoundException {
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
        ArrayList<Integer> choices = ((SendIntegers) input.readObject()).getBody();
        ArrayList<Card> chosenCards = new ArrayList<Card>();
        for (int i : choices) {
            chosenCards.add(possibleCards.get(i));
        }
        return chosenCards;
    }

    public String chooseGameName(boolean taken) throws IOException, ClassNotFoundException {
        output.writeObject(new ChooseGameName(taken));
        return ((SendString) input.readObject()).getBody();
    }

    public String chooseGameRoom(ArrayList<Game> gameRooms) throws IOException, ClassNotFoundException {
        ArrayList<GameView> gameRoomsView = new ArrayList<GameView>();
        for (Game game : gameRooms) {
            gameRoomsView.add(new GameView(game));
        }
        output.writeObject(new ChooseGameRoom(gameRoomsView));
        return ((SendString) input.readObject()).getBody();
    }

    public String chooseNickname(boolean taken) throws IOException, ClassNotFoundException {
        output.writeObject(new ChooseNickname(taken));
        id = ((SendString) input.readObject()).getSender();
        return id;
    }

    public int choosePlayersNumber() throws IOException, ClassNotFoundException {
        output.writeObject(new ChoosePlayersNumber());
        return ((SendInteger) input.readObject()).getBody();
    }

    public Cell chooseStartPosition(ArrayList<Cell> possiblePositions, int num) throws IOException, ClassNotFoundException {
        ArrayList<CellView> positions = new ArrayList<CellView>();
        for (Cell cell : possiblePositions) {
            positions.add(new CellView(cell));
        }
        String desc = "start" + num;
        ChoosePosition msg = new ChoosePosition(positions, desc);
        output.writeObject(msg);
        return possiblePositions.get(((SendInteger) input.readObject()).getBody());
    }

    /**
     * allows the player to choose a worker for his current turn
     *
     * @param workers the workers the player can choose for his turn
     * @return the worker the player chose
     */
    public Worker chooseWorker(ArrayList<Worker> workers) throws IOException, ClassNotFoundException {
        ArrayList<CellView> positions = new ArrayList<CellView>();
        for (Worker worker : workers) {
            positions.add(new CellView(worker.getPosition()));
        }
        ChoosePosition msg = new ChoosePosition(positions, "worker");
        output.writeObject(msg);
        return workers.get(((SendInteger) input.readObject()).getBody());
    }

    /**
     * allows the player to choose a move for one of his workers
     *
     * @param possibleMoves an ArrayList containing all the possible moves a player can do with a worker
     * @return the cell the player decided to move his worker to
     */
    public Cell chooseMovePosition(ArrayList<Cell> possibleMoves) throws IOException, ClassNotFoundException {
        ArrayList<CellView> positions = new ArrayList<CellView>();
        for (Cell cell : possibleMoves) {
            positions.add(new CellView(cell));
        }
        ChoosePosition msg = new ChoosePosition(positions, "move");
        output.writeObject(msg);
        return possibleMoves.get(((SendInteger) input.readObject()).getBody());
    }

    /**
     * allows the player to choose a build for one of his workers
     *
     * @param possibleBuilds an ArrayList containing all the possible builds a player can do with a worker
     * @return the cell the player decided to build on
     */
    public Cell chooseBuildPosition(ArrayList<Cell> possibleBuilds) throws IOException, ClassNotFoundException {
        ArrayList<CellView> positions = new ArrayList<CellView>();
        for (Cell cell : possibleBuilds) {
            positions.add(new CellView(cell));
        }
        ChoosePosition msg = new ChoosePosition(positions, "build");
        output.writeObject(msg);
        return possibleBuilds.get(((SendInteger) input.readObject()).getBody());
    }

    public int chooseStartingPlayer(ArrayList<Player> players) throws IOException, ClassNotFoundException {
        ArrayList<PlayerView> playerViews = new ArrayList<PlayerView>();
        for (Player player : players) {
            playerViews.add(new PlayerView(player));
        }
        output.writeObject(new ChooseStartingPlayer(playerViews));
        return ((SendInteger) input.readObject()).getBody();
    }

    public boolean chooseStartJoin() throws IOException, ClassNotFoundException {
        output.writeObject(new ChooseStartJoin());
        return ((SendBoolean) input.readObject()).getBody();
    }

    /**
     * @param query the question the player should answer to
     * @return true if the player answered "yes", false if the player answered "no"
     */
    public boolean chooseYesNo(String query) throws IOException, ClassNotFoundException {
        ChooseYesNo msg = new ChooseYesNo(query);
        output.writeObject(msg);
        return ((SendBoolean) input.readObject()).getBody();
    }

    public void displayBuild(CellView buildPosition, Card godPower) throws IOException {
        CardView godView = (godPower == null) ? null : new CardView(godPower);
        DisplayBuild msg = new DisplayBuild(buildPosition, godView);
        output.writeObject(msg);
    }

    /**
     * shows the Board of the current Game
     *
     * @param game
     * @param desc
     */
    public void displayGameInfo(Game game, String desc) throws IOException {
        GameView gameView = new GameView(game);
        DisplayGameInfo msg = new DisplayGameInfo(gameView, desc);
        output.writeObject(msg);
    }

    /**
     * shows the message received as an argument
     *
     * @param message
     */
    public void displayMessage(String message) throws IOException {
        DisplayMessage msg = new DisplayMessage(message);
        output.writeObject(msg);
    }

    public void displayMove(HashMap<CellView, CellView> moves, Card godPower) throws IOException {
        CardView godView = (godPower == null) ? null : new CardView(godPower);
        DisplayMove msg = new DisplayMove(moves, godView);
        output.writeObject(msg);
    }

    public void displayPlaceWorker(Cell workerPosition) throws IOException {
        DisplayPlaceWorker msg = new DisplayPlaceWorker(new CellView(workerPosition));
        output.writeObject(msg);
    }

    public void notifyLoss(Player player, String reason) throws IOException {
        PlayerView playerView = new PlayerView(player);
        NotifyLoss msg = new NotifyLoss(playerView, reason);
        output.writeObject(msg);
    }

    public void notifyWin(Player player, String reason) throws IOException {
        PlayerView playerView = new PlayerView(player);
        NotifyWin msg = new NotifyWin(playerView, reason);
        output.writeObject(msg);
    }

    public void notifyDisconnection(Player player) throws IOException {
        PlayerView playerView = new PlayerView(player);
        NotifyDisconnection msg = new NotifyDisconnection(playerView);
        output.writeObject(msg);
    }

    public void gameOver() throws IOException {
        NotifyGameOver msg = new NotifyGameOver();
        output.writeObject(msg);
    }

}
