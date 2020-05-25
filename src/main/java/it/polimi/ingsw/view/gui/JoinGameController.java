package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.view.GameView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.util.ArrayList;

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


    public void initialize(GUIManager manager) {
        this.manager = manager;
    }

    public void backButtonPressed() {
        Platform.runLater(() -> {
            backButtonPressed.setVisible(true);
            backButtonText.setVisible(false);
            backPressedButtonText.setVisible(true);
        });
    }

    public void refreshButtonPressed() {
        Platform.runLater(() -> {
            refreshButtonPressed.setVisible(true);
            refreshButtonText.setVisible(false);
            refreshPressedButtonText.setVisible(true);
        });
    }

    public void confirmButtonPressed() {
        Platform.runLater(() -> {
            confirmButtonPressed.setVisible(true);
            confirmButtonText.setVisible(false);
            confirmPressedButtonText.setVisible(true);
        });
    }

    public void backButtonReleased() {
        Platform.runLater(() -> {
            backButton.setDisable(true);
            manager.putObject(0);
            manager.setBusy(false);
            backButtonPressed.setVisible(false);
            backPressedButtonText.setVisible(false);
            backButtonText.setVisible(true);
            backButton.setDisable(false);
        });
    }

    public void refreshButtonReleased() {
        Platform.runLater(() -> {
            manager.putObject(1);
            manager.setBusy(false);
            refreshButtonPressed.setVisible(false);
            refreshPressedButtonText.setVisible(false);
            refreshButtonText.setVisible(true);
        });
    }

    public void confirmButtonReleased() {
        confirmButton.setDisable(true);
        int choice = gameRoomsList.getSelectionModel().getSelectedIndex();
        if (choice != -1) {
            manager.putObject(choice + 2);
            manager.setBusy(false);
        } else
            confirmButton.setDisable(false);

        Platform.runLater(() -> {
            confirmButtonPressed.setVisible(false);
            confirmPressedButtonText.setVisible(false);
            confirmButtonText.setVisible(true);
        });
    }

    public void chooseGameRoom(ArrayList<GameView> gameRooms) {
        ArrayList<TableItem> rooms = new ArrayList<TableItem>();
        for (GameView gameView : gameRooms)
            rooms.add(new TableItem(gameView));

        final ObservableList<TableItem> data = FXCollections.observableArrayList(rooms);

        gameName.setCellValueFactory(new PropertyValueFactory<GameView, String>("GameName"));
        playersNumber.setCellValueFactory(new PropertyValueFactory<GameView, String>("PlayersInRoom"));

        Platform.runLater(() -> {
            gameRoomsList.setItems(data);
        });
    }
}
