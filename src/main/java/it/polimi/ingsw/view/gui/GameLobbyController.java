package it.polimi.ingsw.view.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class GameLobbyController {

    private GUIManager manager;

    @FXML
    private Text textBox;

    public void initialize(GUIManager manager) {
        this.manager = manager;
    }

    public void chooseStartJoin() {
        Platform.runLater(() -> {
            textBox.setText("Choose start/join");
            manager.setBusy(false);
        });
    }

    public void chooseGameName() {
        Platform.runLater(() -> {
            textBox.setText("Choose game name");
            manager.setBusy(false);
        });
    }

    public void choosePlayersNumber() {
        Platform.runLater(() -> {
            textBox.setText("Choose players number");
            manager.setBusy(false);
        });
    }

    public void chooseGameRoom() {
        Platform.runLater(() -> {
            textBox.setText("Choose game room");
            manager.setBusy(false);
        });
    }

}
