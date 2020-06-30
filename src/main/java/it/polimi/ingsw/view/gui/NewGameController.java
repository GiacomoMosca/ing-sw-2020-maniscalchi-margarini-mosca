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

/**
 * Handles the interaction between client and server during the creation of a new Game.
 */
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

    /**
     * @param manager the GUIManager to set the JoinGameController manager attribute to
     */
    public void initialize(GUIManager manager) {
        this.manager = manager;
        choosingName = false;
        nicknameTab.setText(manager.getId());
        confirmButton.setDisable(true);
    }

    /**
     * Handles the pressing of the Confirm button.
     */
    public void confirmPressed() {
        Platform.runLater(() -> {
            confirmButton_p.setVisible(true);
            confirmText_p.setVisible(true);
            confirmText.setVisible(false);
        });
    }

    /**
     * Handles the releasing of the Confirm button.
     * When the Player inserts a valid Game name in the TextBox, notices the GUIManager that the Confirm button was clicked, putting the input on the messageQueue.
     * Until the inserted Game name is not valid, an error message appears and it's not possible to continue.
     */
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

    /**
     * Allows the Player to choose a Game name, eventually informing him that the previously chosen Game name is already taken.
     *
     * @param taken true if the previously chosen nickname is already taken, false otherwise
     */
    public void chooseGameName(boolean taken) {
        choosingName = true;
        if (taken) errorMessage("Name already taken.");
        Platform.runLater(() -> {
            gameNameField.editableProperty().setValue(true);
            confirmButton.setDisable(false);
        });
    }

    /**
     * Allows the Player to choose a number of Players (2 or 3) for the Game he is creating.
     * When the Player made his choice, he can click on the Start button to start the new Game.
     */
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

    /**
     * Allows displaying an error message when the inserted Game name is not valid, because:
     * <ul>
     *     <li>it's longer than 12 characters;
     *     <li>it's already taken.
     * </ul>
     *
     * @param message the String describing the error message
     */
    public void errorMessage(String message) {
        Platform.runLater(() -> {
            gameNameField.clear();
            error.setText(message);
            error.setVisible(true);
            confirmButton.setDisable(false);
        });
    }
}
