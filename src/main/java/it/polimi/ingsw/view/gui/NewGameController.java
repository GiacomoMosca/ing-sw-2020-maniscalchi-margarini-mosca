package it.polimi.ingsw.view.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class NewGameController {

    private GUIManager manager;

    @FXML
    private Button setButton;
    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private TextField textField;
    @FXML
    private Label playersNumber;

    public void setManager(GUIManager manager) {
        this.manager = manager;
    }

    public void setButtonHandler() {
        manager.putString(textField.getText());
        manager.setBusy(false);
    }

    public void startGameButtonHandler(){
        manager.putString(choiceBox.getSelectionModel().getSelectedItem());
        manager.setBusy(false);
    }

    public void choosePlayersNumber() {
        Platform.runLater(() -> {
            textField.editableProperty().setValue(false);
            playersNumber.setVisible(true);
            choiceBox.setVisible(true);
            ObservableList<String> availableChoices = FXCollections.observableArrayList("2","3");
            choiceBox.setItems(availableChoices);
            setButton.setLayoutY(352);
            setButton.setText("Start Game");
            setButton.setOnAction(e -> startGameButtonHandler());
            manager.setBusy(false);
        });
    }
}
