package it.polimi.ingsw.model.players;

import it.polimi.ingsw.model.game_board.Cell;

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
     * sets the cell where the worker is located
     *
     * @param position
     */
    public void setPosition(Cell position) {
        this.position = position;
    }

    /**
     * properly moves the worker to a new unoccupied cell
     *
     * @param position the cell representing the new position of the worker
     * @throws IllegalArgumentException when trying to move the worker to a cell with a dome or another worker
     */
    public void move(Cell position) throws IllegalArgumentException {
        if (position.isDomed() || position.hasWorker()) throw new IllegalArgumentException();
        this.position.setWorker(null);
        position.setWorker(this);
        this.position = position;
    }

}
