package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.turn_controllers.GodController;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.view.PlayerInterface;

import java.util.ArrayList;

public class PlayerController {

    private final Player player;
    private final PlayerInterface client;
    private GodController godController;

    public PlayerController(Player player, PlayerInterface client) {
        this.player = player;
        this.client = client;
        player.setController(this);
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerInterface getClient() {
        return client;
    }

    public GodController getGodController() {
        return godController;
    }

    public void setGodController(GodController godController) {
        this.godController = godController;
        godController.setPlayer(player, client);
    }

    public String playTurn() {
        Worker activeWorker = null;
        ArrayList<Worker> playableWorkers = new ArrayList<Worker>();
        for (Worker worker : player.getWorkers()) {
            if (godController.canPlay(worker)) playableWorkers.add(worker);
        }
        if (playableWorkers.size() == 0) return "LOST";
        if (playableWorkers.size() == 1) {
            activeWorker = playableWorkers.get(0);
        } else {
            activeWorker = client.chooseWorker(playableWorkers);
        }
        return godController.runPhases(activeWorker);
    }

}
