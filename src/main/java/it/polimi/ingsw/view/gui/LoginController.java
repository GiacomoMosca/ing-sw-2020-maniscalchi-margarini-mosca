package it.polimi.ingsw.view.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.util.concurrent.SynchronousQueue;

public class LoginController {

    private SynchronousQueue<String> messageQueue;

    @FXML
    private Text textBox;

    public void setQueue(SynchronousQueue<String> messageQueue) {
        this.messageQueue = messageQueue;
    }

    public void getServerIp() {
        Platform.runLater(() -> {
            textBox.setText("Get server IP");
        });
    }

    public void chooseNickname() {
        Platform.runLater(() -> {
            textBox.setText("Choose nickname");
        });
    }

}
