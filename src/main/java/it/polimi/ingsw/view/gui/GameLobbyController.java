package it.polimi.ingsw.view.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

/**
 * Handles the interaction between client and server during the choice of starting a new Game or joining an existing one.
 */
public class GameLobbyController {

    private GUIManager manager;

    @FXML
    private ImageView newGameButton, newGameButton_p;
    @FXML
    private ImageView joinGameButton, joinGameButton_p;
    @FXML
    private Text nicknameTab;
    @FXML
    private Text joinGameText, joinGameText_p;
    @FXML
    private Text newGameText, newGameText_p;

    /**
     * @param manager the GUIManager to set the GameLobbyController manager attribute to
     */
    public void initialize(GUIManager manager) {
        this.manager = manager;
        nicknameTab.setText(manager.getId());
    }

    /**
     * Handles the pressing of the JoinGame button.
     */
    public void joinPressed() {
        Platform.runLater(() -> {
            joinGameButton_p.setVisible(true);
            joinGameText.setVisible(false);
            joinGameText_p.setVisible(true);
        });
    }

    /**
     * Handles the releasing of the JoinGame button.
     * Notices the GUIManager that the JoinGame button was clicked, putting this input on the messageQueue.
     */
    public void joinReleased() {
        Platform.runLater(() -> {
            joinGameText.setDisable(true);
            manager.putObject(2);
            manager.setBusy(false);
            joinGameButton_p.setVisible(false);
            joinGameText_p.setVisible(false);
            joinGameText.setVisible(true);
        });
    }

    /**
     * Handles the pressing of the NewGame button.
     */
    public void newGamePressed() {
        Platform.runLater(() -> {
            newGameButton_p.setVisible(true);
            newGameText.setVisible(false);
            newGameText_p.setVisible(true);
        });
    }

    /**
     * Handles the releasing of the NewGame button.
     * Notices the GUIManager that the NewGame button was clicked, putting this input on the messageQueue.
     */
    public void newGameReleased() {
        Platform.runLater(() -> {
            newGameText.setDisable(true);
            manager.putObject(1);
            manager.setBusy(false);
            newGameButton_p.setVisible(false);
            newGameText_p.setVisible(false);
            newGameText.setVisible(true);
        });
    }
}
