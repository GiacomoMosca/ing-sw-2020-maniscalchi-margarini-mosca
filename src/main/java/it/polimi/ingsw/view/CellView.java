package it.polimi.ingsw.view;

import it.polimi.ingsw.model.game_board.Cell;

public class CellView {

    private final int posX;
    private final int posY;
    private final int buildLevel;
    private final boolean hasDome;
    private final String workerColor;

    public CellView (Cell cell) {
        this.posX = cell.getPosX();
        this.posY = cell.getPosY();
        this.buildLevel = cell.getBuildLevel();
        this.hasDome = cell.isDomed();
        if (cell.hasWorker()) this.workerColor = cell.getWorker().getOwner().getColor();
        else this.workerColor = null;
    }

    public CellView (int posX, int posY, int buildLevel, boolean hasDome, String workerColor) {
        this.posX = posX;
        this.posY = posY;
        this.buildLevel = buildLevel;
        this.hasDome = hasDome;
        this.workerColor = workerColor;
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

    public boolean isHasDome() {
        return hasDome;
    }

    public String getWorkerColor() {
        return workerColor;
    }

}
