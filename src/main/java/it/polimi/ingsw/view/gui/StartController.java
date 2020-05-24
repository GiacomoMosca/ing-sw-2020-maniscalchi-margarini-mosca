package it.polimi.ingsw.view.gui;


public class StartController {
    private GUIManager manager;

    public void setManager(GUIManager manager) {
        this.manager = manager;
    }

    public void play(){
        manager.setBusy(false);
    }
}
