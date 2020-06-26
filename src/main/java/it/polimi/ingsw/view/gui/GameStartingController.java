package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.view.GameView;
import it.polimi.ingsw.view.PlayerView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class GameStartingController {

    private GUIManager manager;

    @FXML
    private Text nicknameTab, titleText;
    @FXML
    private ImageView waitingPlayers2, waitingPlayers3;
    @FXML
    private StackPane player1, player2, player3;
    @FXML
    private Text player1Name, player2Name, player3Name;
    @FXML
    private ImageView confirmButton, confirmButton_p;
    @FXML
    private Text confirmText, confirmText_p;

    public void initialize(GUIManager manager) {
        this.manager = manager;
        nicknameTab.setText(manager.getId());
    }

    public void confirmPressed() {
        Platform.runLater(() -> {
            confirmButton_p.setVisible(true);
            confirmText_p.setVisible(true);
            confirmText.setVisible(false);
        });
    }

    public void confirmReleased() {
        Platform.runLater(() -> {
            confirmButton_p.setVisible(false);
            confirmText_p.setVisible(false);
            confirmText.setVisible(true);
            manager.putObject(true);
            manager.setBusy(false);
        });
    }

    public void displayPlayerJoined(GameView game) {
        String gameName = game.getName();
        int playerNum = game.getPlayerNum();
        ArrayList<String> playerNames = new ArrayList<>();
        for (PlayerView playerView : game.getPlayers()) {
            playerNames.add(playerView.getId());
        }
        Platform.runLater(() -> {
            titleText.setText(gameName);
            if (playerNum == 2) waitingPlayers2.setVisible(true);
            else if (playerNum == 3) waitingPlayers3.setVisible(true);
            if (playerNames.size() >= 3) {
                player3.setVisible(true);
                player3Name.setText(playerNames.get(2));
            }
            if (playerNames.size() >= 2) {
                player2.setVisible(true);
                player2Name.setText(playerNames.get(1));
            }
            if (playerNames.size() >= 1) {
                player1.setVisible(true);
                player1Name.setText(playerNames.get(0));
            }
            manager.setBusy(false);
        });
    }

    public void notifyGameStarting() {
        Platform.runLater(() -> {
            confirmButton.setVisible(true);
            confirmText.setText("Start Game");
        });
    }

}
