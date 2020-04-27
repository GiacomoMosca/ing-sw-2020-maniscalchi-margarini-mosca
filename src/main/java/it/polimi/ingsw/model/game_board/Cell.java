package it.polimi.ingsw.model.game_board;

import it.polimi.ingsw.model.players.Worker;

public class Cell {

    private final int posX;
    private final int posY;
    private int buildLevel;
    private boolean hasDome;
    private Worker worker;

    /**
     * creates and initializes a new cell
     *
     * @param posY the y-coordinate of the cell
     * @param posX the x-coordinate of the cell
     */
    public Cell(int posY, int posX) {
        this.posX = posX;
        this.posY = posY;
        this.buildLevel = 0;
        this.hasDome = false;
        this.worker = null;
    }

    /**
     * @return the x-coordinate of the cell
     */
    public int getPosX() {
        return posX;
    }

    /**
     * @return the y-coordinate of the cell
     */
    public int getPosY() {
        return posY;
    }

    /**
     * @return the building-level of the cell
     */
    public int getBuildLevel() {
        return buildLevel;
    }

    /**
     * @param buildLevel the building-level to set this cell to
     */
    public void setBuildLevel(int buildLevel) {
        this.buildLevel = buildLevel;
    }

    /**
     * builds a level on the cell, or a dome if the cell has already reached the maximum build level
     *
     * @throws IllegalStateException when it's not possible to build on this cell
     */
    public void build() throws IllegalStateException {
        if (buildLevel > 3 || hasDome) throw new IllegalStateException();
        if (buildLevel == 3) hasDome = true;
        else this.buildLevel++;
    }

    /**
     * @return true if the cell has a Dome, false otherwise
     */
    public boolean isDomed() {
        return hasDome;
    }

    /**
     * builds a Dome on the cell
     */
    public void buildDome() {
        this.hasDome = true;
    }

    /**
     * @return the Worker standing on the cell
     */
    public Worker getWorker() {
        return worker;
    }

    /**
     * @return true if the cell is occupied by a worker, false otherwise
     */
    public boolean hasWorker() {
        return worker != null;
    }

    /**
     * sets the cell as an occupied space (by the worker received as an argument)
     *
     * @param worker
     */
    public void setWorker(Worker worker) {
        this.worker = worker;
    }

}
