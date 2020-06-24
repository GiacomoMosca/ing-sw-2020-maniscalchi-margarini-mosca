package it.polimi.ingsw.view.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class LoginController {

    private GUIManager manager;
    private PauseTransition visiblePause;

    @FXML
    private TextField textField;
    @FXML
    private ImageView grayConnectButton;
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
    @FXML
    private Text error;

    public void initialize(GUIManager manager) {
        this.manager = manager;
        visiblePause = new PauseTransition(
                Duration.seconds(2)
        );
        visiblePause.setOnFinished(
                event -> error.setVisible(false)
        );
    }

    public void pressed() {
        Platform.runLater(() -> {
            connectButtonPressed.setVisible(true);
            connectText.setVisible(false);
            connectTextPressed.setVisible(true);
        });
    }

    public void chooseReleased() {
        connectButton.setDisable(true);
        String choice = textField.getText();
        if (!choice.trim().isEmpty()) {
            if(choice.length()>12)
                errorMessage("Invalid input (max 12 characters)");
            else {
                manager.putObject(choice);
                manager.setBusy(false);
            }
        } else
            connectButton.setDisable(false);
        Platform.runLater(() -> {
            connectButtonPressed.setVisible(false);
            connectText.setVisible(true);
            connectTextPressed.setVisible(false);
        });
    }

    public void connectReleased() {
        connectButton.setDisable(true);
        String choice = textField.getText();
        if (!choice.trim().isEmpty()) {
            Platform.runLater(() -> {
                grayConnectButton.setVisible(true);
                connectText.setText("Wait...");
            });
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
            grayConnectButton.setVisible(false);
            connectButton.setOnMouseReleased(e -> chooseReleased());
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

    public void errorMessage(String message){
        textField.clear();
        error.setText(message);
        error.setVisible(true);
        visiblePause.play();
        connectButton.setDisable(false);
    }
}
