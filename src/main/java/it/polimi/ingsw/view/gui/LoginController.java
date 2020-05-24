package it.polimi.ingsw.view.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class LoginController {

    private GUIManager manager;

    @FXML
    private TextField textField;
    @FXML
    private Button button;
    @FXML
    private Label label;

    public void setManager(GUIManager manager) {
        this.manager = manager;
    }

    public void ButtonHandler() {
        manager.putString(textField.getText());
        manager.setBusy(false);
    }

    public void chooseNickname() {
        Platform.runLater(() -> {
            label.setText("Choose nickname:");
            textField.clear();
            textField.setPromptText("Nickname");
            button.setText("Choose");
            manager.setBusy(false);
        });
    }
}
