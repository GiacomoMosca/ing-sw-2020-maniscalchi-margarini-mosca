package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.network.message.to_client.ToClientMessage;
import it.polimi.ingsw.view.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class Gui implements UI{//implements Runnable

    private Stage boardStage;
    //tutti i vari stage da decidere

    private Scene sceneBoard;
    //tutte le varie scene per gli stage da decidere

    private GameBoardController gameBoardController;


    @Override
    public void run() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void parseMessage(ToClientMessage message) {

    }

    @Override
    public String getServerIp() {
        return null;
    }


    @Override
    public void chooseNickname() {

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
    public void gameOver() {

    }

    @Override
    public void chooseGameRoom(ArrayList<String> gameRooms) {

    }

    @Override
    public void chooseGameName() {

    }

    @Override
    public void chooseInt(String query, int max) {

    }

    public void initBoard(GameView board) throws IOException {
        gameBoardController=new GameBoardController(board);
        gameBoardController.initialize();
    }

    public void updateBoard(GameView board){
        gameBoardController.updateBoard(board);
    }


}
