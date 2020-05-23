package it.polimi.ingsw.view.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class GameSetupController {

    private GUIManager manager = new GUIManager();

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private ImageView frame;
    @FXML
    private VBox centerVBox;
    @FXML
    private Text centerText;

    public void setManager(GUIManager manager) {
        this.manager = manager;
    }

    public void displayGameInfo() {
        Platform.runLater(() -> {
            centerText.setText("Picking cards...");
            manager.setBusy(false);
        });
    }

    public void chooseCards() {
        Platform.runLater(() -> {
            centerText.setText("Choose cards");
            manager.setBusy(false);
        });
    }

}
