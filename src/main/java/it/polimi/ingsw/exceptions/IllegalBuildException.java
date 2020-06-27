package it.polimi.ingsw.exceptions;

/**
 * The IllegalBuildException is caught or thrown when a Player tries to build a level on a domed Cell.
 */
public class IllegalBuildException extends Exception {

    /**
     * IllegalBuildException constructor.
     *
     * @param msg the String describing the x and y-coordinates of the illegal build and the building level of that Cell
     */
    public IllegalBuildException(String msg) {
        super(msg);
    }

}