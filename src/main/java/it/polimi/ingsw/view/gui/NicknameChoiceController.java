package it.polimi.ingsw.view.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;

public class NicknameChoiceController {


    AnchorPane anchorPane;

    private Stage menuStage;
    private Scene menuScene;

    public NicknameChoiceController(Stage stage){
        menuStage=stage;
        menuScene=null;
        anchorPane=null;
    }

    public void initialize() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nicknameChoice.fxml"));
        anchorPane=loader.load();
        menuScene=new Scene(anchorPane, 960, 540);
        displayScene(menuStage,menuScene);
    }

    public void displayScene(Stage stage, Scene scene) {
        stage.setScene(scene);
        stage.show();
    }
}