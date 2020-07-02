package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.view.PlayerView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 * Handles the interaction between client and server during the ending phase of the Game.
 */
public class GameOverController {

    /**
     * The manager for this GUI instance.
     */
    private GUIManager manager;
    /**
     * Set to <code>true</code> when closing the client.
     */
    private boolean shuttingDown = false;

    @FXML
    private Text titleText;
    @FXML
    private StackPane darkBG;
    @FXML
    private ImageView confirmButton, confirmButton_p;
    @FXML
    private Text confirmText, confirmText_p;

    /**
     * @param manager the GUIManager to set the GameLobbyController manager attribute to
     */
    public void initialize(GUIManager manager) {
        this.manager = manager;
    }

    /**
     * Handles the pressing of the Confirm button.
     */
    public void confirmPressed() {
        Platform.runLater(() -> {
            confirmButton_p.setVisible(true);
            confirmText.setVisible(false);
            confirmText_p.setVisible(true);
        });
    }

    /**
     * Handles the releasing of the Confirm button.
     * Notifies the GUIManager that the Confirm button was clicked, putting this input on the messageQueue.
     */
    public void confirmReleased() {
        Platform.runLater(() -> {
            confirmButton_p.setVisible(false);
            confirmText.setVisible(true);
            confirmText_p.setVisible(false);
            manager.setBusy(false);
            if (shuttingDown) manager.putObject(true);
        });
    }

    /**
     * Allows notifying the user that there was an error while trying to join a Game.
     *
     * @param message the reason why the user couldn't join the Game
     */
    public void notifyGameError(String message) {
        Platform.runLater(() -> {
            titleText.setText(message.substring(0, message.length() - 2));
            confirmText.setText("Return to lobby");
            confirmText_p.setText("Return to lobby");
        });
    }

    /**
     * Allows notifying the user that a Player has disconnected from the current Game.
     *
     * @param player the PlayerView representing the Player who disconnected
     */
    public void notifyDisconnection(PlayerView player) {
        Platform.runLater(() -> {
            titleText.setText(player.getId() + " disconnected!");
            confirmText.setText("Continue");
            confirmText_p.setText("Continue");
        });
    }

    /**
     * Allows notifying the user that the current Game is over.
     */
    public void notifyGameOver() {
        Platform.runLater(() -> {
            titleText.setText("Game over!");
            confirmText.setText("Return to lobby");
            confirmText_p.setText("Return to lobby");
        });
    }

    /**
     * Allows notifying the user that the server is down and makes him quit the game.
     */
    public void serverClosed() {
        Platform.runLater(() -> {
            shuttingDown = true;
            titleText.setText("Disconnected from the server. ");
            confirmText.setText("Quit");
            confirmText_p.setText("Quit");
        });
    }

}
