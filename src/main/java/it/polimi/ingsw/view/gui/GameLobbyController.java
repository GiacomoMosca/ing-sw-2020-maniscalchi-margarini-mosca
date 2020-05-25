package it.polimi.ingsw.view.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

    public void setManager(GUIManager manager) {
        this.manager = manager;
    }

    public void joinPressed(){
        Platform.runLater(()-> {
            joinButtonPressed.setVisible(true);
            joinGame.setVisible(false);
            joinGamePressed.setVisible(true);
        });
    }

    public void joinReleased(){
        Platform.runLater(()-> {
            manager.putString("2");
            manager.setBusy(false);
            joinGamePressed.setVisible(false);
            joinGame.setVisible(true);
            joinButtonPressed.setVisible(false);
        });
    }

    public void newGamePressed(){
        Platform.runLater(()-> {
            newGameButtonPressed.setVisible(true);
            newGame.setVisible(false);
            newGamePressed.setVisible(true);
        });
    }

    public void newGameReleased(){
        Platform.runLater(()-> {
            manager.putString("1");
            manager.setBusy(false);
            newGamePressed.setVisible(false);
            newGame.setVisible(true);
            newGameButtonPressed.setVisible(false);
        });
    }
}
