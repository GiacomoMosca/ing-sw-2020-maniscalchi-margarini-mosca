package it.polimi.ingsw.view.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.util.concurrent.SynchronousQueue;

public class GameLobbyController {

    private SynchronousQueue<String> messageQueue;

    @FXML
    private Text textBox;

    public void setQueue(SynchronousQueue<String> messageQueue) {
        this.messageQueue = messageQueue;
    }

    public void chooseStartJoin() {
        Platform.runLater(() -> {
            textBox.setText("Choose start/join");
        });
    }

    public void chooseGameName() {
        Platform.runLater(() -> {
            textBox.setText("Choose game name");
        });
    }

    public void choosePlayersNumber() {
        Platform.runLater(() -> {
            textBox.setText("Choose players number");
        });
    }

    public void chooseGameRoom() {
        Platform.runLater(() -> {
            textBox.setText("Choose game room");
        });
    }

}
