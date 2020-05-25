package it.polimi.ingsw.view.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class ReadyToStartController {

    private GUIManager manager;

    @FXML
    private Text title;
    @FXML
    private ImageView button;
    @FXML
    private ImageView buttonPressed;
    @FXML
    private Text pressedEnter;
    @FXML
    private Text enter;

    public void setManager(GUIManager manager) {
        this.manager = manager;
    }

    public void pressed() {
        Platform.runLater(() -> {
            buttonPressed.setVisible(true);
            enter.setVisible(false);
            pressedEnter.setVisible(true);
        });
    }

    public void released() {
        manager.setBusy(false);
        Platform.runLater(() -> {
            buttonPressed.setVisible(false);
            pressedEnter.setVisible(false);
            enter.setVisible(true);
        });
    }

}
