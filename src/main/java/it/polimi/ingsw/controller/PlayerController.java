package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.turn_controllers.GodController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.view.VirtualView;

import java.io.IOException;
import java.util.ArrayList;


public class PlayerController {

    private final Player player;
    private final VirtualView client;
    private final GameController game;
    private GodController godController;

    /**
     * PlayerController constructor.
     * Associates the Player and his VirtualView to their PlayerController.
     *
     * @param player the Player associated with this PlayerController
     * @param client the VirtualView associated with this PlayerController
     * @param game   the GameController for this Game
     */
    public PlayerController(Player player, VirtualView client, GameController game) {
        this.player = player;
        this.client = client;
        this.game = game;
    }

    /**
     * @return the Player associated with this PlayerController
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the VirtualView associated with this PlayerController
     */
    public VirtualView getClient() {
        return client;
    }

    /**
     * @return the GameController associated with this Game
     */
    public GameController getGame() {
        return game;
    }

    /**
     * @return the GodController associated with this PlayerController
     */
    public GodController getGodController() {
        return godController;
    }

    /**
     * Associates the PlayerController to the correct GodController, ie to the God Power Card associated to the player.
     *
     * @param godController the GodController to associate this PlayerController to
     */
    public void setGodController(GodController godController) {
        this.godController = godController;
        godController.setPlayer(player, client);
    }

    /**
     * Handles the preparation of the turn. Checks if there are any workers available for the active Player: if no Worker is available to move, returns "outOfMoves";
     * if only one Worker is available to move, sets it as the active Worker; if two Workers are available, lets the player choose which one to move.
     * In the first case, the active Player will be eliminated. In the second and third cases the player will play his turn.
     *
     * @return "outOfMoves" if all the active Player's Workers can't move, "godConditionAchieved" if the Player won, "next" if the game goes on
     * @throws IOExceptionFromController when an IOException from a specific PlayerController occurs
     */
    public String playTurn() throws IOExceptionFromController {
        Worker activeWorker;
        ArrayList<Worker> playableWorkers = new ArrayList<Worker>();
        for (Worker worker : player.getWorkers()) {
            if (godController.canPlay(worker)) playableWorkers.add(worker);
        }
        try {
            if (playableWorkers.size() == 0) return "outOfMoves";
            else activeWorker = client.chooseWorker(playableWorkers);
            return godController.runPhases(activeWorker);
        } catch (IOException | InterruptedException e) {
            throw new IOExceptionFromController(e, this);
        }
    }
}
