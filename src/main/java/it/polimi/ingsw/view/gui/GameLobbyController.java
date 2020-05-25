package it.polimi.ingsw.view.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class GameLobbyController {

    private GUIManager manager;

    @FXML
    private ImageView newGameButtonPressed;
    @FXML
    private ImageView joinButtonPressed;
    @FXML
    private Text joinGame;
    @FXML
    private Text joinGamePressed;
    @FXML
    private Text newGame;
    @FXML
    private Text newGamePressed;

    public void initialize(GUIManager manager) {
        this.manager = manager;
    }

    public void joinPressed() {
        Platform.runLater(() -> {
            joinButtonPressed.setVisible(true);
            joinGame.setVisible(false);
            joinGamePressed.setVisible(true);
        });
    }

    public void joinReleased() {
        Platform.runLater(() -> {
            joinGame.setDisable(true);
            manager.putObject(2);
            manager.setBusy(false);
            joinButtonPressed.setVisible(false);
            joinGamePressed.setVisible(false);
            joinGame.setVisible(true);
        });
    }

    public void newGamePressed() {
        Platform.runLater(() -> {
            newGameButtonPressed.setVisible(true);
            newGame.setVisible(false);
            newGamePressed.setVisible(true);
        });
    }

    public void newGameReleased() {
        Platform.runLater(() -> {
            newGame.setDisable(true);
            manager.putObject(1);
            manager.setBusy(false);
            newGameButtonPressed.setVisible(false);
            newGamePressed.setVisible(false);
            newGame.setVisible(true);
        });
    }
}
