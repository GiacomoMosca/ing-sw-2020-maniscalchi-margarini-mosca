package it.polimi.ingsw.model.players;

import it.polimi.ingsw.model.cards.Card;

import java.util.ArrayList;

/**
 * do we need to write a JavaDoc comment for every single class?
 */
public class Player {

    private final String id;
    private final String color;
    private Card godCard;
    private ArrayList<Worker> workers;
    private boolean hasLost;

    /**
     * Sets all the attributes associated with the player to their actual values.
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
     * Sets the God Card received as an argument as the God Card associated with the player.
     *
     * @param godCard
     */
    public void setGodCard(Card godCard) {
        this.godCard = godCard;
    }

    /**
     * @return an ArrayList of Worker objects associated with this player
     */
    public ArrayList<Worker> getWorkers() {
        return new ArrayList<Worker>(workers);
    }

    /**
     * Adds the worker received as an argument to the list of the workers associated with the player.
     *
     * @param worker the worker to add
     */
    public void addWorker(Worker worker) {
        workers.add(worker);
    }

    /**
     * Removes the worker received as an argument from the list of the workers associated with the player.
     *
     * @param worker the worker to remove
     */
    public void removeWorker(Worker worker) {
        worker.getPosition().setWorker(null);
        workers.remove(worker);
    }

    /**
     *
     * @return true if the player has lost, false otherwise
     */
    public boolean hasLost() {
        return hasLost;
    }

    /**
     * Sets to the value true the parameter stating this player has lost.
     */
    public void setLost() {
        hasLost = true;
    }

}
