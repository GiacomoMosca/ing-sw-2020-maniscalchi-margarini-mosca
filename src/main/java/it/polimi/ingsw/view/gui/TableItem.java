package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.view.GameView;

public class TableItem {
    private String gameName;
    private String createdBy;
    private String players;

    public TableItem(GameView gameView) {
        this.gameName = gameView.getName();
        this.createdBy = gameView.getPlayers().get(0).getId();
        this.players = String.valueOf(gameView.getPlayers().size()) + "/" + String.valueOf(gameView.getPlayerNum());
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getPlayers() {
        return players;
    }

    public void setPlayers(String players) {
        this.players = players;
    }
}
