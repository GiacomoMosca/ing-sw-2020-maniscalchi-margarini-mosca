package it.polimi.ingsw.view.gui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.concurrent.SynchronousQueue;

public class ServerConnectionController extends Application {

    private SynchronousQueue<String> messageQueue;

    @FXML
    private TextField serverIP;

    AnchorPane anchorPane;

    private Stage menuStage;
    private Scene menuScene;

    public ServerConnectionController(SynchronousQueue<String> messageQueue, Stage stage){
        this.messageQueue=messageQueue;
        menuStage=stage;
        menuScene=null;
        anchorPane=null;
    }

    public void initialize() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/serverConnection.fxml"));
        anchorPane=loader.load();
        menuScene=new Scene(anchorPane, 960, 540);
        displayScene(menuStage,menuScene);
    }

    public void displayScene(Stage stage, Scene scene) {
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void start(Stage stage) throws Exception {

    }
}
