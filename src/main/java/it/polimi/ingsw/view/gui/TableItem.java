package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.view.GameView;

public class TableItem {
    private String gameName;
    private String playersInRoom;

    public TableItem(GameView gameView) {
        this.gameName = gameView.getName();
        this.playersInRoom = String.valueOf(gameView.getPlayers().size()) + "/" + String.valueOf(gameView.getPlayerNum());
    }

    public String getGameName() {
        return gameName;
    }

    public String getPlayersInRoom() {
        return playersInRoom;
    }
}