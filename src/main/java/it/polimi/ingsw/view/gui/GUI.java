package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.network.message.to_client.ToClientMessage;
import it.polimi.ingsw.view.*;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class GUI implements UI{//implements Runnable

    private GameView currentGame;
    private Stage boardStage;
    //tutti i vari stage da decidere

    private Scene sceneBoard;
    //tutte le varie scene da decidere

    private GameBoardController gameBoardController;

    public GUI() {

    }

    @Override
    public void run() {

    }
    private void inputListener() {

    }

    private void quit() {

    }

    public synchronized void stop() {

    }

    @Override
    public String getServerIp() {
        //default
        return null;
    }

    @Override
    public void parseMessage(ToClientMessage message) {
        message.performAction(this);
    }

    @Override
    public void sendBoolean(boolean body) {

    }

    @Override
    public void sendInteger(int body) {

    }

    @Override
    public void sendIntegers(ArrayList<Integer> body) {

    }

    @Override
    public void sendString(String body) {

    }

    @Override
    public void chooseCards(ArrayList<CardView> possibleCards, int num, ArrayList<CardView> pickedCards) {

    }

    @Override
    public void chooseGameName(boolean taken) {

    }

    @Override
    public void chooseGameRoom(ArrayList<GameView> gameRooms) {

    }

    @Override
    public void chooseNickname(boolean taken) {
    }

    @Override
    public void choosePlayersNumber() {

    }

    //TO DO: verificare in GameBoardController di "disilluminare" le celle dopo la scelta
    //TO DO: decidere se mostrare messaggi diversi per ogni scelta
    @Override
    public void choosePosition(ArrayList<CellView> positions, String desc) {
            Integer choice=null;
            StringBuilder string = new StringBuilder();
            string.append("\n");
            switch (desc) {
                case "start1":
                    string.append("(Worker 1) Choose the starting position for your worker:");
                    choice=gameBoardController.prepareChoice(positions);
                    break;
                case "start2":
                    string.append("(Worker 2) Choose the starting position for your worker:");
                    choice=gameBoardController.prepareChoice(positions);
                    break;
                case "worker":
                    string.append("Choose a worker:");
                    choice=gameBoardController.prepareChoice(positions);
                    break;
                case "move":
                    string.append("Choose a position to move to:");
                    choice=gameBoardController.prepareChoice(positions);
                    break;
                case "build":
                    string.append("Choose a position to build in:");
                    choice=gameBoardController.prepareChoice(positions);
                    break;
            }
            sendInteger(choice);
        }


    @Override
    public void chooseStartingPlayer(ArrayList<PlayerView> players) { }

    @Override
    public void chooseStartJoin() { }

    @Override
    public void chooseYesNo(String query) { }

    @Override
    public void displayGameInfo(GameView game, String desc) {
        //TO DO: decidere se aggiornare/controllare la "boardView" qui
        currentGame = game;
    }

    @Override
    public void displayMessage(String message) {

    }

    @Override
    public void displayPlaceWorker(CellView position) {
        currentGame.setCell(position);
        gameBoardController.placeWorker(position);
        //TO DO: serve chiamare la displayBoard();  ?
    }

    //TO DO: cosa fare qui e dopo quando godCard != null: mostrare potere usato?
    @Override
    public void displayMove(HashMap<CellView, CellView> moves, CardView godCard) {
        gameBoardController.moveWorker(moves, godCard);
        moves.forEach((startPosition, endPosition) -> {
            CellView newStart = new CellView(
                    startPosition.getPosX(), startPosition.getPosY(), startPosition.getBuildLevel(), startPosition.isDomed(), null
            );
            currentGame.setCell(newStart);
            CellView newEnd = new CellView(
                    endPosition.getPosX(), endPosition.getPosY(), endPosition.getBuildLevel(), endPosition.isDomed(), startPosition.getWorker()
            );
            currentGame.setCell(newEnd);
        });
        displayBoard();
    }

    @Override
    public void displayBuild(CellView buildPosition, CardView godCard) {
        currentGame.setCell(buildPosition);
        gameBoardController.buildLevel(buildPosition, godCard);
        displayBoard();
    }


    @Override
    public void notifyDisconnection(PlayerView player) {

    }

    @Override
    public void notifyGameOver() {

    }

    @Override
    public void notifyLoss(PlayerView player, String reason) {

    }

    @Override
    public void notifyWin(PlayerView player, String reason) {

    }

    public void initBoard(GameView gameView) throws IOException {
        gameBoardController=new GameBoardController(gameView);
        gameBoardController.initialize();
    }

    public void displayBoard() {
        gameBoardController.getBoardStage().setResizable(true);
        gameBoardController.getBoardStage().setScene(gameBoardController.getBoardScene());
        gameBoardController.getBoardStage().show();
    }

}


/*
 *  avremo controller per:
 *  - inserire IP server / nickname
 *  - start game / join game
 *  - partita
 *
 */