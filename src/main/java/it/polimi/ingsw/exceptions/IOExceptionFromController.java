package it.polimi.ingsw.exceptions;

import it.polimi.ingsw.controller.PlayerController;

public class IOExceptionFromController extends Exception {

    PlayerController controller;

    public IOExceptionFromController(Exception e, PlayerController controller) {
        super(e.getMessage());
        this.controller = controller;
    }

    public PlayerController getController() {
        return controller;
    }
}
