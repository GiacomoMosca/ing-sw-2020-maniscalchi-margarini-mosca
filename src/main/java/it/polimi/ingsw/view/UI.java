package it.polimi.ingsw.view;

import it.polimi.ingsw.network.message.to_client.ToClientMessage;

import java.util.ArrayList;
import java.util.HashMap;

public interface UI {

    public void run();

    public void serverListener();

    public void stop();

    public String getServerIp();

    public void parseMessage(ToClientMessage message);

    public void sendBoolean(boolean body);

    public void sendInteger(int body);

    public void sendIntegers(ArrayList<Integer> body);

    public void sendString(String body);

    public void chooseCards(ArrayList<CardView> possibleCards, int num, ArrayList<CardView> pickedCards);

    public void chooseGameName(boolean taken);

    public void chooseGameRoom(ArrayList<GameView> gameRooms);

    public void chooseNickname(boolean taken);

    public void choosePlayersNumber();

    public void choosePosition(ArrayList<CellView> positions, String desc);

    public void chooseStartingPlayer(ArrayList<PlayerView> players);

    public void chooseStartJoin();

    /**
     * @param query the question the player should answer to
     * @return true if the player answered "yes", false if the player answered "no"
     */
    public void chooseYesNo(String query);

    public void displayBuild(CellView buildPosition, CardView godCard);

    /**
     * shows the game board
     *
     * @param game the Board of the current game
     */
    public void displayGameInfo(GameView game, String desc);

    /**
     * shows a message
     *
     * @param message the message to show
     */
    public void displayMessage(String message);

    public void displayMove(HashMap<CellView, CellView> moves, CardView godCard);

    public void displayPlaceWorker(CellView position);

    public void notifyDisconnection(PlayerView player);

    public void notifyGameOver();

    public void notifyGameStarting();

    public void notifyLoss(String reason, PlayerView winner);

    public void notifyWin(String reason);

}
