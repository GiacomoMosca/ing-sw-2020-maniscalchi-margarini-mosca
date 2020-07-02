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

/**
 * Handles the serialization of messages from server to client and the deserialization of messages from client to server.
 * Each Client is associated to a specific VirtualView.
 */
public class VirtualView {

    /**
     * Set to <code>false</code> when the client is disconnected.
     */
    private final AtomicBoolean alive;
    /**
     * Socket for client communications.
     */
    private final Socket socket;
    /**
     * Queue for all incoming messages from the client.
     */
    private final SynchronousQueue<ToServerMessage> messageQueue;
    /**
     * Queue for all incoming ping responses from the client.
     */
    private final SynchronousQueue<ToServerMessage> pingQueue;
    /**
     * InputStream for inputs from the client.
     */
    private ObjectInputStream input;
    /**
     * OutputStream for outputs to the client.
     */
    private ObjectOutputStream output;
    /**
     * The nickname chosen by the user.
     */
    private String id;
    /**
     * The controller associated with the client.
     */
    private PlayerController playerController;

    /**
     * VirtualView constructor.
     * This constructor:
     * <ul>
     *     <li>sets the attributes to the values received as arguments or their default values;
     *     <li>creates a messageQueue where the messages from the associated client to the server will be placed;
     *     <li>creates a pingQueue;
     *     <li>creates a Thread to continuously listen to the associated client, deserializing his messages;
     * </ul>
     *
     * @param socket the socket associated with the client
     * @param input  the ObjectInputStream associated with the socket
     * @param output the ObjectOutputStream associated with the socket
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

    /**
     * Allows resetting the ObjectInputStream and ObjectOutputStream associated to the communication on the client socket.
     *
     * @throws IOException when an exception related to ObjectOutputStream and ObjectInputStream occurs
     */
    public void resetStreams() throws IOException {
        input = new ObjectInputStream(socket.getInputStream());
        output = new ObjectOutputStream(socket.getOutputStream());
    }

    /**
     * @return the socket, ie the communication channel between server and the client associated with this VirtualView
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * @return the ID of the Player associated with this VirtualView
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id attribute to the String received as an argument (ie the ID of the Player associated with this VirtualView).
     *
     * @param id the ID of the Player
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the PlayerController associated with this VirtualView
     */
    public PlayerController getPlayerController() {
        return playerController;
    }

    /**
     * Sets the playerController attribute to the PlayerController Object received as an argument.
     *
     * @param playerController the PlayerController of the Player associated with this VirtualView
     */
    public void setPlayerController(PlayerController playerController) {
        this.playerController = playerController;
    }

    /**
     * @return true is the Player associated with this VirtualView is still in the Game, false otherwise
     */
    public boolean isInGame() {
        return (playerController != null);
    }

    /**
     * For all the time the VirtualView is active, this method allows listening to the associated client and deserializing the messages wrote by the client on the communication channel.
     * If the message is a Pong, offers it on the pingQueue; if the message is a message from the client to the server, offers it on the messageQueue.
     */
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

    /**
     * Allows taking from the messageQueue a message previously sent by the client to the server.
     *
     * @return the message taken from the messageQueue
     * @throws InterruptedException when the thread handling the communication is waiting and it is interrupted before or during its activity
     */
    public ToServerMessage takeInput() throws InterruptedException {
        ToServerMessage input = messageQueue.take();
        if (input instanceof ErrorMessage) throw new InterruptedException("disconnected");
        return input;
    }

