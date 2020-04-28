package it.polimi.ingsw.view;

import it.polimi.ingsw.network.message.to_client.ToClientMessage;

import java.util.ArrayList;

public interface UI {

    public void run();

    public void stop();

    public void parseMessage(ToClientMessage message);

    public String getServerIp();

    public void chooseNickname(ArrayList<String> playerList);

    public void choosePlayersNumber();

    /**
     * shows the game board
     *
     * @param board the Board of the current game
     */
    public void displayBoard(GameView board);

    /**
     * shows a message
     *
     * @param message the message to show
     */
    public void displayMessage(String message);

    public void choosePosition(ArrayList<CellView> positions, String desc);

    /**
     * @param query the question the player should answer to
     * @return true if the player answered "yes", false if the player answered "no"
     */
    public void chooseYesNo(String query);

    public void notifyLoss(PlayerView player, String reason);

    public void notifyWin(PlayerView player, String reason);

    public void gameOver();

}
