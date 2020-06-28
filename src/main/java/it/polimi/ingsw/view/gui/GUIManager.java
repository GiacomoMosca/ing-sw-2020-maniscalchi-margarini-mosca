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
import javafx.scene.image.Image;
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

    private static Scene loginScene;
    private static Scene gameLobbyScene;
    private static Scene newGameScene;
    private static Scene joinGameScene;
    private static Scene gameStartingScene;
    private static Scene gameSetupScene;
    private static Scene gameBoardScene;
    private static Scene gameOverScene;

    private static TitleController titleController = null;
    private static LoginController loginController = null;
    private static GameLobbyController gameLobbyController = null;
    private static NewGameController newGameController = null;
    private static JoinGameController joinGameController = null;
    private static GameStartingController gameStartingController = null;
    private static GameSetupController gameSetupController = null;
    private static GameBoardController gameBoardController = null;
    private static GameOverController gameOverController = null;

    private static Scene currentScene;
    private static GUI gui;
    private static SynchronousQueue<Object> messageQueue;
    private String id;

    public void run() {
        launch();
    }

    public void setGUI(GUI gui) {
        GUIManager.gui = gui;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setQueue(SynchronousQueue<Object> messageQueue) {
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

    @Override
    public void start(Stage primaryStage) throws Exception {
        initAll();
        stage = primaryStage;
        stage.setTitle("Santorini");
        stage.getIcons().add(new Image("/assets/graphics/icon.png"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/title.fxml"));
        Parent root = loader.load();
        currentScene = new Scene(root, 1280, 720);
        titleController = loader.getController();
        titleController.setManager(this);
        stage.setScene(currentScene);
        stage.setResizable(false);
        stage.show();
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public void initAll() {
        initLogin();
        initGameLobby();
        initNewGame();
        initJoinGame();
        initGameStarting();
        initGameSetup();
        initGameBoard();
        initGameOver();
    }

    public void initLogin() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        try {
            Parent root = loader.load();
            loginScene = new Scene(root);
            loginController = loader.getController();
            loginController.initialize(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initNewGame() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/newGame.fxml"));
        try {
            Parent root = loader.load();
            newGameScene = new Scene(root);
            newGameController = loader.getController();
            newGameController.initialize(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initJoinGame() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/joinGame.fxml"));
        try {
            Parent root = loader.load();
            joinGameScene = new Scene(root);
            joinGameController = loader.getController();
            joinGameController.initialize(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initGameLobby() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/gameLobby.fxml"));
        try {
            Parent root = loader.load();
            gameLobbyScene = new Scene(root);
            gameLobbyController = loader.getController();
            gameLobbyController.initialize(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initGameStarting() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/gameStarting.fxml"));
        try {
            Parent root = loader.load();
            gameStartingScene = new Scene(root);
            gameStartingController = loader.getController();
            gameStartingController.initialize(this);
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
            gameSetupController.initialize(this);
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
            gameBoardController.initialize(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initGameOver() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/gameOver.fxml"));
        try {
            Parent root = loader.load();
            gameOverScene = new Scene(root);
            gameOverController = loader.getController();
            gameOverController.initialize(this);
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

    // queue

    public void putObject(Object object) {
        new Thread(() -> messageQueue.offer(object)).start();
    }

    // generic

    public void displayMessage(String message) {
        if (currentScene.equals(gameBoardScene)) gameBoardController.displayMessage(message);

        setBusy(false);
    }

    public void chooseYesNo(String query) {
        if (currentScene.equals(gameSetupScene)) gameSetupController.chooseYesNo(query);
        else if (currentScene.equals(gameBoardScene)) gameBoardController.chooseYesNo(query);
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
        setScene(loginScene);
    }

    public void chooseNickname(boolean taken) {
        loginController.chooseNickname();
        if (taken)
            loginController.errorMessage("Nickname already taken.");
    }

    public void serverErrorMessage(String message) {
        loginController.errorMessage(message);
    }

    // GameLobbyController

    public void chooseStartJoin() {
        initGameLobby();
        setScene(gameLobbyScene);
    }

    public void chooseGameName(boolean taken) {
        initNewGame();
        setScene(newGameScene);
        newGameController.chooseGameName(taken);
    }

    public void choosePlayersNumber() {
        newGameController.choosePlayersNumber();
    }

    public void chooseGameRoom(ArrayList<GameView> gameRooms) {
        initJoinGame();
        setScene(joinGameScene);
        joinGameController.chooseGameRoom(gameRooms);
    }

    // ReadyToStartController

    public void notifyGameStarting() {
        gameStartingController.notifyGameStarting();
    }

    // GameSetupController

    public void displayGameInfo(GameView game, String desc) {
        switch (desc) {
            case "playerJoined":
                if (!currentScene.equals(gameStartingScene)) {
                    initGameStarting();
                    setScene(gameStartingScene);
                }
                gameStartingController.displayPlayerJoined(game);
                break;
            case "gameSetup":
                initGameSetup();
                setScene(gameSetupScene);
                gameSetupController.displayGameInfo();
                break;
            case "boardSetup":
                initGameBoard();
                setScene(gameBoardScene);
                gameBoardController.initialize(game);
                gameBoardController.displayGameInfo(game, desc);
                break;
            default:
                gameBoardController.displayGameInfo(game, desc);
                break;
        }
    }

    public void chooseCards(ArrayList<CardView> possibleCards, int num, ArrayList<CardView> pickedCards) {
        ArrayList<String> possibleCardsNames = new ArrayList<String>();
        for (CardView card : possibleCards) {
            possibleCardsNames.add(card.getGod().toLowerCase());
        }
        if (num > 1) gameSetupController.chooseAllCards(possibleCardsNames, num);
        else {
            ArrayList<String> pickedCardsNames = new ArrayList<String>();
            for (CardView card : pickedCards) {
                pickedCardsNames.add(card.getGod().toLowerCase());
            }
            gameSetupController.chooseMyCard(possibleCardsNames, pickedCardsNames);
        }
    }

    // GameBoardController

    public void chooseStartingPlayer(ArrayList<PlayerView> players) {
        gameBoardController.chooseStartingPlayer();
    }

    public void choosePosition(ArrayList<CellView> positions, String desc) {
        gameBoardController.choosePosition(positions, desc);
    }

    public void displayBuild(CellView buildPosition, CardView godCard) {
        gameBoardController.displayBuild(buildPosition, godCard);
    }

    public void displayMove(HashMap<CellView, CellView> moves, CardView godCard) {
        gameBoardController.displayMove(moves, godCard);
    }

    public void displayPlaceWorker(CellView position) {
        gameBoardController.displayPlaceWorker(position);
    }

    public void notifyLoss(String reason, PlayerView player) {
        gameBoardController.notifyLoss(reason, player);
    }

    public void notifyWin(String reason) {
        gameBoardController.notifyWin(reason);
    }

    // GameOver

    public void notifyDisconnection(PlayerView player) {
        initGameOver();
        setScene(gameOverScene);
        gameOverController.notifyDisconnection(player);
    }

    public void notifyGameOver() {
        initGameOver();
        setScene(gameOverScene);
        gameOverController.notifyGameOver();
    }

}
