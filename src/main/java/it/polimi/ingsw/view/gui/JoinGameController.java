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
    private TableColumn<?, ?> gameName, createdBy, playersNumber;
    @FXML
    private TableView<TableItem> gameRoomsList;
    @FXML
    private Text nicknameTab, titleText;
    @FXML
    private Text refreshText, refreshText_p;
    @FXML
    private Text backText, backText_p;
    @FXML
    private Text confirmText, confirmText_p;
    @FXML
    private ImageView refreshButton, refreshButton_p;
    @FXML
    private ImageView backButton, backButton_p;
    @FXML
    private ImageView confirmButton, confirmButton_p;


    public void initialize(GUIManager manager) {
        this.manager = manager;
        nicknameTab.setText(manager.getId());
    }

    public void backButtonPressed() {
        Platform.runLater(() -> {
            backButton_p.setVisible(true);
            backText.setVisible(false);
            backText_p.setVisible(true);
        });
    }

    public void refreshButtonPressed() {
        Platform.runLater(() -> {
            refreshButton_p.setVisible(true);
            refreshText.setVisible(false);
            refreshText_p.setVisible(true);
        });
    }

    public void confirmButtonPressed() {
        Platform.runLater(() -> {
            confirmButton_p.setVisible(true);
            confirmText.setVisible(false);
            confirmText_p.setVisible(true);
        });
    }

    public void backButtonReleased() {
        Platform.runLater(() -> {
            backButton.setDisable(true);
            backButton_p.setVisible(false);
            backText_p.setVisible(false);
            backText.setVisible(true);
            backButton.setDisable(false);
            manager.putObject(0);
            manager.setBusy(false);
        });
    }

    public void refreshButtonReleased() {
        Platform.runLater(() -> {
            refreshButton_p.setVisible(false);
            refreshText_p.setVisible(false);
            refreshText.setVisible(true);
            manager.putObject(1);
            manager.setBusy(false);
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
            confirmButton_p.setVisible(false);
            confirmText_p.setVisible(false);
            confirmText.setVisible(true);
        });
    }

    public void chooseGameRoom(ArrayList<GameView> gameRooms) {
        ArrayList<TableItem> rooms = new ArrayList<TableItem>();
        for (GameView gameView : gameRooms)
            rooms.add(new TableItem(gameView));

        final ObservableList<TableItem> data = FXCollections.observableArrayList(rooms);

        gameName.setCellValueFactory(new PropertyValueFactory<>("GameName"));
        createdBy.setCellValueFactory(new PropertyValueFactory<>("CreatedBy"));
        playersNumber.setCellValueFactory(new PropertyValueFactory<>("Players"));

        Platform.runLater(() -> {
            gameRoomsList.setItems(data);
        });
    }
}
