package it.polimi.ingsw.model.Players;

import it.polimi.ingsw.model.GameBoard.Cell;
import it.polimi.ingsw.model.Players.Player;

public class Worker {

    private final Player owner;
    private Cell position;

    public Worker(Player owner) {
        this.owner = owner;
        this.position = null;
    }

    public Player getOwner() {
        return owner;
    }

    public Cell getPosition() {
        return position;
    }

    public void move(Cell position) {
        this.position = position;
    }

}
