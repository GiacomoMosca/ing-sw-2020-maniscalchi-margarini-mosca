package it.polimi.ingsw.view;

import it.polimi.ingsw.model.game_board.Cell;

import java.io.Serializable;

public class CellView implements Serializable {

    private final int posX;
    private final int posY;
    private final int buildLevel;
    private final boolean hasDome;
    private final WorkerView worker;

    public CellView(Cell cell) {
        this.posX = cell.getPosX();
        this.posY = cell.getPosY();
        this.buildLevel = cell.getBuildLevel();
        this.hasDome = cell.isDomed();
        if (cell.hasWorker()) this.worker = new WorkerView(cell.getWorker());
        else this.worker = null;
    }

    public CellView(int posX, int posY, int buildLevel, boolean hasDome, WorkerView worker) {
        this.posX = posX;
        this.posY = posY;
        this.buildLevel = buildLevel;
        this.hasDome = hasDome;
        this.worker = worker;
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

    public boolean isDomed() {
        return hasDome;
    }

    public boolean hasWorker() {
        return worker != null;
    }

    public WorkerView getWorker() {
        return worker;
    }


}
