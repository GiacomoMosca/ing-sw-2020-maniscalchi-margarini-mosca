package it.polimi.ingsw.view.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.util.concurrent.SynchronousQueue;

public class GameBoardController {

    private SynchronousQueue<String> messageQueue;

    @FXML
    private Text textBox;

    public void setQueue(SynchronousQueue<String> messageQueue) {
        this.messageQueue = messageQueue;
    }

    public void displayGameInfo() {
        Platform.runLater(() -> {
            textBox.setText("Display game info");
        });
    }

    public void choosePosition() {
        Platform.runLater(() -> {
            textBox.setText("Choose position");
        });
    }

    public void displayBuild() {
        Platform.runLater(() -> {
            textBox.setText("Display build");
        });
    }

    public void displayMove() {
        Platform.runLater(() -> {
            textBox.setText("Display move");
        });
    }

    public void displayPlaceWorker() {
        Platform.runLater(() -> {
            textBox.setText("Display place worker");
        });
    }

    public void notifyDisconnection() {
        Platform.runLater(() -> {
            textBox.setText("Disconnected!");
        });
    }

    public void notifyGameOver() {
        Platform.runLater(() -> {
            textBox.setText("Game over!");
        });
    }

    public void notifyLoss() {
        Platform.runLater(() -> {
            textBox.setText("You lose!");
        });
    }

    public void notifyWin() {
        Platform.runLater(() -> {
            textBox.setText("You win!");
        });
    }

}
