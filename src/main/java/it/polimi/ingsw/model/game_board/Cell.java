package it.polimi.ingsw.model.game_board;

import it.polimi.ingsw.exceptions.IllegalBuildException;
import it.polimi.ingsw.model.players.Worker;

/**
 * The Cell class keeps information about each one of the 25 Cells composing the Board, such as the x and y-coordinates, the building level, if the cell has a dome, the worker eventually standing on the Cell.
 * It also allows interaction with the Cell with getter and setter methods.
 */
public class Cell {

    private final int posX;
    private final int posY;
    private int buildLevel;
    private boolean hasDome;
    private Worker worker;

    /**
     * Cell constructor. Initializes a Cell, setting its x and y coordinates as the values received as arguments and all the other attributes to thei default values.
     *
     * @param posY the y-coordinate of the Cell
     * @param posX the x-coordinate of the Cell
     */
    public Cell(int posY, int posX) {
        this.posX = posX;
        this.posY = posY;
        this.buildLevel = 0;
        this.hasDome = false;
        this.worker = null;
    }

    /**
     * @return the x-coordinate of the Cell
     */
    public int getPosX() {
        return posX;
    }

    /**
     * @return the y-coordinate of the Cell
     */
    public int getPosY() {
        return posY;
    }

    /**
     * @return the building-level of the Cell
     */
    public int getBuildLevel() {
        return buildLevel;
    }

    /**
     * @param buildLevel the building-level to set this Cell to
     */
    public void setBuildLevel(int buildLevel) {
        this.buildLevel = buildLevel;
    }

    /**
     * Builds a level (in a range from 1 to 3) or a dome on the Cell.
     *
     * @throws IllegalStateException when it's not possible to build on this Cell
     */
    public void build() throws IllegalBuildException {
        if (hasDome)
            throw new IllegalBuildException("cell [" + posX + "," + posY + "] is at build level " + buildLevel + " and already has a dome");
        if (buildLevel == 3) hasDome = true;
        else this.buildLevel++;
    }

    /**
     * @return true if the Cell has a Dome, false otherwise
     */
    public boolean isDomed() {
        return hasDome;
    }

    /**
     * Builds a Dome on the Cell.
     */
    public void buildDome() {
        this.hasDome = true;
    }

    /**
     * @return the Worker standing on the Cell
     */
    public Worker getWorker() {
        return worker;
    }

    /**
     * Sets the Cell as an occupied space (by the worker received as an argument).
     *
     * @param worker the Worker to put in the Cell
     */
    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    /**
     * @return true if the Cell is occupied by a Worker, false otherwise.
     */
    public boolean hasWorker() {
        return worker != null;
    }

}
