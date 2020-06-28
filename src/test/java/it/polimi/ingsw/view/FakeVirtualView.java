package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.PlayerController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.network.message.to_server.SendInteger;
import it.polimi.ingsw.network.message.to_server.SendIntegers;
import it.polimi.ingsw.network.message.to_server.ToServerMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class FakeVirtualView extends VirtualView {


    public FakeVirtualView(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
        super(socket, objectInputStream, objectOutputStream);
    }


    @Override
    public Worker chooseWorker(ArrayList<Worker> workers) {
        return workers.get(0);
    }

    @Override
    public Cell chooseMovePosition(ArrayList<Cell> possibleMoves) {
        return possibleMoves.get(0);
    }

    @Override
    public Cell chooseBuildPosition(ArrayList<Cell> possibleBuilds) {
        return possibleBuilds.get(0);
    }

    @Override
    public boolean chooseYesNo(String query) {
        return false;
    }

    @Override
    public void displayBuild(CellView buildPosition, Card godPower) throws IOException {
        //
    }

    @Override
    public void displayGameInfo(Game game, String desc) throws IOException {
        //
    }

    @Override
    public void displayMessage(String message) throws IOException {
        //
    }

    @Override
    public void displayMove(HashMap<CellView, CellView> moves, Card godPower) throws IOException {
        //
    }

    @Override
    public void displayPlaceWorker(Cell workerPosition) throws IOException {
        //
    }

    @Override
    public void resetStreams() throws IOException {
        //
    }

    @Override
    public Socket getSocket() {
        return super.getSocket();
    }

    @Override
    public String getId() {
        return "player";
    }

    @Override
    public void setId(String id) {
        //
    }

    @Override
    public PlayerController getPlayerController() {
        return super.getPlayerController();
    }

    @Override
    public void setPlayerController(PlayerController playerController) {
        super.setPlayerController(playerController);
    }

    @Override
    public boolean isInGame() {
        return super.isInGame();
    }

    @Override
    public void clientListener() {
        //
    }

    @Override
    public ToServerMessage takeInput() throws InterruptedException {
        return null;
    }

    @Override
    public void checkAlive() throws IOException {
        //
    }

    @Override
    public ArrayList<Card> chooseCards(ArrayList<Card> possibleCards, int num, ArrayList<Card> pickedCards) throws IOException, InterruptedException {
        ArrayList<Integer> choices = new ArrayList<>();
        choices.add(0);
        ArrayList<Card> chosenCards = new ArrayList<Card>();
        for (int i : choices) {
            chosenCards.add(possibleCards.get(i));
        }
        return chosenCards;
    }

    @Override
    public String chooseGameName(boolean taken) throws IOException, InterruptedException {
        return null;
    }

    @Override
    public String chooseGameRoom(ArrayList<Game> gameRooms) throws IOException, InterruptedException {
        return null;
    }

    @Override
    public String chooseNickname(boolean taken) throws IOException, InterruptedException {
        return null;
    }

    @Override
    public int choosePlayersNumber() throws IOException, InterruptedException {
        return 3;
    }

    @Override
    public Cell chooseStartPosition(ArrayList<Cell> possiblePositions, int num) throws IOException, InterruptedException {
        return possiblePositions.get(10);
    }

    @Override
    public int chooseStartingPlayer(ArrayList<Player> players) throws IOException, InterruptedException {
        return 1;
    }

    @Override
    public boolean chooseStartJoin() throws IOException, InterruptedException {
        return true;
    }

    @Override
    public void notifyGameStarting() throws IOException, InterruptedException {
        //
    }

    @Override
    public void notifyLoss(String reason, Player winner) throws IOException {
        //
    }

    @Override
    public void notifyWin(String reason) throws IOException {
        //
    }

    @Override
    public void notifyDisconnection(Player player) throws IOException {
        //
    }

    @Override
    public void notifyGameOver() throws IOException {
        //
    }
}
