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
     * creates a PlayerController associating the player and his VirtualView
     *
     * @param player
     * @param client
     */
    public PlayerController(Player player, VirtualView client, GameController game) {
        this.player = player;
        this.client = client;
        this.game = game;
    }

    /**
     * @return the player associated with this PlayerController
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
     * sets the attribute godController of the player to the GodController passed as an argument
     *
     * @param godController the GodController to associate this PlayerController to
     */
    public void setGodController(GodController godController) {
        this.godController = godController;
        godController.setPlayer(player, client);
    }

    /**
     * handles the turn.
     * checks if there are any workers available: if no worker is available to move, returns "LOST";
     * if one worker is available sets it as the activeWorker,
     * if two workers are available lets the player choose which one to move.
     *
     * @return LOST if the player lost, WIN if the player won, NEXT if the game goes on
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
        } catch (IOException e) {
            throw new IOExceptionFromController(e, this);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return "next";
        }
    }

}
