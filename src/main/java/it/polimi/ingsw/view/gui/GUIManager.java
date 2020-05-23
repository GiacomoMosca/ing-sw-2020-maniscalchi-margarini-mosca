package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.view.CardView;
import it.polimi.ingsw.view.CellView;
import it.polimi.ingsw.view.GameView;
import it.polimi.ingsw.view.PlayerView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class GUIManager extends Application {

    private final static AtomicBoolean busy = new AtomicBoolean(true);
    private final static Object busyLock = new Object();

    private static Stage stage;

    private static Scene gameLobbyScene;
    private static Scene gameSetupScene;
    private static Scene gameBoardScene;

    private static LoginController loginController = null;
    private static GameLobbyController gameLobbyController = null;
    private static GameSetupController gameSetupController = null;
    private static GameBoardController gameBoardController = null;

    private static Scene currentScene;
    private static GUI gui;
    private static SynchronousQueue<String> messageQueue;

    public void run() {
        launch();
    }

    public void setGui(GUI gui) {
        GUIManager.gui = gui;
    }

    public void setQueue(SynchronousQueue<String> messageQueue) {
        GUIManager.messageQueue = messageQueue;
    }

    public Object getLock() {
        return busyLock;
    }

    public boolean setBusy(boolean val) {
        synchronized (busyLock) {
            boolean res = busy.compareAndSet(!val, val);
            busyLock.notifyAll();
            return res;
        }
    }

    public boolean isBusy() {
        return busy.get();
    }

    public void putString(String string) {
        messageQueue.offer(string);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initAll();
        stage = primaryStage;
        stage.setTitle("Santorini");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();
        currentScene = new Scene(root, 1280, 720);
        loginController = loader.getController();
        loginController.setManager(this);
        stage.setScene(currentScene);
        stage.setResizable(false);
        stage.show();
        busy.set(false);
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public void initAll() {
        initGameLobby();
        initGameSetup();
        initGameBoard();
    }

    private void initGameLobby() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/gameLobby.fxml"));
        try {
            Parent root = loader.load();
            gameLobbyScene = new Scene(root);
            gameLobbyController = loader.getController();
            gameLobbyController.setManager(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initGameSetup() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/gameSetup.fxml"));
        try {
            Parent root = loader.load();
            gameSetupScene = new Scene(root);
            gameSetupController = loader.getController();
            gameSetupController.setManager(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initGameBoard() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/gameBoard.fxml"));
        try {
            Parent root = loader.load();
            gameBoardScene = new Scene(root);
            gameBoardController = loader.getController();
            gameBoardController.setManager(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setScene(Scene scene) {
        if (currentScene == scene) return;
        Platform.runLater(() -> {
            currentScene = scene;
            stage.setScene(scene);
        });
    }

    // generic

    public void displayMessage(String message) {
        setBusy(false);
    }

    public void chooseYesNo(String query) {
        setBusy(false);
    }

    // LoginController

    public void getServerIp() {
        while (loginController == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //
            }
        }
        loginController.getServerIp();
    }

    public void chooseNickname(boolean taken) {
        loginController.chooseNickname();
    }

    // GameLobbyController

    public void chooseStartJoin() {
        setScene(gameLobbyScene);
        gameLobbyController.chooseStartJoin();
    }

    public void chooseGameName(boolean taken) {
        gameLobbyController.chooseGameName();
    }

    public void choosePlayersNumber() {
        gameLobbyController.choosePlayersNumber();
    }

    public void chooseGameRoom(ArrayList<GameView> gameRooms) {
        gameLobbyController.chooseGameRoom();
    }

    // GameSetupController

    public void displayGameInfo(GameView game, String desc) {
        switch (desc) {
            case "gameSetup1":
                setScene(gameSetupScene);
                gameSetupController.displayGameInfo();
                break;
            case "gameSetup2":
                setScene(gameBoardScene);
                gameBoardController.displayGameInfo();
                break;
            default:
                gameBoardController.displayGameInfo();
                break;
        }
    }

    public void chooseCards(ArrayList<CardView> possibleCards, int num, ArrayList<CardView> pickedCards) {
        gameSetupController.chooseCards();
    }

    // GameBoardController

    public void chooseStartingPlayer(ArrayList<PlayerView> players) {
        gameBoardController.chooseStartingPlayer();
    }

    public void choosePosition(ArrayList<CellView> positions, String desc) {
        gameBoardController.choosePosition();

    }

    public void displayBuild(CellView buildPosition, CardView godCard) {
        gameBoardController.displayBuild();
    }

    public void displayMove(HashMap<CellView, CellView> moves, CardView godCard) {
        gameBoardController.displayMove();
    }

    public void displayPlaceWorker(CellView position) {
        gameBoardController.displayPlaceWorker();
    }

    public void notifyDisconnection(PlayerView player) {
        gameBoardController.notifyDisconnection();
    }

    public void notifyGameOver() {
        gameBoardController.notifyGameOver();
    }

    public void notifyLoss(PlayerView player, String reason) {
        gameBoardController.notifyLoss();
    }

    public void notifyWin(PlayerView player, String reason) {
        gameBoardController.notifyWin();
    }

}
