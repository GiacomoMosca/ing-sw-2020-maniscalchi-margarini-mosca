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

/**
 * Manager for the GUI class.
 * Extends Application, the entry point for JavaFX applications, so that a GUI can be started through the launch() method.
 */
public class GUIManager extends Application {

    /**
     * Set to <code>false</code> only when the GUI is ready to switch to the next instruction.
     */
    private final static AtomicBoolean busy = new AtomicBoolean(true);
    /**
     * Lock that allows only one message at a time to be processed by the GUI.
     */
    private final static Object busyLock = new Object();

    /**
     * The main stage.
     */
    private static Stage stage;

    /**
     * The scene used by the Login screen.
     */
    private static Scene loginScene;
    /**
     * The scene used by the Game Lobby screen.
     */
    private static Scene gameLobbyScene;
    /**
     * The scene used by the New Game screen.
     */
    private static Scene newGameScene;
    /**
     * The scene used by the Join Game screen.
     */
    private static Scene joinGameScene;
    /**
     * The scene used by the Game Starting screen.
     */
    private static Scene gameStartingScene;
    /**
     * The scene used by the Game Setup screen.
     */
    private static Scene gameSetupScene;
    /**
     * The scene used by the Game Board screen.
     */
    private static Scene gameBoardScene;
    /**
     * The scene used by the Game Over screen.
     */
    private static Scene gameOverScene;

    /**
     * The controller for the Title screen.
     */
    private static TitleController titleController = null;
    /**
     * The controller for the Login screen.
     */
    private static LoginController loginController = null;
    /**
     * The controller for the Game Lobby screen.
     */
    private static GameLobbyController gameLobbyController = null;
    /**
     * The controller for the New Game screen.
     */
    private static NewGameController newGameController = null;
    /**
     * The controller for the Join Game screen.
     */
    private static JoinGameController joinGameController = null;
    /**
     * The controller for the Game Starting screen.
     */
    private static GameStartingController gameStartingController = null;
    /**
     * The controller for the Game Setup screen.
     */
    private static GameSetupController gameSetupController = null;
    /**
     * The controller for the Game Board screen.
     */
    private static GameBoardController gameBoardController = null;
    /**
     * The controller for the Game Over screen.
     */
    private static GameOverController gameOverController = null;

    /**
     * The currently active scene.
     */
    private static Scene currentScene;
    /**
     * The main GUI instance.
     */
    private static GUI gui;
    /**
     * Internal queue for inputs from the user.
     */
    private static SynchronousQueue<Object> messageQueue;
    /**
     * The nickname chosen by the user.
     */
    private String id;

    /**
     * Launch the application.
     */
    public void run() {
        launch();
    }

    /**
     * Sets the gui static attribute of the GUIManager as the GUI received as argument.
     *
     * @param gui the GUI to set the gui attribute to
     */
    public void setGUI(GUI gui) {
        GUIManager.gui = gui;
    }

    /**
     * @return the ID associated to the GUIManager
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id static attribute of the GUIManager as the String received as argument.
     *
     * @param id the ID of the GUIManager
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the messageQueue static attribute of the GUIManager as the SynchronousQueue received as argument.
     *
     * @param messageQueue the messageQueue
     */
    public void setQueue(SynchronousQueue<Object> messageQueue) {
        GUIManager.messageQueue = messageQueue;
    }

    /**
     * @return the busyLock Object
     */
    public Object getLock() {
        return busyLock;
    }

    /**
     * If the current value of busy is !val, sets busy to val. Returns true if successful, false otherwise.
     *
     * @param val the updated value to set busy to
     * @return true if val was set to the received argument, false otherwise
     */
    public boolean setBusy(boolean val) {
        synchronized (busyLock) {
            boolean res = busy.compareAndSet(!val, val);
            busyLock.notifyAll();
            return res;
        }
    }

    /**
     * @return true if the GUIManager is busy, false otherwise
     */
    public boolean isBusy() {
        return busy.get();
    }

    /**
     * Handles the initialization of all the Controllers and all the scenes.
     * Sets the stage attribute as the primaryStage received as argument; all the scenes will be set on this stage.
     * Sets the title of the stage, his size, his FXML Controller and other attributes.
     *
     * @param primaryStage the primary Stage
     * @throws Exception when an exception occurs while loading the Parent using the FXMLLoader
     */
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

    /**
     * Calls all the methods that will initialize the Controllers and the scenes.
     */
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

