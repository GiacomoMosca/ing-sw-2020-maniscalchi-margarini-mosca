package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.turn_controllers.GodController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.view.VirtualView;

public class FakePlayerController extends PlayerController{

    public FakePlayerController(Player player, VirtualView client, GameController game) {
        super(player, client, game);
    }

    public Player getPlayer() {
        return super.getPlayer();
    }

    public VirtualView getClient() {
        return super.getClient();
    }

    public GameController getGame() {
        return super.getGame();
    }

    /**
     * @return the GodController associated with this PlayerController
     */
    public GodController getGodController() {
        return super.getGodController();
    }

    public void setGodController(GodController godController) {
        super.setGodController(godController);
    }

    public String playTurn() throws IOExceptionFromController {
        return "winConditionAchieved";
    }
}
