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
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class NewGameController {

    private GUIManager manager;

    @FXML
    private ImageView button;
    @FXML
    private ImageView buttonPressed;
    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private TextField textField;
    @FXML
    private Text text1;
    @FXML
    private Text text2;
    @FXML
    private Text buttonPressedText;
    @FXML
    private Text buttonText;
    @FXML
    private StackPane stackPane;


    public void setManager(GUIManager manager) {
        this.manager = manager;
    }

    public void pressed() {
        Platform.runLater(() -> {
            buttonPressed.setVisible(true);
            buttonText.setVisible(false);
            buttonPressedText.setVisible(true);
        });
    }

    public void chooseReleased(){
        Platform.runLater(() -> {
            manager.putString(textField.getText());
            manager.setBusy(false);
            buttonPressed.setVisible(false);
            buttonPressedText.setVisible(false);
            buttonText.setVisible(true);
        });

    }

    public void startReleased(){
        Platform.runLater(() -> {
            manager.putString(choiceBox.getSelectionModel().getSelectedItem());
            manager.setBusy(false);
            buttonPressed.setVisible(false);
            buttonPressedText.setVisible(false);
            buttonText.setVisible(true);
        });
    }

    public void choosePlayersNumber() {
        Platform.runLater(() -> {
            textField.editableProperty().setValue(false);
            text2.setVisible(true);
            choiceBox.setVisible(true);
            ObservableList<String> availableChoices = FXCollections.observableArrayList("2","3");
            choiceBox.setItems(availableChoices);
            stackPane.setLayoutY(350);
            buttonText.setText("Start Game");
            buttonPressedText.setText("Start Game");
            button.setOnMouseReleased(e -> startReleased());
            manager.setBusy(false);
        });
    }
}