    /**
     * Allows the server to check whether a client is still active or not.
     * Makes an attempt to send a Ping and to receive a Pong as a reply; if it fails, tries again.
     * If it fails again, throws an IOException described by a "time out" message.
     *
     * @throws IOException when the second attempt to receive a Pong fails
     */
    public void checkAlive() throws IOException {
        int attempts = 0;
        while (attempts < 2) {
            output.writeObject(new Ping());
            try {
                pingQueue.poll(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                attempts++;
                continue;
            }
            break;
        }
        if (attempts >= 2 || !alive.get()) throw new IOException("timed out");
    }

    /**
     * Sends a message to the client, asking him to choose some Cards among those available.
     * Creates a new Message (ChooseCards Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the client.
     * Takes the client's answer from the messageQueue, selects the Cards that the client chose and returns them in an ArrayList.
     *
     * @param possibleCards an ArrayList containing all the available Cards
     * @param num           the number of Cards to pick
     * @param pickedCards   an ArrayList containing all the already picked Cards
     * @return an ArrayList containing the chosen Cards
     * @throws IOException          when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException when the thread handling the communication is waiting and it is interrupted before or during its activity
     */
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

    /**
     * Sends a message to the client, asking the name for the Game he is creating.
     * Creates a new Message (ChooseGameName Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the client.
     * Takes the client's answer from the messageQueue and returns it.
     *
     * @param taken true if the previously chosen Game name is already taken, false otherwise
     * @return the Game name chosen by the client
     * @throws IOException          when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException when the thread handling the communication is waiting and it is interrupted before or during its activity
     */
    public String chooseGameName(boolean taken) throws IOException, InterruptedException {
        output.writeObject(new ChooseGameName(taken));
        return ((SendString) takeInput()).getBody();
    }

    /**
     * Sends a message to the client, asking him to choose which Game room to join to.
     * Creates a new Message (ChooseGameRoom Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the client.
     * Takes the client's answer from the messageQueue and returns it.
     *
     * @param gameRooms the Game rooms actually available to play
     * @return the Game room chosen by the client
     * @throws IOException          when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException when the thread handling the communication is waiting and it is interrupted before or during its activity
     */
    public String chooseGameRoom(ArrayList<Game> gameRooms) throws IOException, InterruptedException {
        ArrayList<GameView> gameRoomsView = new ArrayList<GameView>();
        for (Game game : gameRooms) {
            gameRoomsView.add(new GameView(game));
        }
        output.writeObject(new ChooseGameRoom(gameRoomsView));
        return ((SendString) takeInput()).getBody();
    }

    /**
     * Sends a message to the client, asking his nickname.
     * Creates a new Message (ChooseNickname Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the client.
     * Takes the client's answer from the messageQueue and returns it.
     *
     * @param taken true if the previously chosen nickname is already taken, false otherwise
     * @return the nickname chosen by the client
     * @throws IOException          when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException when the thread handling the communication is waiting and it is interrupted before or during its activity
     */
    public String chooseNickname(boolean taken) throws IOException, InterruptedException {
        output.writeObject(new ChooseNickname(taken));
        id = ((SendString) takeInput()).getSender();
        return id;
    }

    /**
     * Sends a message to the client, asking the number of players for the Game.
     * Creates a new Message (ChoosePlayersNumber Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the client.
     * Takes the client's answer from the messageQueue and returns it.
     *
     * @return the number of players chosen by the client for the Game
     * @throws IOException          when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException when the thread handling the communication is waiting and it is interrupted before or during its activity
     */
    public int choosePlayersNumber() throws IOException, InterruptedException {
        output.writeObject(new ChoosePlayersNumber());
        return ((SendInteger) takeInput()).getBody();
    }

    /**
     * Sends a message to the client, asking him the starting position for his Worker.
     * Creates a new Message (ChoosePosition Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the client.
     * Takes the client's answer from the messageQueue and returns it.
     *
     * @param possiblePositions an ArrayList containing all the positions the client can choose among
     * @param num               the number of the Worker the client has to choose the position of
     * @return the chosen position
     * @throws IOException          when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException when the thread handling the communication is waiting and it is interrupted before or during its activity
     */
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
     * Sends a message to the client, asking him the Worker he wants to use during the current turn.
     * Creates a new Message (ChooseWorker Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the client.
     * Takes the client's answer from the messageQueue and returns it.
     *
     * @param workers an ArrayList containing the available workers
     * @return the chosen Worker
     * @throws IOException          when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException when the thread handling the communication is waiting and it is interrupted before or during its activity
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
     * Sends a message to the client, asking him the position he wants to move his Worker to during the current turn.
     * Creates a new Message (ChoosePosition Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the client.
     * Takes the client's answer from the messageQueue and returns it.
     *
     * @param possibleMoves an ArrayList containing all the positions the client can choose among
     * @return the chosen position
     * @throws IOException          when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException when the thread handling the communication is waiting and it is interrupted before or during its activity
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
     * Sends a message to the client, asking him the position he wants his Worker to build in during the current turn.
     * Creates a new Message (ChoosePosition Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the client.
     * Takes the client's answer from the messageQueue and returns it.
     *
     * @param possibleBuilds an ArrayList containing all the positions the client can choose among
     * @return the chosen position
     * @throws IOException          when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException when the thread handling the communication is waiting and it is interrupted before or during its activity
     */
    public Cell chooseBuildPosition(ArrayList<Cell> possibleBuilds) throws IOException, InterruptedException {
        ArrayList<CellView> positions = new ArrayList<CellView>();
        for (Cell cell : possibleBuilds) {
            positions.add(new CellView(cell));
        }
        output.writeObject(new ChoosePosition(positions, "build"));
        return possibleBuilds.get(((SendInteger) takeInput()).getBody());
    }

    /**
     * Sends a message to the client, asking him which player should start playing.
     * Creates a new Message (ChooseStartingPlayer Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the client.
     * Takes the client's answer from the messageQueue and returns it.
     *
     * @param players an ArrayList containing all the players to choose among
     * @return the number associated to the chosen player
     * @throws IOException          when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException when the thread handling the communication is waiting and it is interrupted before or during its activity
     */
    public int chooseStartingPlayer(ArrayList<Player> players) throws IOException, InterruptedException {
        ArrayList<PlayerView> playerViews = new ArrayList<PlayerView>();
        for (Player player : players) {
            playerViews.add(new PlayerView(player));
        }
        output.writeObject(new ChooseStartingPlayer(playerViews));
        return ((SendInteger) takeInput()).getBody();
    }

    /**
     * Sends a message to the client, asking him to choose between starting a new Game or joining an existing one.
     * Creates a new Message (ChooseStartJoin Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the client.
     * Takes the client's answer from the messageQueue and returns it.
     *
     * @return true if the client chose to start a new Game, false if the client chose to join an existing one
     * @throws IOException          when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException when the thread handling the communication is waiting and it is interrupted before or during its activity
     */
    public boolean chooseStartJoin() throws IOException, InterruptedException {
        output.writeObject(new ChooseStartJoin());
        return ((SendBoolean) takeInput()).getBody();
    }

    /**
     * Sends a message to the client, asking him to answer to a "yes or no" question.
     * Creates a new Message (ChooseYesNo Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the client.
     * Takes the client's answer from the messageQueue and returns it.
     *
     * @param query the question the client should answer to
     * @return true if the client answered "yes", false if he answered "no"
     * @throws IOException          when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException when the thread handling the communication is waiting and it is interrupted before or during its activity
     */
    public boolean chooseYesNo(String query) throws IOException, InterruptedException {
        output.writeObject(new ChooseYesNo(query));
        return ((SendBoolean) takeInput()).getBody();
    }

    /**
     * Sends a message to the client, in order to notify him of a new build.
     * Creates a new Message (DisplayBuild Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the client.
     *
     * @param buildPosition the position of the new building
     * @param godPower      the Card associated to the God Power that eventually allowed this build
     * @throws IOException when an exception related to ObjectOutputStream and ObjectInputStream occurs
     */
    public void displayBuild(CellView buildPosition, Card godPower) throws IOException {
        CardView godView = (godPower == null) ? null : new CardView(godPower);
        output.writeObject(new DisplayBuild(buildPosition, godView));
    }

    /**
     * Sends a message to the client, in order to notify him an information.
     * Creates a new Message (DisplayGameInfo Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the client.
     *
     * @param game the current situation of the Game
     * @param desc the information
     * @throws IOException when an exception related to ObjectOutputStream and ObjectInputStream occurs
     */
    public void displayGameInfo(Game game, String desc) throws IOException {
        GameView gameView = new GameView(game);
        output.writeObject(new DisplayGameInfo(gameView, desc));
    }


    /**
     * Sends a message to the client, in order to notify him an information.
     * Creates a new Message (DisplayMessage Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the client.
     *
     * @param message the String describing the message to sent
     * @throws IOException when an exception related to ObjectOutputStream and ObjectInputStream occurs
     */
    public void displayMessage(String message) throws IOException {
        output.writeObject(new DisplayMessage(message));
    }

    /**
     * Sends a message to the client, in order to notify him of a new move.
     * Creates a new Message (DisplayMove Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the client.
     * The HashMap received as an argument will contain:
     * - a pair of positions if only one Worker moved;
     * - two pairs of positions if two Workers moved (in this case the godPower parameter mustn't be null: this kind of double move is only allowed thanks to a God Power).
     *
     * @param moves    an HashMap containing the starting position and the final position of each Worker who moved.
     * @param godPower the Card associated to the God Power that eventually allowed this move
     * @throws IOException when an exception related to ObjectOutputStream and ObjectInputStream occurs
     */
    public void displayMove(HashMap<CellView, CellView> moves, Card godPower) throws IOException {
        CardView godView = (godPower == null) ? null : new CardView(godPower);
        output.writeObject(new DisplayMove(moves, godView));
    }

    /**
     * Sends a message to the client, in order to notify him the placing of a Worker.
     * Creates a new Message (DisplayPlaceWorker Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the client.
     *
     * @param workerPosition the position of the Worker
     * @throws IOException when an exception related to ObjectOutputStream and ObjectInputStream occurs
     */
    public void displayPlaceWorker(Cell workerPosition) throws IOException {
        CellView cellView = new CellView(workerPosition);
        output.writeObject(new DisplayPlaceWorker(cellView));
    }

    /**
     * Sends a message to the client, in order to notify him the Game is starting.
     * Creates a new Message (NotifyGameStarting Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the client.
     *
     * @throws IOException          when an exception related to ObjectOutputStream and ObjectInputStream occurs
     * @throws InterruptedException when the thread handling the communication is waiting and it is interrupted before or during its activity
     */
    public void notifyGameStarting() throws IOException, InterruptedException {
        output.writeObject(new NotifyGameStarting());
        takeInput();
    }

    /**
     * Sends a message to the client, in order to notify him his loss.
     * Creates a new Message (NotifyLoss Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the client.
     *
     * @param reason the reason he lost, can be:
     *               <ul>
     *                  <li>"outOfWorkers" if the Player lost because he ran out of Workers;
     *                  <li>"outOfMoves" if the Player lost because he ran out of moves;
     *                  <li>"outOfBuilds" if the Player lost because he ran out of builds;
     *                  <li>"godConditionAchieved" if the Player lost because another Worker achieved his God's win condition;
     *                  <li>"winConditionAchieved" if the Player lost because another Worker achieved the normal win condition.
     *               </ul>
     * @param winner null if the Player lost by his own, not null if the Player lost because another Player won
     * @throws IOException when an exception related to ObjectOutputStream and ObjectInputStream occurs
     */
    public void notifyLoss(String reason, Player winner) throws IOException {
        PlayerView winnerView = (winner == null) ? null : new PlayerView(winner);
        output.writeObject(new NotifyLoss(reason, winnerView));
    }

    /**
     * Sends a message to the client, in order to notify him his victory.
     * Creates a new Message (NotifyWin Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the client.
     *
     * @param reason the reason of his victory, can be
     *               <ul>
     *                  <li>"godConditionAchieved" if the Player won by achieving his God's specific win condition;
     *                  <li>"winConditionAchieved" if the Player won by achieving the normal win condition;
     *                  <li>"outOfWorkers" if the Player won because the only Player left ran out of Workers;
     *                  <li>"outOfMoves", if the Player won because the only Player left ran out of moves;
     *                  <li>"outOfBuilds", if the Player won because the only Player left he ran out of builds.
     *               </ul>
     * @throws IOException when an exception related to ObjectOutputStream and ObjectInputStream occurs
     */
    public void notifyWin(String reason) throws IOException {
        output.writeObject(new NotifyWin(reason));
    }

    /**
     * Sends a message to the client, in order to notify him that a Player disconnected.
     * Creates a new Message (NotifyDisconnection Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the client.
     *
     * @param player the client who disconnected
     * @throws IOException when an exception related to ObjectOutputStream and ObjectInputStream occurs
     */
    public void notifyDisconnection(Player player) throws IOException {
        PlayerView playerView = new PlayerView(player);
        output.writeObject(new NotifyDisconnection(playerView));
    }

    /**
     * Sends a message to the client, in order to notify him the Game is over.
     * Creates a new Message (NotifyGameOver Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the client.
     *
     * @throws IOException when an exception related to ObjectOutputStream and ObjectInputStream occurs
     */
    public void notifyGameOver() throws IOException {
        output.writeObject(new NotifyGameOver());
    }

    /**
     * Sends a message to the client, in order to notify him that the server is closing.
     * Creates a new Message (NotifyGameOver Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the client.
     *
     * @throws IOException when an exception related to ObjectOutputStream and ObjectInputStream occurs
     */
    public void serverClosed() throws IOException {
        output.writeObject(new ServerClosed());
    }

}
