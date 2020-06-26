package it.polimi.ingsw.view.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class NewGameController {

    private GUIManager manager;
    private boolean choosingName;

    @FXML
    private Text nicknameTab, titleText;
    @FXML
    private HBox playersBox;
    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private TextField gameNameField;
    @FXML
    private Text gameNameText, playersText;
    @FXML
    private Text error;
    @FXML
    private ImageView confirmButton, confirmButton_p;
    @FXML
    private Text confirmText, confirmText_p;

    public void initialize(GUIManager manager) {
        this.manager = manager;
        choosingName = false;
        nicknameTab.setText(manager.getId());
        confirmButton.setDisable(true);
    }

    public void confirmPressed() {
        Platform.runLater(() -> {
            confirmButton_p.setVisible(true);
            confirmText_p.setVisible(true);
            confirmText.setVisible(false);
        });
    }

    public void confirmReleased() {
        confirmButton.setDisable(true);
        if (choosingName) {
            String gameName = gameNameField.getText();
            if (!gameName.trim().isEmpty()) {
                if (gameName.length() > 12)
                    errorMessage("Invalid input (max 12 characters).");
                else {
                    error.setVisible(false);
                    manager.putObject(gameName);
                    manager.setBusy(false);
                }
            } else
                confirmButton.setDisable(false);

            Platform.runLater(() -> {
                confirmButton_p.setVisible(false);
                confirmText_p.setVisible(false);
                confirmText.setVisible(true);
            });
        } else {
            String playersNum = choiceBox.getSelectionModel().getSelectedItem();
            if (playersNum != null) {
                manager.putObject(Integer.parseInt(playersNum));
                manager.setBusy(false);
            } else {
                confirmButton.setDisable(false);
            }
            Platform.runLater(() -> {
                confirmButton_p.setVisible(false);
                confirmText_p.setVisible(false);
                confirmText.setVisible(true);
            });
        }
    }

    public void chooseGameName(boolean taken) {
        choosingName = true;
        if (taken) errorMessage("Name already taken.");
        Platform.runLater(() -> {
            gameNameField.editableProperty().setValue(true);
            confirmButton.setDisable(false);
        });
    }

    public void choosePlayersNumber() {
        choosingName = false;
        ObservableList<String> availableChoices = FXCollections.observableArrayList("2", "3");
        Platform.runLater(() -> {
            gameNameField.editableProperty().setValue(false);
            playersBox.setVisible(true);
            choiceBox.setItems(availableChoices);
            confirmText.setText("Start");
            confirmText_p.setText("Start");
            confirmButton.setDisable(false);
        });
    }

    public void errorMessage(String message) {
        Platform.runLater(() -> {
            gameNameField.clear();
            error.setText(message);
            error.setVisible(true);
            confirmButton.setDisable(false);
        });
    }
}
