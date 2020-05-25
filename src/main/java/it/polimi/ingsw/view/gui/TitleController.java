package it.polimi.ingsw.view.gui;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class TitleController {
    private GUIManager manager;

    @FXML
    private ImageView playButton;
    @FXML
    private Text playText;
    @FXML
    private Text playTextPressed;

    public void setManager(GUIManager manager) {
        this.manager = manager;
    }

    public void pressed(){
        Platform.runLater(() ->{
            playButton.setVisible(true);
            playText.setVisible(false);
            playTextPressed.setVisible(true);
        });

    }
    public void released(){
        Platform.runLater(() ->{
            manager.setBusy(false);
            playButton.setVisible(false);
            playTextPressed.setVisible(false);
            playText.setVisible(true);
        });
    }
}
