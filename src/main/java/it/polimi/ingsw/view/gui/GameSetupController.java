package it.polimi.ingsw.view.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class GameSetupController {

    private GUIManager manager;

    @FXML
    private Text textBox;

    public void setManager(GUIManager manager) {
        this.manager = manager;
    }

    public void chooseCards() {
        Platform.runLater(() -> {
            textBox.setText("Choose cards");
            manager.setBusy(false);
        });
    }

}
