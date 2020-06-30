package it.polimi.ingsw.exceptions;

/**
 * Thrown when a Player tries to move his Worker to a domed or occupied Cell.
 */
public class IllegalMoveException extends Exception {

    /**
     * IllegalMoveException constructor.
     *
     * @param msg the String describing the x and y-coordinates of the illegal move and the reason why this move is not possible
     */
    public IllegalMoveException(String msg) {
        super(msg);
    }

}