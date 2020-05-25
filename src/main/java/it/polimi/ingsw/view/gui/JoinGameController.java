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
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class JoinGameController {

    private GUIManager manager;

    @FXML
    private TableColumn gameName;
    @FXML
    private TableColumn playersNumber;
    @FXML
    private TableView gameRoomsList;
    @FXML
    private Text title;
    @FXML
    private Text refreshButtonText;
    @FXML
    private Text refreshPressedButtonText;
    @FXML
    private Text backButtonText;
    @FXML
    private Text backPressedButtonText;
    @FXML
    private Text confirmButtonText;
    @FXML
    private Text confirmPressedButtonText;
    @FXML
    private ImageView refreshButton;
    @FXML
    private ImageView refreshButtonPressed;
    @FXML
    private ImageView backButton;
    @FXML
    private ImageView backButtonPressed;
    @FXML
    private ImageView confirmButton;
    @FXML
    private ImageView confirmButtonPressed;


    public void setManager(GUIManager manager) {
        this.manager = manager;
    }

    public void backButtonPressed(){
        Platform.runLater(()->{
            backButtonPressed.setVisible(true);
            backButtonText.setVisible(false);
            backPressedButtonText.setVisible(true);
        });
    }

    public void refreshButtonPressed(){
        Platform.runLater(()->{
            refreshButtonPressed.setVisible(true);
            refreshButtonText.setVisible(false);
            refreshPressedButtonText.setVisible(true);
        });
    }

    public void confirmButtonPressed(){
        Platform.runLater(()->{
            confirmButtonPressed.setVisible(true);
            confirmButtonText.setVisible(false);
            confirmPressedButtonText.setVisible(true);
        });
    }

    public void backButtonReleased(){
        Platform.runLater(()->{
            manager.putString("0");
            manager.setBusy(false);
            backButtonPressed.setVisible(false);
            backPressedButtonText.setVisible(false);
            backButtonText.setVisible(true);
        });
    }

    public void refreshButtonReleased(){
        Platform.runLater(()->{
            manager.putString("1");
            manager.setBusy(false);
            refreshButtonPressed.setVisible(false);
            refreshPressedButtonText.setVisible(false);
            refreshButtonText.setVisible(true);
        });
    }

    public void confirmButtonReleased(){
        Platform.runLater(()->{
            manager.putString(String.valueOf(gameRoomsList.getSelectionModel().getSelectedIndex()+2));
            manager.setBusy(false);
            confirmButtonPressed.setVisible(false);
            confirmPressedButtonText.setVisible(false);
            confirmButtonText.setVisible(true);
        });
    }

    public void chooseGameRoom(ArrayList<GameView> gameRooms){
        Platform.runLater(() -> {
            final ObservableList<GameView> data = FXCollections.observableArrayList(gameRooms);

            gameName.setCellValueFactory(new PropertyValueFactory<GameView, String>("Name"));
            playersNumber.setCellValueFactory(new PropertyValueFactory<GameView, Integer>("PlayerNum"));

            gameRoomsList.setItems(data);
        });
    }
}
