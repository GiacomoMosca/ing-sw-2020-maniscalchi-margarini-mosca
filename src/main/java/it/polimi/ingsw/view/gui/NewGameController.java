package it.polimi.ingsw.view.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class NewGameController {

    private GUIManager manager;
    private PauseTransition visiblePause;

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
    private Text error;
    @FXML
    private Text centerText;
    @FXML
    private StackPane stackPane;


    public void initialize(GUIManager manager) {
        this.manager = manager;
        visiblePause = new PauseTransition(
                Duration.seconds(2)
        );
        visiblePause.setOnFinished(
                event -> error.setVisible(false)
        );
    }

    public void pressed() {
        Platform.runLater(() -> {
            buttonPressed.setVisible(true);
            buttonText.setVisible(false);
            buttonPressedText.setVisible(true);
        });
    }

    public void chooseReleased() {
        button.setDisable(true);
        String gameName = textField.getText();
        if (!gameName.trim().isEmpty()) {
            if(gameName.length()>12)
                errorMessage("Invalid input (max 12 characters)");
            else {
                manager.putObject(gameName);
                manager.setBusy(false);
            }
        }
        else
            button.setDisable(false);

        Platform.runLater(() -> {
            buttonPressed.setVisible(false);
            buttonPressedText.setVisible(false);
            buttonText.setVisible(true);
        });

    }

    public void startReleased() {
        button.setDisable(true);
        String playersNum = choiceBox.getSelectionModel().getSelectedItem();
        if (playersNum != null) {
            manager.putObject(Integer.parseInt(playersNum));
            Platform.runLater(() -> {
                buttonPressed.setVisible(false);
                buttonPressedText.setVisible(false);
                buttonText.setVisible(true);
                text1.setVisible(false);
                text2.setVisible(false);
                choiceBox.setVisible(false);
                textField.setVisible(false);
                button.setVisible(false);
                buttonText.setVisible(false);
                centerText.setVisible(true);
            });
            manager.setBusy(false);
        }
        else {
            button.setDisable(false);
            Platform.runLater(() -> {
                buttonPressed.setVisible(false);
                buttonPressedText.setVisible(false);
                buttonText.setVisible(true);
            });
        }

    }

    public void choosePlayersNumber() {
        Platform.runLater(() -> {
            textField.editableProperty().setValue(false);
            text2.setVisible(true);
            choiceBox.setVisible(true);
            ObservableList<String> availableChoices = FXCollections.observableArrayList("2", "3");
            choiceBox.setItems(availableChoices);
            stackPane.setLayoutY(350);
            buttonText.setText("Start Game");
            buttonPressedText.setText("Start Game");
            button.setOnMouseReleased(e -> startReleased());
            button.setDisable(false);
            //manager.setBusy(false); //Serve?
        });
    }

    public void errorMessage(String message){
        textField.clear();
        error.setText(message);
        error.setVisible(true);
        visiblePause.play();
        button.setDisable(false);
    }
}
