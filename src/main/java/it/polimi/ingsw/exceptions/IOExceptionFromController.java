package it.polimi.ingsw.exceptions;

import it.polimi.ingsw.controller.PlayerController;

/**
 * The IOExceptionFromController is an IOException with a reference to the PlayerController it was generated from.
 */
public class IOExceptionFromController extends Exception {

    PlayerController controller;

    /**
     * IOExceptionFromController constructor.
     *
     * @param e          the exception
     * @param controller the PlayerController this IOExceptionFromController was generated from
     */
    public IOExceptionFromController(Exception e, PlayerController controller) {
        super(e.getMessage());
        this.controller = controller;
    }

    /**
     * @return the PlayerController this IOExceptionFromController was generated from
     */
    public PlayerController getController() {
        return controller;
    }
}
