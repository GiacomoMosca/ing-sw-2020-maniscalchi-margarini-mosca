package it.polimi.ingsw.view.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class LoginController {

    private GUIManager manager;

    @FXML
    private TextField textField;
    @FXML
    private ImageView connectButton;
    @FXML
    private ImageView connectButtonPressed;
    @FXML
    private Text request;
    @FXML
    private Text connectText;
    @FXML
    private Text connectTextPressed;

    public void initialize(GUIManager manager) {
        this.manager = manager;
    }

    public void pressed() {
        Platform.runLater(() -> {
            connectButtonPressed.setVisible(true);
            connectText.setVisible(false);
            connectTextPressed.setVisible(true);
        });
    }

    public void released(){
        connectButton.setDisable(true);
        String choice = textField.getText();
        if(!choice.trim().isEmpty()) {
            manager.putObject(choice);
            manager.setBusy(false);
        }
        else
            connectButton.setDisable(false);
        Platform.runLater(() -> {
            connectButtonPressed.setVisible(false);
            connectText.setVisible(true);
            connectTextPressed.setVisible(false);
        });
    }

    public void chooseNickname() {
        Platform.runLater(() -> {
            request.setText("Choose nickname:");
            request.setLayoutX(341);
            textField.clear();
            textField.setPromptText("Nickname");
            connectText.setText("Choose");
            connectTextPressed.setText("Choose");
            connectButton.setDisable(false);
            manager.setBusy(false);
        });
    }
}
