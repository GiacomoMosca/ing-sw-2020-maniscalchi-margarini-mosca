package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Worker;

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
        return true;
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

}
