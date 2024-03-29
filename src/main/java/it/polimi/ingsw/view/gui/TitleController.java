package it.polimi.ingsw.view.gui;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

/**
 * Handles the starting screen of the game.
 */
public class TitleController {

    /**
     * The manager for this GUI instance.
     */
    private GUIManager manager;

    @FXML
    private ImageView playButton, playButton_pressed;
    @FXML
    private Text playText, playText_pressed;

    /**
     *
     * @param manager the GUIManager to set the JoinGameController manager attribute to
     */
    public void setManager(GUIManager manager) {
        this.manager = manager;
    }

    /**
     * Handles the pressing of the Play button.
     */
    public void pressed() {
        Platform.runLater(() -> {
            playButton_pressed.setVisible(true);
            playText.setVisible(false);
            playText_pressed.setVisible(true);
        });

    }

    /**
     * Handles the releasing of the Play Button.
     */
    public void released() {
        Platform.runLater(() -> {
            manager.setBusy(false);
            playButton_pressed.setVisible(false);
            playText_pressed.setVisible(false);
            playText.setVisible(true);
        });
    }
}
