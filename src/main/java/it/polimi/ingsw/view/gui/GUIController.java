package it.polimi.ingsw.view.gui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.concurrent.SynchronousQueue;

public class GUIController extends Application {

    private static ServerConnectionController serverConnectionController;
    private static NicknameChoiceController nicknameChoiceController;

    private AnchorPane anchorPane;



    @FXML
    private TextField nickname;

    public GUIController(SynchronousQueue<String> messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage=new Stage();
        primaryStage.setTitle("SANTORINI");
        serverConnectionController=new ServerConnectionController(primaryStage);
        serverConnectionController.initialize();
    }

    @FXML
    private void connectButtonHandler() {
        messageQueue.offer(serverIP.getText());
    }
}
