package it.polimi.ingsw.view.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.util.concurrent.SynchronousQueue;

public class GameSetupController {

    private SynchronousQueue<String> messageQueue;

    @FXML
    private Text textBox;

    public void setQueue(SynchronousQueue<String> messageQueue) {
        this.messageQueue = messageQueue;
    }

    public void chooseCards() {
        Platform.runLater(() -> {
            textBox.setText("Choose cards");
        });
    }

    public void chooseStartingPlayer() {
        Platform.runLater(() -> {
            textBox.setText("Choose starting player");
        });
    }

}
