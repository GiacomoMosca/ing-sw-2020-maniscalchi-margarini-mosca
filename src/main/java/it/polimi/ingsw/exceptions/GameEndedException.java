package it.polimi.ingsw.exceptions;

/**
 * Thrown when the Game is ended unexpectedly.
 */
public class GameEndedException extends Exception {

    /**
     * GameEndedException constructor.
     *
     * @param msg the String describing the Exception, ie "game ended"
     */
    public GameEndedException(String msg) {
        super(msg);
    }

}