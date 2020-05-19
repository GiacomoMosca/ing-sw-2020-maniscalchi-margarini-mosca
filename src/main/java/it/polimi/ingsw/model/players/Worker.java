package it.polimi.ingsw.model.players;

import it.polimi.ingsw.exceptions.IllegalMoveException;
import it.polimi.ingsw.model.game_board.Cell;

/**
 * do we need to write a JavaDoc comment for every single class?
 */
public class Worker {

    private final Player owner;
    private final int num;
    private Cell position;

    /**
     * Creates a Worker, setting the player received as an argument as the actual owner of this Worker.
     *
     * @param owner the player who owns the worker
     */
    public Worker(Player owner, int num) {
        this.owner = owner;
        this.num = num;
        this.position = null;
    }

    /**
     * @return the player who owns the worker
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * @return the worker's number
     */
    public int getNum() {
        return num;
    }

    /**
     * @return the cell where the worker is located
     */
    public Cell getPosition() {
        return position;
    }

    /**
     * Sets the cell where the worker is located.
     *
     * @param position
     */
    public void setPosition(Cell position) {
        position.setWorker(this);
        this.position = position;
    }

    /**
     * Properly moves the worker to a new unoccupied cell.
     * Before doing this, checks if the desired position is available to be occupied.
     *
     * @param position the cell representing the new position of the worker
     * @throws IllegalArgumentException when trying to move the worker to a cell with a dome or occupied by another worker
     */
    public void move(Cell position) throws IllegalMoveException {
        if (position.isDomed())
            throw new IllegalMoveException("cell [" + position.getPosX() + "," + position.getPosY() + "] has a dome");
        if (position.hasWorker())
            throw new IllegalMoveException("cell [" + position.getPosX() + "," + position.getPosY() + "] has a worker");
        this.position.setWorker(null);
        position.setWorker(this);
        this.position = position;
    }

}
