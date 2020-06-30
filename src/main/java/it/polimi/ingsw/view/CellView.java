package it.polimi.ingsw.view;

import it.polimi.ingsw.model.game_board.Cell;

import java.io.Serializable;

/**
 * Represents a view of the Cell class to the Client.
 * It implements serializable so that it can be serialized in the messages that client and server exchange: this way, the client won't have access to the Model objects.
 */
public class CellView implements Serializable {

    private final int posX;
    private final int posY;
    private final int buildLevel;
    private final boolean hasDome;
    private final WorkerView worker;

    /**
     * CellView constructor.
     * CellView attributes are set to the values of the same attributes of the Cell Object received as an argument.
     *
     * @param cell the Cell represented by this CellView
     */
    public CellView(Cell cell) {
        this.posX = cell.getPosX();
        this.posY = cell.getPosY();
        this.buildLevel = cell.getBuildLevel();
        this.hasDome = cell.isDomed();
        if (cell.hasWorker()) this.worker = new WorkerView(cell.getWorker());
        else this.worker = null;
    }

    /**
     * CellView constructor.
     * CellView attributes are set to the values received as arguments.
     *
     * @param posX       the x-coordinate of the Cell represented by this CellView
     * @param posY       the y-coordinate of the Cell represented by this CellView
     * @param buildLevel the build level of the Cell represented by this CellView
     * @param hasDome    true if the Cell represented by this CellView has a dome, false otherwise
     * @param worker     the WorkerView of the Worker eventually standing on the Cell represented by this CellView
     */
    public CellView(int posX, int posY, int buildLevel, boolean hasDome, WorkerView worker) {
        this.posX = posX;
        this.posY = posY;
        this.buildLevel = buildLevel;
        this.hasDome = hasDome;
        this.worker = worker;
    }

    /**
     * @return the x-coordinate of the Cell represented by this CellView
     */
    public int getPosX() {
        return posX;
    }

    /**
     * @return the y-coordinate of the Cell represented by this CellView
     */
    public int getPosY() {
        return posY;
    }

    /**
     * @return the build level of the Cell represented by this CellView
     */
    public int getBuildLevel() {
        return buildLevel;
    }

    /**
     * @return true if the Cell represented by this CellView has a dome, false otherwise
     */
    public boolean isDomed() {
        return hasDome;
    }

    /**
     * @return true if the Cell represented by this CellView is occupied by a Worker, false otherwise
     */
    public boolean hasWorker() {
        return worker != null;
    }

    /**
     * @return the WorkerView of the Worker eventually standing on the Cell represented by this CellView
     */
    public WorkerView getWorker() {
        return worker;
    }


}
