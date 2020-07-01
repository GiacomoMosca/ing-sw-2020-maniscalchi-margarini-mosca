package it.polimi.ingsw.view;

import it.polimi.ingsw.model.players.Worker;

import java.io.Serializable;

/**
 * Represents a view of the Worker class to the Client.
 * It implements serializable so that it can be serialized in the messages that client and server exchange: this way, the client won't have access to the Model objects.
 */
public class WorkerView implements Serializable {

    /**
     * The Worker's owner.
     */
    private final PlayerView player;
    /**
     * The Worker's color.
     */
    private final String color;
    /**
     * The Worker's number (1 or 2).
     */
    private final int num;

    /**
     * WorkerView constructor.
     * WorkerView attributes are set to the values of the same attributes of the Worker Object received as an argument.
     *
     * @param worker the Worker this WorkerView represents
     */
    public WorkerView(Worker worker) {
        this.player = new PlayerView(worker.getOwner());
        this.color = worker.getOwner().getColor();
        this.num = worker.getNum();
    }

    /**
     * WorkerView constructor.
     * WorkerView attributes are set to the values received as arguments.
     *
     * @param player the Player who owns the Worker this WorkerView represents
     * @param color  the color of the Worker
     * @param num    the number which identifies the Worker
     */
    public WorkerView(PlayerView player, String color, int num) {
        this.player = player;
        this.color = color;
        this.num = num;
    }

    /**
     * @return the PlayerView representing the Player who owns the Worker represented by this WorkerView
     */
    public PlayerView getPlayer() {
        return player;
    }

    /**
     * @return the color of the Worker represented by this WorkerView
     */
    public String getColor() {
        return color;
    }

    /**
     * @return the number which identifies the Worker represented by this WorkerView
     */
    public int getNum() {
        return num;
    }

}
