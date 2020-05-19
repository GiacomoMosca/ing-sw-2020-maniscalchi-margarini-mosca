package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.network.message.to_client.ToClientMessage;
import it.polimi.ingsw.network.message.to_server.ToServerMessage;
import it.polimi.ingsw.view.*;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class GUI extends Application implements UI {//implements Runnable

    private Stage boardStage;
    private Stage primaryStage;
    //tutti i vari stage da decidere

    private Scene sceneBoard;
    //tutte le varie scene per gli stage da decidere

    private GameBoardController gameBoardController;
    private static ServerConnectionController serverConnectionController;
    private static NicknameChoiceController nicknameChoiceController;

    private AnchorPane anchorPane;

    private final AtomicBoolean running;
    private Socket server;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private SynchronousQueue<String> messageQueue;
    private String id;

    @FXML
    private TextField serverIP;

    @FXML
    private TextField nickname;

    public GUI() {
        this.running = new AtomicBoolean();
        this.id = null;
    }


    @Override
    public void run() {
        GUI.launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage=new Stage();
        primaryStage.setTitle("SANTORINI");
        serverConnectionController=new ServerConnectionController(primaryStage);
        serverConnectionController.initialize();
    }

    private void inputListener() {
        Scanner scanner = new Scanner(System.in);
        while (running.get()) {
            String input = scanner.nextLine();
            switch (input) {
                // more commands go here
                case "/quit":
                    quit();
                    break;
            }
            messageQueue.offer(input);
        }
    }

    @Override
    public void stop() {
        try {
            super.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void connectToServer() {
        running.set(true);
        messageQueue = new SynchronousQueue<String>();
        new Thread(this::inputListener).start();

        String ip = serverIP.getText();
        server = new Socket();
        try {
            server.connect(new InetSocketAddress(ip, 8000), 5 * 1000);
        } catch (IOException e) {
            System.out.println("Server unreachable. ");
            stop();
            return;
        }
        System.out.println("Connected! ");

        try {
            output = new ObjectOutputStream(server.getOutputStream());
            input = new ObjectInputStream(server.getInputStream());
        } catch (IOException e) {
            System.out.println("Server is down. ");
            stop();
            return;
        } catch (ClassCastException e) {
            System.out.println("Protocol violation. ");
            stop();
            return;
        }

        ToClientMessage message;
        while (running.get()) {
            try {
                message = (ToClientMessage) input.readObject();
            } catch (IOException e) {
                System.out.println("Disconnected. ");
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }
            if (message != null) {
                parseMessage(message);
            }
        }
        stop();
    }

    public void parseMessage(ToClientMessage message) {
        message.performAction(this);
    }

    public void sendMessage(Object body) {
        try {
            output.writeObject(new ToServerMessage(body, id));
        } catch (IOException e) {
            System.out.println("Disconnected. ");
            stop();
        }
    }

    public String getServerIp() {
        return null;
    }

    public void chooseNickname(boolean taken) {
        //Stage primarStage=new Stage();
        nicknameChoiceController=new NicknameChoiceController(primaryStage);
        try {
            nicknameChoiceController.initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleChooseButton(){
        id = nickname.getText();
        sendMessage(null);
    }

    @Override
    public void chooseStartJoin() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/gameChoice.fxml"));
        try {
            anchorPane=(AnchorPane)loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void choosePlayersNumber() {

    }

    @Override
    public void chooseCards(ArrayList<CardView> possibleCards, int num, ArrayList<CardView> pickedCards) {

    }

    @Override
    public void chooseStartingPlayer(ArrayList<PlayerView> players) {

    }

    @Override
    public void updateGame(GameView board, String desc, CardView godPower) {

    }

    //@Override
    public void displayBoard(GameView board, String desc, CardView godPower) {
        updateBoard(board);
        gameBoardController.getBoardStage().setResizable(true);
        gameBoardController.getBoardStage().setScene(gameBoardController.getBoardScene());
        gameBoardController.getBoardStage().show();
    }

    @Override
    public void displayMessage(String message) {

    }

    @Override
    public void choosePosition(ArrayList<CellView> positions, String desc) {

    }

    @Override
    public void chooseYesNo(String query) {

    }

    @Override
    public void notifyLoss(PlayerView player, String reason) {

    }

    @Override
    public void notifyWin(PlayerView player, String reason) {

    }

    @Override
    public void notifyDisconnection(PlayerView player) {

    }

    @Override
    public void gameOver() {

    }

    //@Override
    public void chooseGameRoom(ArrayList<GameView> gameRooms) {

    }

    @Override
    public void chooseGameName(boolean taken) {

    }

    //@Override
    public void chooseInt(String query, int max) {

    }

    public void initBoard(GameView board) throws IOException {
        gameBoardController=new GameBoardController(board);
        gameBoardController.initialize();
    }

    public void updateBoard(GameView board){
        gameBoardController.updateBoard(board);
    }

    private void quit() {
        stop();
    }
}
