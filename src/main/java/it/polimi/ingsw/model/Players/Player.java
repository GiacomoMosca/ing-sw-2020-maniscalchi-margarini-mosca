package it.polimi.ingsw.model.Players;

import it.polimi.ingsw.model.Cards.Card;

import java.util.ArrayList;

public class Player {

    private final String id;
    private final String color;
    private Card godCard;
    private final ArrayList<Worker> workers;

    public Player(String id, String color) {
        this.id = id;
        this.color = color;
        this.godCard = null;
        this.workers = new ArrayList<Worker>();
    }

    public String getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public Card getGodCard() {
        return godCard;
    }

    public void setGodCard(Card godCard) {
        this.godCard = godCard;
    }

    public ArrayList<Worker> getWorkers() {
        return new ArrayList<Worker>(workers);
    }

    public void addWorker(Worker worker) {
        workers.add(worker);
    }

    public void removeWorker(Worker worker) {
        workers.remove(worker);
    }

}
