package it.polimi.ingsw.view;

import it.polimi.ingsw.network.message.to_client.ToClientMessage;

import java.util.ArrayList;

public interface UI {

    public void start();

    public void stop();

    public void parseMessage(ToClientMessage message);

    public String getServerIp();

    /**
     * shows the game board
     *
     * @param board the Board of the current game
     */
    public void displayBoard(BoardView board);

    /**
     * shows a message
     *
     * @param message the message to show
     */
    public void displayMessage(String message);

    public void choosePosition(ArrayList<CellView> positions, String desc);

    /**
     *
     * @param query the question the player should answer to
     * @return true if the player answered "yes", false if the player answered "no"
     */
    public void chooseYesNo(String query);

}
