package it.polimi.ingsw.model.players;

import it.polimi.ingsw.exceptions.IllegalMoveException;
import it.polimi.ingsw.model.game_board.Cell;

/**
 * Describes a Player's Worker within a specific Game.
 * Stores a reference to its owner (a Player), the number which identifies it, the Cell representing its position.
 */
public class Worker {

    /**
     * The Worker's owner.
     */
    private final Player owner;
    /**
     * The Worker's number (1 or 2).
     */
    private final int num;
    /**
     * The Worker's current position in the Game Board.
     */
    private Cell position;

    /**
     * Worker constructor. Sets the player received as an argument as the actual owner of this Worker.
     *
     * @param owner the Player who owns the Worker
     * @param num the number identifying this Worker (0 or 1)
     */
    public Worker(Player owner, int num) {
        this.owner = owner;
        this.num = num;
        this.position = null;
    }

    /**
     * @return the Player who owns the Worker
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * @return the Worker's number
     */
    public int getNum() {
        return num;
    }

    /**
     * @return the Cell where the Worker is located
     */
    public Cell getPosition() {
        return position;
    }

    /**
     * Sets the Cell received as an argument as the Cell where the Worker is located.
     *
     * @param position the Cell to set this Worker on
     */
    public void setPosition(Cell position) {
        position.setWorker(this);
        this.position = position;
    }

    /**
     * Properly moves the Worker to a new unoccupied Cell.
     * Before doing this, checks if the desired position is vacant so that it can be occupied.
     *
     * @param position the Cell representing the new position of the Worker
     * @throws IllegalMoveException when trying to move the Worker to a Cell with a dome or a Cell occupied by another Worker
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
