package it.polimi.ingsw.view.gui;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class TitleController {
    private GUIManager manager;

    @FXML
    private ImageView playButton, playButton_pressed;
    @FXML
    private Text playText, playText_pressed;

    public void setManager(GUIManager manager) {
        this.manager = manager;
    }

    public void pressed() {
        Platform.runLater(() -> {
            playButton_pressed.setVisible(true);
            playText.setVisible(false);
            playText_pressed.setVisible(true);
        });

    }

    public void released() {
        Platform.runLater(() -> {
            manager.setBusy(false);
            playButton_pressed.setVisible(false);
            playText_pressed.setVisible(false);
            playText.setVisible(true);
        });
    }
}