    /**
     * Loads the loginController from the associated FXML resource, sets its loginScene and initializes it.
     */
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

    /**
     * Loads the newGameController from the associated FXML resource, sets its newGameScene and initializes it.
     */
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

    /**
     * Loads the joinGameController from the associated FXML resource, sets its joinGameScene and initializes it.
     */
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

    /**
     * Loads the gameLobbyController from the associated FXML resource, sets its gameLobbyScene and initializes it.
     */
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

    /**
     * Loads the gameStartingController from the associated FXML resource, sets its gameStartingScene and initializes it.
     */
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

    /**
     * Loads the gameSetUpController from the associated FXML resource, sets its gameSetUpScene and initializes it.
     */
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

    /**
     * Loads the gameBoardController from the associated FXML resource, sets its gameBoardScene and initializes it.
     */
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

    /**
     * Loads the gameOverController from the associated FXML resource, sets its gameOverScene and initializes it.
     */
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

    /**
     * @param scene the Scene to set the currentScene attribute to
     */
    private void setScene(Scene scene) {
        if (currentScene == scene) return;
        Platform.runLater(() -> {
            currentScene = scene;
            stage.setScene(scene);
        });
    }

    // queue

    /**
     * Starts a new Thread to offer the Object received as argument (an input provided by the client) on the messageQueue.
     *
     * @param object the Object to offer on the messageQueue
     */
    public void putObject(Object object) {
        new Thread(() -> messageQueue.offer(object)).start();
    }

    // generic


    /**
     * Allows the player to display a message received from the server.
     *
     * @param message the message sent
     */
    public void displayMessage(String message) {
        if (message.equals("The room is already full. ") || message.equals("The room doesn't exist anymore. ")) {
            initGameOver();
            setScene(gameOverScene);
            gameOverController.notifyGameError(message);
        } else if (currentScene.equals(gameBoardScene)) gameBoardController.displayMessage(message);
        else setBusy(false);
        // deprecated for other instances
    }

    /**
     * Allows the Player to answer to a "yes or no question".
     * Depending on the currentScene, the chooseYesNo method is called on the right Controller, which will show the question and provide the client's answer.
     *
     * @param query the "yes or no question" the Player should answer to
     */
    public void chooseYesNo(String query) {
        if (currentScene.equals(gameSetupScene)) gameSetupController.chooseYesNo(query);
        else if (currentScene.equals(gameBoardScene)) gameBoardController.chooseYesNo(query);
    }

    // LoginController

    /**
     * When loginController is not null sets the currentScene attribute of the GUIManager as the loginScene.
     */
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

    /**
     * Allows the Player to choose his nickname.
     * The chooseNickname method is called on the loginController, which will show the question and provide the client's answer.
     *
     * @param taken if the previously chosen nickname is already taken, false otherwise
     */
    public void chooseNickname(boolean taken) {
        loginController.chooseNickname();
        if (taken)
            loginController.errorMessage("Nickname already taken.");
    }

    /**
     * Allows notifying the Player of an error message coming from the server.
     * The errorMessage method is called on the loginController, which will show the error message.
     *
     * @param message the message to show
     */
    public void serverErrorMessage(String message) {
        loginController.errorMessage(message);
    }

    // GameLobbyController

    /**
     * Allows the Player to choose to start a new Game or joining an existing one.
     * Initializes the Game Lobby and sets the currentScene attribute of the GUIManager as the gameLobbyScene.
     * The gameLobbyController initialization will provide to show the question and to notice the client's answer.
     */
    public void chooseStartJoin() {
        initGameLobby();
        setScene(gameLobbyScene);
    }

    /**
     * Allows the Player to choose to choose a Game name.
     * Initializes the new Game and sets the currentScene attribute of the GUIManager as the newGameScene.
     * The newGameController will provide to show the question and to notice the client's answer.
     *
     * @param taken if the previously chosen Game name is already taken, false otherwise
     */
    public void chooseGameName(boolean taken) {
        initNewGame();
        setScene(newGameScene);
        newGameController.chooseGameName(taken);
    }

    /**
     * Allows the Player to choose the number of Players for the Game he is creating.
     * The newGameController will provide to show the question and to notice the client's answer.
     */
    public void choosePlayersNumber() {
        newGameController.choosePlayersNumber();
    }

