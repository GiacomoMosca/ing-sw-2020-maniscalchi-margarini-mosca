package it.polimi.ingsw.view;

import it.polimi.ingsw.network.message.to_client.ToClientMessage;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The UI interface represents the User Interface, which is implemented by the GUI and the CLI.
 * It contains methods which allows the parsing of the messages from the server to the client,
 * methods which allows sending messages from the client to the server,
 * methods which allows to perform on the client the action requested by the message received by the server
 * (those actions can be: simple messages intended to inform the client about an event, notifications of any change in the View, messages containing questions and requiring an answer)
 */
public interface UI {

    /**
     * Allows the UI to run.
     */
    public void run();

    /**
     * Allows listening to the messages coming from the server.
     */
    public void serverListener();

    /**
     * Allows the UI to stop.
     */
    public void stop();

    /**
     * @return the String provided by the client as the IP address of the server he wants to connect to
     */
    public String getServerIp();

    /**
     * @param message the ToClientMessage to parse
     */
    public void parseMessage(ToClientMessage message);

    /**
     * @param body the boolean to send to the server in the SendBoolean Message
     */
    public void sendBoolean(boolean body);

    /**
     * @param body the int to send to the server in the SendInteger Message
     */
    public void sendInteger(int body);

    /**
     * @param body the ArrayList of Integers to send to the server in the SendIntegers Message
     */
    public void sendIntegers(ArrayList<Integer> body);

    /**
     * @param body the String to send to the server in the SendString Message
     */
    public void sendString(String body);

    /**
     * Allows asking the client to choose some Cards among those available.
     *
     * @param possibleCards an ArrayList containing all the CardViews representing the possible Cards
     * @param num           the number of Cards to choose
     * @param pickedCards   an ArrayList containing all the CardViews representing the already picked Cards
     */
    public void chooseCards(ArrayList<CardView> possibleCards, int num, ArrayList<CardView> pickedCards);

    /**
     * Allows asking the client to choose a Game name.
     *
     * @param taken true if the previously chosen Game name is already taken, false otherwise
     */
    public void chooseGameName(boolean taken);

    /**
     * Allows asking the client to choose which Game Room to join.
     *
     * @param gameRooms an ArrayList of GameViews containing all the Game Rooms
     */
    public void chooseGameRoom(ArrayList<GameView> gameRooms);

    /**
     * Allows asking the client to choose a nickname.
     *
     * @param taken true if the previously chosen nickname is already taken, false otherwise
     */
    public void chooseNickname(boolean taken);

    /**
     * Allows asking the client to choose the number of Players for the Game.
     */
    public void choosePlayersNumber();

    /**
     * Allows asking the client to choose a position among those available.
     *
     * @param positions an ArrayList containing CellViews representing all the available positions
     * @param desc      the reason of this choice
     */
    public void choosePosition(ArrayList<CellView> positions, String desc);

    /**
     * Allows asking the client to choose the Player which will start playing.
     *
     * @param players an ArrayList of PlayerViews representing all the Players involved in the Game
     */
    public void chooseStartingPlayer(ArrayList<PlayerView> players);

    /**
     * Allows asking the client to choose between starting a new Game or joining an existing one.
     */
    public void chooseStartJoin();

    /**
     * Allows asking the client a "yes or no question" and sending it to the server.
     *
     * @param query the question to ask to the client
     */
    public void chooseYesNo(String query);

    /**
     * Allows displaying a build.
     *
     * @param buildPosition the CellView representing the position of the building
     * @param godCard       the CardView representing the God Card which eventually allowed this build
     */
    public void displayBuild(CellView buildPosition, CardView godCard);

    /**
     * Allows displaying an information about the Game.
     *
     * @param game the GameView representing the current state of the Game
     * @param desc the information
     */
    public void displayGameInfo(GameView game, String desc);

    public void displayMessage(String message);

    /**
     * Allows displaying the move of a Worker.
     *
     * @param moves   an HashMap containing pairs of (starting position, final position) for each worker who moved or was forced to move
     * @param godCard the CardView representing the God Card which eventually allowed this move
     */
    public void displayMove(HashMap<CellView, CellView> moves, CardView godCard);

    /**
     * Allows displaying the starting position of a Worker.
     *
     * @param position the starting position of a Worker
     */
    public void displayPlaceWorker(CellView position);

    /**
     * Allows notifying the client of the disconnection of a Player.
     *
     * @param player the PlayerView representing the Player who disconnected
     */
    public void notifyDisconnection(PlayerView player);

    /**
     * Allows notifying the client the Game is over.
     */
    public void notifyGameOver();

    /**
     * Allows notifying the client the Game is starting.
     */
    public void notifyGameStarting();

    /**
     * Allows notifying the client of his loss.
     *
     * @param reason the reason of his loss
     * @param winner the Player who eventually won, can be null
     */
    public void notifyLoss(String reason, PlayerView winner);

    /**
     * Allows notifying the client of his victory.
     *
     * @param reason the reason of the victory
     */
    public void notifyWin(String reason);

    /**
     * Allows notifying the client that the server is down.
     */
    public void serverClosed();

}
