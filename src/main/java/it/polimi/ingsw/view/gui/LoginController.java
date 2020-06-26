package it.polimi.ingsw.view.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

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
    private ImageView connectButton_p;
    @FXML
    private Text titleText;
    @FXML
    private Text connectText;
    @FXML
    private Text connectText_p;
    @FXML
    private Text error;

    public void initialize(GUIManager manager) {
        this.manager = manager;
        /* visiblePause = new PauseTransition(
                Duration.seconds(2)
        );
        visiblePause.setOnFinished(
                event -> error.setVisible(false)
        ); */
    }

    public void pressed() {
        Platform.runLater(() -> {
            connectButton_p.setVisible(true);
            connectText.setVisible(false);
            connectText_p.setVisible(true);
        });
    }

    public void chooseReleased() {
        connectButton.setDisable(true);
        String choice = textField.getText();
        if (!choice.trim().isEmpty()) {
            if(choice.length()>12)
                errorMessage("Invalid input (max 12 characters).");
            else {
                manager.putObject(choice);
                manager.setBusy(false);
            }
        } else
            connectButton.setDisable(false);
        Platform.runLater(() -> {
            connectButton_p.setVisible(false);
            connectText.setVisible(true);
            connectText_p.setVisible(false);
        });
    }

    public void connectReleased() {
        connectButton.setDisable(true);
        String choice = textField.getText();
        if (!choice.trim().isEmpty()) {
            Platform.runLater(() -> {
                grayConnectButton.setVisible(true);
                connectText.setText("Connecting");
            });
            manager.putObject(choice);
            manager.setBusy(false);
        }
        else
            connectButton.setDisable(false);
        Platform.runLater(() -> {
            connectButton_p.setVisible(false);
            connectText.setVisible(true);
            connectText_p.setVisible(false);
        });
    }

    public void chooseNickname() {
        Platform.runLater(() -> {
            grayConnectButton.setVisible(false);
            connectButton.setOnMouseReleased(e -> chooseReleased());
            titleText.setText("Choose nickname:");
            textField.clear();
            textField.setPromptText("Nickname");
            connectText.setText("Choose");
            connectText_p.setText("Choose");
            connectButton.setDisable(false);
            manager.setBusy(false);
        });
    }

    public void errorMessage(String message){
        textField.clear();
        error.setText(message);
        error.setVisible(true);
        // visiblePause.play();
        connectButton.setDisable(false);
    }
}
