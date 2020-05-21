package it.polimi.ingsw.view.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class LoginController {

    private GUIManager manager;

    @FXML
    private Text textBox;

    public void setManager(GUIManager manager) {
        this.manager = manager;
    }

    public void getServerIp() {
        Platform.runLater(() -> {
            textBox.setText("Get server IP");
            manager.setBusy(false);
        });
    }

    public void chooseNickname() {
        Platform.runLater(() -> {
            textBox.setText("Choose nickname");
            manager.setBusy(false);
        });
    }

}
