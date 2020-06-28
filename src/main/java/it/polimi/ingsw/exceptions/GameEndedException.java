package it.polimi.ingsw.exceptions;

/**
 * The GameEndedException is thrown when the Game is unexpectedly ended.
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