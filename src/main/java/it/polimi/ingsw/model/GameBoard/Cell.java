package it.polimi.ingsw.model.GameBoard;

import it.polimi.ingsw.model.Players.Worker;

public class Cell {

    private final int posX;
    private final int posY;
    private int buildLevel;
    private boolean hasDome;
    private Worker worker;

    public Cell(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        this.buildLevel = 0;
        this.hasDome = false;
        this.worker = null;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getBuildLevel() {
        return buildLevel;
    }

    public void build() throws IllegalStateException {
        if (buildLevel >= 3) throw new IllegalStateException();
        this.buildLevel++;
    }

    public boolean isDomed() {
        return hasDome;
    }

    public void buildDome() {
        this.hasDome = true;
    }

    public Worker getWorker() {
        return worker;
    }

    public boolean hasWorker() {
        return worker != null;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

}
