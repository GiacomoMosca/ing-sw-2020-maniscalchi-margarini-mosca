package it.polimi.ingsw.model.players;

import it.polimi.ingsw.model.cards.Card;

import java.util.ArrayList;

/**
 * Describes the state of a Player within a specific Game.
 * Stores his ID, his color, the God Card he is associated with, his Workers, the information about his eventual loss.
 */
public class Player {

    /**
     * The Player's nickname.
     */
    private final String id;
    /**
     * The Player's color.
     */
    private final String color;
    /**
     * List of the Player's Workers.
     */
    private final ArrayList<Worker> workers;
    /**
     * The God Card associated with this Player.
     */
    private Card godCard;
    /**
     * Set to <code>true</code> if the Player has Lost.
     */
    private boolean hasLost;

    /**
     * Player constructor. Sets all the attributes associated with the player to their actual values.
     *
     * @param id    the string that identifies the Player
     * @param color the color associated with the Player
     */
    public Player(String id, String color) {
        this.id = id;
        this.color = color;
        this.godCard = null;
        this.workers = new ArrayList<Worker>();
        this.hasLost = false;
    }

    /**
     * @return the ID of the Player
     */
    public String getId() {
        return id;
    }

    /**
     * @return the color associated with the Player
     */
    public String getColor() {
        return color;
    }

    /**
     * @return the God Card assigned to the Player
     */
    public Card getGodCard() {
        return godCard;
    }

    /**
     * Sets the God Card received as an argument as the God Card associated with the Player.
     *
     * @param godCard the God Card to set
     */
    public void setGodCard(Card godCard) {
        this.godCard = godCard;
    }

    /**
     * @return an ArrayList of Workers associated with this Player
     */
    public ArrayList<Worker> getWorkers() {
        return new ArrayList<Worker>(workers);
    }

    /**
     * Adds the worker received as an argument to the list of the workers associated with the Player.
     *
     * @param worker the Worker to add
     */
    public void addWorker(Worker worker) {
        workers.add(worker);
    }

    /**
     * Removes the worker received as an argument from the list of the workers associated with the Player.
     *
     * @param worker the Worker to remove
     */
    public void removeWorker(Worker worker) {
        worker.getPosition().setWorker(null);
        workers.remove(worker);
    }

    /**
     * @return true if the Player has lost, false otherwise
     */
    public boolean hasLost() {
        return hasLost;
    }

    /**
     * Sets to the value true the parameter stating this Player has lost.
     */
    public void setLost() {
        hasLost = true;
    }

}
