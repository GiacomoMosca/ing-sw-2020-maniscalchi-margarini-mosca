package it.polimi.ingsw.model.Players;

import it.polimi.ingsw.model.GameBoard.Cell;
import it.polimi.ingsw.model.Players.Player;

public class Worker {

    private final Player owner;
    private Cell position;

    /**
     * creates a worker
     *
     * @param owner the player who owns the worker
     */
    public Worker(Player owner) {
        this.owner = owner;
        this.position = null;
    }

    /**
     *
     * @return the player who owns the worker
     */
    public Player getOwner() {
        return owner;
    }

    /**
     *
     * @return the cell where the worker is located
     */
    public Cell getPosition() {
        return position;
    }

    /**
     * moves the worker in a new position
     *
     * @param position the cell representing the new position of the worker
     */
    public void move(Cell position) {
        this.position = position;
    }

}
