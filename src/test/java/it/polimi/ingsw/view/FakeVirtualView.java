package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game_board.Board;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class FakeVirtualView extends VirtualView {


    public FakeVirtualView(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
        super(socket, objectInputStream, objectOutputStream);
    }

    @Override
    public void displayBoard(Game game, String desc, Card card) throws IOException {
    }

    @Override
    public void displayMessage(String message) throws IOException {
    }

    @Override
    public Worker chooseWorker(ArrayList<Worker> workers) throws IOException, ClassNotFoundException {
        return workers.get(0);
    }

    @Override
    public Cell chooseMovePosition(ArrayList<Cell> possibleMoves) throws IOException, ClassNotFoundException {
        return possibleMoves.get(0);
    }

    @Override
    public Cell chooseBuildPosition(ArrayList<Cell> possibleBuilds) throws IOException, ClassNotFoundException {
        return possibleBuilds.get(0);
    }

    @Override
    public boolean chooseYesNo(String query) throws IOException, ClassNotFoundException {
        return true;
    }

    }
