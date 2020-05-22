package it.polimi.ingsw.view.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class GameBoardController {

    private GUIManager manager;

    @FXML
    private Text textBox;

    public void setManager(GUIManager manager) {
        this.manager = manager;
    }

    public void displayGameInfo() {
        Platform.runLater(() -> {
            textBox.setText("Display game info");
            manager.setBusy(false);
        });
    }

    public void chooseStartingPlayer() {
        Platform.runLater(() -> {
            textBox.setText("Choose starting player");
            manager.setBusy(false);
        });
    }

    public void choosePosition() {
        Platform.runLater(() -> {
            textBox.setText("Choose position");
            manager.setBusy(false);
        });
    }

    public void displayBuild() {
        Platform.runLater(() -> {
            textBox.setText("Display build");
            manager.setBusy(false);
        });
    }

    public void displayMove() {
        Platform.runLater(() -> {
            textBox.setText("Display move");
            manager.setBusy(false);
        });
    }

    public void displayPlaceWorker() {
        Platform.runLater(() -> {
            textBox.setText("Display place worker");
            manager.setBusy(false);
        });
    }

    public void notifyDisconnection() {
        Platform.runLater(() -> {
            textBox.setText("Disconnected!");
            manager.setBusy(false);
        });
    }

    public void notifyGameOver() {
        Platform.runLater(() -> {
            textBox.setText("Game over!");
            manager.setBusy(false);
        });
    }

    public void notifyLoss() {
        Platform.runLater(() -> {
            textBox.setText("You lose!");
            manager.setBusy(false);
        });
    }

    public void notifyWin() {
        Platform.runLater(() -> {
            textBox.setText("You win!");
            manager.setBusy(false);
        });
    }

}
