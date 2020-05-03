package it.polimi.ingsw.model.players;

import it.polimi.ingsw.model.cards.Card;

import java.util.ArrayList;

public class Player {

    private final String id;
    private final String color;
    private Card godCard;
    private ArrayList<Worker> workers;
    private boolean hasLost;

    /**
     * sets all the attributes associated with the player
     *
     * @param id    the string that identifies the player
     * @param color the color associated with the player
     */
    public Player(String id, String color) {
        this.id = id;
        this.color = color;
        this.godCard = null;
        this.workers = new ArrayList<Worker>();
        this.hasLost = false;
    }

    /**
     * @return the ID of the player
     */
    public String getId() {
        return id;
    }

    /**
     * @return the color associated with the player
     */
    public String getColor() {
        return color;
    }

    /**
     * @return the God Card assigned to the player
     */
    public Card getGodCard() {
        return godCard;
    }

    /**
     * sets the God Card received as an argument as the God Card associated with the player
     *
     * @param godCard
     */
    public void setGodCard(Card godCard) {
        this.godCard = godCard;
    }

    /**
     * @return the list of the workers associated with this player
     */
    public ArrayList<Worker> getWorkers() {
        return new ArrayList<Worker>(workers);
    }

    /**
     * adds the worker received as an argument to the list of the workers associated with the player
     *
     * @param worker
     */
    public void addWorker(Worker worker) {
        workers.add(worker);
    }

    /**
     * removes the worker from the list of the workers associated with the player
     *
     * @param worker
     */
    public void removeWorker(Worker worker) {
        worker.getPosition().setWorker(null);
        workers.remove(worker);
    }

    public boolean hasLost() {
        return hasLost;
    }

    public void setLost() {
        hasLost = true;
    }

}
