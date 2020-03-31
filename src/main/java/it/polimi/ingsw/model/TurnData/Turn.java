package it.polimi.ingsw.model.TurnData;

import it.polimi.ingsw.model.Players.Player;
import it.polimi.ingsw.model.Players.Worker;

public class Turn {

    private final Player activePlayer;
    /**
     * identifies the phase of the turn, such as moving or building
     */
    private int phase;
    private Worker activeWorker;

    /**
     * prepares a new turn for the player received as an argument
     *
     * @param activePlayer
     */
    public Turn(Player activePlayer) {
        this.activePlayer = activePlayer;
        this.phase = 1;
        this.activeWorker = null;
    }

    /**
     *
     * @return the player associated with this turn
     */
    public Player getActivePlayer() {
        return activePlayer;
    }

    /**
     *
     * @return the current phase of the turn
     */
    public int getPhase() {
        return phase;
    }

    /**
     * sets the phase received as an argument as the phase of the turn
     *
     * @param phase
     */
    public void setPhase(int phase) {
        this.phase = phase;
    }

    /**
     *
     * @return the worker associated with this turn
     */
    public Worker getActiveWorker() {
        return activeWorker;
    }

    /**
     * sets the worker associated with this turn
     *
     * @param activeWorker the active worker
     */
    public void setActiveWorker(Worker activeWorker) {
        this.activeWorker = activeWorker;
    }

    /**
     * Indicates whether some other turn is "equal to" this one.
     *
     * @param obj the reference object with which to compare
     * @return true if this turn is the same as the obj argument, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        Turn t2= (Turn) obj;
        return(this.getActivePlayer()==t2.getActivePlayer()&&this.getPhase()==t2.getPhase()&&this.getActiveWorker()==t2.getActiveWorker());
    }


}
