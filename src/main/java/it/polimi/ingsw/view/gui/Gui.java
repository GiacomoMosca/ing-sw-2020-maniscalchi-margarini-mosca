package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.network.message.to_client.ToClientMessage;
import it.polimi.ingsw.network.message.to_server.SendInteger;
import it.polimi.ingsw.view.*;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * supposizioni: la initialize() di GameBoardController viene chiamata "prima..."
 * creazione gui -> chiamata di gui.initBoard()
 * creazione gameBoardController -> chiamata di gameBoardController.initialize()
 * ogni quanto controllare la board?
 */

public class Gui implements UI{//implements Runnable

    private GameView currentGame;
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
    public String getServerIp() {
        return null;
    }

    @Override
    public void parseMessage(ToClientMessage message) {
        //do
    }

    @Override
    public void sendBoolean(boolean body) {
        //do
    }

    @Override
    public void sendInteger(int body) {
        /*try {
            output.writeObject(new SendInteger(id, body));
        } catch (IOException e) {
            System.out.println("Disconnected. ");
            stop();
        }*/
    }

    @Override
    public void sendIntegers(ArrayList<Integer> body) {
        //do
    }

    @Override
    public void sendString(String body) {
        //do
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

    //utilizzo la stessa funzione per mostrare a schermo qualsiasi scelta: illumino le caselle! e quando le disillumino però?
    //mostrare messaggi diversi
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
    public void chooseStartingPlayer(ArrayList<PlayerView> players) {
        //gameBoardController.chooseStartingPlayer(players);
    }

    @Override
    public void chooseStartJoin() {

    }

    @Override
    public void chooseYesNo(String query) {

    }

    @Override
    public void displayGameInfo(GameView game, String desc) {
        currentGame = game;
        //in base al messaggio...
        //per ora chiamo update e faccio un controllo
        gameBoardController.checkBoard(game);
    }

    @Override
    public void displayMessage(String message) {
        //di là stampa a schermo msg
    }

    @Override
    public void displayPlaceWorker(CellView position) {
        currentGame.setCell(position);
        gameBoardController.placeWorker(position);
        //displayBoard();       non voglio mostrare nuova board ad ogni chiamata (?)
    }

    //cosa fare qui e dopo quando goDcard != null
    //prima faccio muovere perhé mi serve cella di partenza
    //poi aggiorno gameView
    //poi mostro la board. vorrei mostrare la mossa con transizione alla fine! come si fa?
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

    //ogni quanto?
    public void updateBoard(GameView board){
        //gameBoardController.updateBoard(board);
    }

    public void displayBoard() {
        gameBoardController.getBoardStage().setResizable(true);
        gameBoardController.getBoardStage().setScene(gameBoardController.getBoardScene());
        gameBoardController.getBoardStage().show();
    }

}
