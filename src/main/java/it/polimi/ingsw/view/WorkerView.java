package it.polimi.ingsw.view;

import it.polimi.ingsw.model.players.Worker;

import java.io.Serializable;

public class WorkerView implements Serializable {

    private final PlayerView player;
    private final String color;
    private final int num;

    public WorkerView(Worker worker) {
        this.player = new PlayerView(worker.getOwner());
        this.color = worker.getOwner().getColor();
        this.num = worker.getNum();
    }

    public WorkerView(PlayerView player, String color, int num) {
        this.player = player;
        this.color = color;
        this.num = num;
    }

    public PlayerView getPlayer() {
        return player;
    }

    public String getColor() {
        return color;
    }

    public int getNum() {
        return num;
    }

}
