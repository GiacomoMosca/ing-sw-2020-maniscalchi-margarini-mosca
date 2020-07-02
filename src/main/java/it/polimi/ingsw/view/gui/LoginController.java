package it.polimi.ingsw.view.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

/**
 * Handles the interaction between client and server during the login phase of a Player.
 */
public class LoginController {

    /**
     * The manager for this GUI instance.
     */
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

    /**
     * @param manager the GUIManager to set the JoinGameController manager attribute to
     */
    public void initialize(GUIManager manager) {
        this.manager = manager;
    }

    /**
     * Handles the pressing of the Connect button.
     */
    public void pressed() {
        Platform.runLater(() -> {
            textField.editableProperty().setValue(false);
            connectButton_p.setVisible(true);
            connectText.setVisible(false);
            connectText_p.setVisible(true);
        });
    }

    /**
     * Handles the releasing of the Connect button.
     * Notices the GUIManager that the Connect button was clicked, putting the input inserted in the TextBox (Server IP address) on the messageQueue.
     */
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
        } else {
            Platform.runLater(() -> textField.editableProperty().setValue(true));
            connectButton.setDisable(false);
        }
    }

    /**
     * Handles the releasing of the Choose button.
     * When the Player inserts a valid nickname in the TextBox, notices the GUIManager that the Choose button was clicked, putting the input on the messageQueue.
     * Until the inserted nickname is not valid, an error message appears and it's not possible to continue.
     */
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
        } else {
            Platform.runLater(() -> textField.editableProperty().setValue(true));
            connectButton.setDisable(false);
        }
    }

    /**
     * Allows the Player to choose his nickname.
     * Sets an empty textBox as visible, and allows clicking on a Connect button when a valid nickname is inserted.
     */
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

    /**
     * Allows displaying an error message:
     * <ul>
     *     <li>when the inserted server IP address is not valid;
     *     <li>when the inserted nickname is not valid.
     * </ul>
     *
     * @param message the String describing the error message
     */
    public void errorMessage(String message) {
        Platform.runLater(() -> {
            textField.clear();
            textField.editableProperty().setValue(true);
            error.setText(message);
            error.setVisible(true);
            connectButton.setDisable(false);
        });
    }
}
