package it.polimi.ingsw.model.TurnData;

import it.polimi.ingsw.model.Players.Player;
import it.polimi.ingsw.model.Players.Worker;

public class Turn {

    private final Player activePlayer;
    private int phase;
    private Worker activeWorker;

    public Turn(Player activePlayer) {
        this.activePlayer = activePlayer;
        this.phase = 1;
        this.activeWorker = null;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public Worker getActiveWorker() {
        return activeWorker;
    }

    public void setActiveWorker(Worker activeWorker) {
        this.activeWorker = activeWorker;
    }
}
