package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.view.PlayerView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class GameOverController {

    private GUIManager manager;
    private boolean shuttingDown = false;

    @FXML
    private Text titleText;
    @FXML
    private StackPane darkBG;
    @FXML
    private ImageView confirmButton, confirmButton_p;
    @FXML
    private Text confirmText, confirmText_p;

    public void initialize(GUIManager manager) {
        this.manager = manager;
    }

    public void confirmPressed() {
        Platform.runLater(() -> {
            confirmButton_p.setVisible(true);
            confirmText.setVisible(false);
            confirmText_p.setVisible(true);
        });
    }

    public void confirmReleased() {
        Platform.runLater(() -> {
            confirmButton_p.setVisible(false);
            confirmText.setVisible(true);
            confirmText_p.setVisible(false);
            manager.setBusy(false);
            if (shuttingDown) manager.putObject(true);
        });
    }

    public void notifyDisconnection(PlayerView player) {
        Platform.runLater(() -> {
            titleText.setText(player.getId() + " disconnected!");
            confirmText.setText("Continue");
            confirmText_p.setText("Continue");
        });
    }

    public void notifyGameOver() {
        Platform.runLater(() -> {
            titleText.setText("Game over!");
            confirmText.setText("Return to lobby");
            confirmText_p.setText("Return to lobby");
        });
    }

    public void serverClosed() {
        Platform.runLater(() -> {
            shuttingDown = true;
            titleText.setText("Disconnected from the server\n(server is down)");
            confirmText.setText("Quit");
            confirmText_p.setText("Quit");
        });
    }

}
