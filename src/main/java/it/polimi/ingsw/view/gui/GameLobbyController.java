package it.polimi.ingsw.view.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

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

    public void initialize(GUIManager manager) {
        this.manager = manager;
        nicknameTab.setText(manager.getId());
    }

    public void joinPressed() {
        Platform.runLater(() -> {
            joinGameButton_p.setVisible(true);
            joinGameText.setVisible(false);
            joinGameText_p.setVisible(true);
        });
    }

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

    public void newGamePressed() {
        Platform.runLater(() -> {
            newGameButton_p.setVisible(true);
            newGameText.setVisible(false);
            newGameText_p.setVisible(true);
        });
    }

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
