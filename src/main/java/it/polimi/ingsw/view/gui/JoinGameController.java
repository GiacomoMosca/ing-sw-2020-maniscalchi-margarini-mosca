package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.view.GameView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class JoinGameController /*implements Initializable*/ {

    private GUIManager manager;

    @FXML
    private Label label;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TableColumn gameName;
    @FXML
    private TableColumn playersNumber;
    @FXML
    private TableView gameRoomsList;


    public void setManager(GUIManager manager) {
        this.manager = manager;
    }

    public void backButtonHandler(){
        manager.putString("0");
        manager.setBusy(false);
    }

    public void refreshButtonHandler(){
        manager.putString("1");
        manager.setBusy(false);
    }

    public void chooseGameRoom(ArrayList<GameView> gameRooms){
        Platform.runLater(() -> {
            final ObservableList<GameView> data = FXCollections.observableArrayList(gameRooms);

            gameName.setCellValueFactory(new PropertyValueFactory<GameView, String>("Name"));
            playersNumber.setCellValueFactory(new PropertyValueFactory<GameView, Integer>("PlayerNum"));

            gameRoomsList.setItems(data);
        });
    }
    /*
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        final ObservableList<GameView> data = FXCollections.observableArrayList(
                new GameView("Game 1",2,null,0,null,null),
                new GameView("Game 2",3,null,0,null,null),
                new GameView("Game 3",2,null,0,null,null)
        );

        gameName.setCellValueFactory(new PropertyValueFactory<GameView,String>("Name"));
        playersNumber.setCellValueFactory(new PropertyValueFactory<GameView,Integer>("PlayerNum"));

        gameRoomsList.setItems(data);
    }
    */
}
