package it.polimi.ingsw.view.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class LoginController {

    private GUIManager manager;

    @FXML
    private Text titleText;
    @FXML
    private TextField textField;
    @FXML
    private ImageView grayConnectButton;
    @FXML
    private ImageView connectButton, connectButton_p;
    @FXML
    private Text connectText, connectText_p;
    @FXML
    private Text error;

    public void initialize(GUIManager manager) {
        this.manager = manager;
    }

    public void pressed() {
        Platform.runLater(() -> {
            textField.editableProperty().setValue(false);
            connectButton_p.setVisible(true);
            connectText.setVisible(false);
            connectText_p.setVisible(true);
        });
    }

    public void connectReleased() {
        Platform.runLater(() -> {
            connectButton_p.setVisible(false);
            connectText.setVisible(true);
            connectText_p.setVisible(false);
        });
        connectButton.setDisable(true);
        String choice = textField.getText();
        if (!choice.trim().isEmpty()) {
            Platform.runLater(() -> {
                error.setVisible(false);
                grayConnectButton.setVisible(true);
                connectText.setText("Connecting");
                manager.putObject(choice);
                manager.setBusy(false);
            });
        } else
            connectButton.setDisable(false);
    }

    public void chooseReleased() {
        Platform.runLater(() -> {
            connectButton_p.setVisible(false);
            connectText.setVisible(true);
            connectText_p.setVisible(false);
        });
        connectButton.setDisable(true);
        String choice = textField.getText();
        if (!choice.trim().isEmpty()) {
            if (choice.length() > 12) {
                errorMessage("Invalid input (max 12 characters).");
            } else {
                Platform.runLater(() -> {
                    error.setVisible(false);
                    manager.putObject(choice);
                    manager.setBusy(false);
                });
            }
        } else
            connectButton.setDisable(false);
    }

    public void chooseNickname() {
        Platform.runLater(() -> {
            grayConnectButton.setVisible(false);
            connectButton.setOnMouseReleased(e -> chooseReleased());
            titleText.setText("Choose nickname:");
            textField.clear();
            textField.setPromptText("Nickname");
            textField.editableProperty().setValue(true);
            connectText.setText("Choose");
            connectText_p.setText("Choose");
            connectButton.setDisable(false);
            manager.setBusy(false);
        });
    }

    public void errorMessage(String message) {
        textField.clear();
        textField.editableProperty().setValue(true);
        error.setText(message);
        error.setVisible(true);
        connectButton.setDisable(false);
    }
}
