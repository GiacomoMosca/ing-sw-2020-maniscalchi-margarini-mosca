package it.polimi.ingsw.view.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class ServiceMessageController {

    private GUIManager manager;

    @FXML
    private Text serviceMessage;

    public void initialize(GUIManager manager) {
        this.manager = manager;
    }

    public void displayMessage(String message) {
        Platform.runLater(() -> {
            serviceMessage.setText(message);
            serviceMessage.setLayoutX(424);
            serviceMessage.setLayoutY(377);
        });
    }
}