    /**
     * Allows the Player to choose which Game room to join among those available.
     * Sets the currentScene attribute of the GUIManager as the joinGameScene
     * The joinGameController will provide to show the question and to notice the client's answer.
     *
     * @param gameRooms an ArrayList containing all the Game rooms the Player can choose among
     */
    public void chooseGameRoom(ArrayList<GameView> gameRooms) {
        initJoinGame();
        setScene(joinGameScene);
        joinGameController.chooseGameRoom(gameRooms);
    }

    // ReadyToStartController

    /**
     * Allows the Player to be notified that the Game is starting.
     * The gameStartingController will provide showing the information.
     */
    public void notifyGameStarting() {
        gameStartingController.notifyGameStarting();
    }

    // GameSetupController

    /**
     * Depending on the received information, the right method is called on the Controller which is supposed to display that information.
     * This may require the initialization of Controllers not yet initialized and the setting of the appropriate scene.
     *
     * @param game the GameView representing the current state of the Game
     * @param desc the information to display
     */
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

    /**
     * Allows the Player to choose some Cards between those available.
     * He could be asked to choose the 2 or 3 Cards to be used in a 2 or 3-players Game, or his own Card for the Game.
     * Depending on the choice he has to make, the right method is called on the gameSetUpController.
     *
     * @param possibleCards an ArrayList containing all the available Cards
     * @param num           the number of Cards to choose
     * @param pickedCards   an ArrayList containing all the already picked Cards
     */
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

    /**
     * Allows the Player to choose the starting Player.
     * The gameBoardController will handle the choice and will provide the client's answer.
     *
     * @param players an ArrayList of PlayerViews representing all the Players involved in the Game
     */
    public void chooseStartingPlayer(ArrayList<PlayerView> players) {
        gameBoardController.chooseStartingPlayer();
    }

    /**
     * Allows the Player to choose a position.
     * The gameBoardController will handle the choice and will provide the client's answer.
     *
     * @param positions an ArrayList containing CellViews representing all the available positions
     * @param desc      the reason of the choice
     */
    public void choosePosition(ArrayList<CellView> positions, String desc) {
        gameBoardController.choosePosition(positions, desc);
    }

    /**
     * Notifies the Player that a new build occurred.
     * The gameBoardController will handle the displaying of this new build.
     *
     * @param buildPosition the CellView representing the position of the building
     * @param godCard       the CardView representing the God Card which eventually allowed this build
     */
    public void displayBuild(CellView buildPosition, CardView godCard) {
        gameBoardController.displayBuild(buildPosition, godCard);
    }

    /**
     * Notifies the Player that a new move occurred.
     * The gameBoardController will handle the displaying of this new move.
     *
     * @param moves   an HashMap containing pairs of (starting position, final position) for each worker who moved or was forced to move
     * @param godCard the CardView representing the God Card which eventually allowed this move
     */
    public void displayMove(HashMap<CellView, CellView> moves, CardView godCard) {
        gameBoardController.displayMove(moves, godCard);
    }

    /**
     * Notifies the Player that a Worker was placed on its starting position.
     * The gameBoardController will handle the displaying of this placing.
     *
     * @param position the starting position of a Worker
     */
    public void displayPlaceWorker(CellView position) {
        gameBoardController.displayPlaceWorker(position);
    }

    /**
     * Notifies a Player that he lost.
     * The gameBoardController will handle this notification.
     *
     * @param reason the reason of the loss
     * @param player the PlayerView representing the Player who eventually won, can be null
     */
    public void notifyLoss(String reason, PlayerView player) {
        gameBoardController.notifyLoss(reason, player);
    }

    /**
     * Notifies a Player that he won.
     * The gameBoardController will handle this notification.
     *
     * @param reason the reason of the victory
     */
    public void notifyWin(String reason) {
        gameBoardController.notifyWin(reason);
    }

    // GameOver

    /**
     * Notifies a Player that another Player disconnected.
     * The gameOverController will handle this notification.
     *
     * @param player the PlayerView representing the Player who disconnected
     */
    public void notifyDisconnection(PlayerView player) {
        initGameOver();
        setScene(gameOverScene);
        gameOverController.notifyDisconnection(player);
    }

    /**
     * Notifies a Player that the Game is over.
     * The gameOverController will handle this notification.
     */
    public void notifyGameOver() {
        initGameOver();
        setScene(gameOverScene);
        gameOverController.notifyGameOver();
    }

    /**
     * Notifies a Player that the server is down.
     * The gameOverController will handle this notification.
     */
    public void serverClosed() {
        initGameOver();
        setScene(gameOverScene);
        gameOverController.serverClosed();
    }

}
