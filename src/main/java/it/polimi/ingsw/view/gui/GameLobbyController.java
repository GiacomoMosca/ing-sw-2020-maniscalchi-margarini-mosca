package it.polimi.ingsw.view.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class GameLobbyController {

    private GUIManager manager;

    @FXML
    private Button startButton;
    @FXML
    private Button joinButton;

    public void setManager(GUIManager manager) {
        this.manager = manager;
    }

    public void startButtonHandler(){
        manager.putString("1");
        manager.setBusy(false);
    }

    public void joinButtonHandler(){
        manager.putString("2");
        manager.setBusy(false);
    }
}
