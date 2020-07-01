package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.view.GameView;

/**
 * Represents an item for the Table containing information about the active Game rooms.
 */
public class TableItem {

    /**
     * The Game name.
     */
    private String gameName;
    /**
     * The ID of the Game's creator.
     */
    private String createdBy;
    /**
     * The number of players currently in the Game out of the total number of players required.
     */
    private String players;

    /**
     * @param gameView the GameView representing the current state of the Game
     */
    public TableItem(GameView gameView) {
        this.gameName = gameView.getName();
        this.createdBy = gameView.getPlayers().get(0).getId();
        this.players = gameView.getPlayers().size() + "/" + gameView.getPlayerNum();
    }

    /**
     * @return the Game Name of this TableItem
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * @param gameName the Game name to set this TableItem to
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    /**
     * @return the ID of the Player who created the Game room this TableItem refers to
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy the ID of the Player who created the Game room this TableItem refers to
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return the number of Players involved in the Game this TableItem refers to
     */
    public String getPlayers() {
        return players;
    }

    /**
     * @param players the number of Players involved in the Game this TableItem refers to
     */
    public void setPlayers(String players) {
        this.players = players;
    }
}
