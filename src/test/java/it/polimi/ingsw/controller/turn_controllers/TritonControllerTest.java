package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.PlayerController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.view.FakeVirtualView;
import it.polimi.ingsw.view.VirtualView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class TritonControllerTest {
    private TritonController tritonController;
    private FakeGameController fakeGameController;
    private FakeVirtualView fakeVirtualView;
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public class FakeGameController extends GameController {

        public FakeGameController(VirtualView client, int num, String gameName) {
            super(client, num, gameName);
        }

        @Override
        public void addPlayer(VirtualView client) {
            Player player = new Player(client.getId(), colors.get(playerControllers.size()));
            PlayerController playerController = new PlayerController(player, client);
            game.addPlayer(player);
            playerControllers.add(playerController);
            gameSetUp();
        }

        @Override
        public void gameSetUp() {
            Deck deck = game.getDeck();
            deck.addCard(tritonController.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(tritonController);

            placeWorkers();
            placeBuildings();
            playGame();
        }

        private void placeWorkers() {
            Worker worker = new Worker(players.get(0));
            worker.setPosition(game.getBoard().getCell(2, 2));
            players.get(0).addWorker(worker);
        }

        private void placeBuildings(){
            game.getBoard().getCell(0,0).buildDome();
            game.getBoard().getCell(0,1).buildDome();
            game.getBoard().getCell(0,2).buildDome();
            game.getBoard().getCell(2,2).buildDome();

        }

        public void playGame() {
            String result = playerControllers.get(game.getActivePlayer()).playTurn();
            if(result.equals("WON"))
                game.setWinner(players.get(game.getActivePlayer()));
        }
    }

    @Before
    public void setUp(){
        socket=new Socket();
        fakeVirtualView = new FakeVirtualView(socket, objectInputStream, objectOutputStream);
        fakeGameController = new FakeGameController(fakeVirtualView,1, "game");
        tritonController = new TritonController(fakeGameController);
    }

    @After
    public void tearDown(){
    }

    @Test
    public void generateCard_noInputGiven_shouldReturnTheGodCard() {
        Card testCard = new Card(
                "Triton",
                "God of the Waves",
                "Your Move: Each time your Worker moves onto a perimeter space (ground or block), it may immediately move again.",
                2,
                false,
                tritonController);
        assertEquals(tritonController.generateCard(), testCard); }

    @Test
    public void movePhase_noInputGiven_shouldMoveTwoTimes() {
        //buildPhase() is later called by gameSetUp()
        //the worker starts from (2,1), then moves in (0,1) and moves again to (1,1)
        //check if it's there
        fakeGameController.gameSetUp();
        assertEquals(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition(), fakeGameController.getGame().getBoard().getCell(1,1));
    }

    @Test
    public void movePhase_noInputGiven_shouldGenerateFirstExceptionIllegalMove() throws IOException, ClassNotFoundException {
        //a client who chooses to move in a domed cell and doesn't want to move again
        class FakeVirtualViewToGenerateException extends FakeVirtualView{
            public FakeVirtualViewToGenerateException(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream){
                super(socket, objectInputStream, objectOutputStream);
            }
            @Override
            public Cell chooseMovePosition(ArrayList<Cell> possibleMoves){
                return(fakeGameController.getGame().getBoard().getCell(1,1));
            }
            @Override
            public boolean chooseYesNo(String query){
                return false;
            }
        }

        socket=new Socket();
        fakeVirtualView=new FakeVirtualViewToGenerateException(socket, objectInputStream, objectOutputStream);
        fakeGameController=new FakeGameController(fakeVirtualView, 1, "game");
        tritonController=new TritonController(fakeGameController);
        tritonController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);
        Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(1,2));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        tritonController.activeWorker=worker;
        fakeGameController.getGame().getBoard().getCell(1,1).buildDome();

        tritonController.movePhase();
    }

    @Test
    public void movePhase_noInputGiven_shouldGenerateSecondExceptionIllegalMove() throws IOException, ClassNotFoundException {
        //a client who chooses to move twice in a domed cell
        class FakeVirtualViewToGenerateException extends FakeVirtualView{
            public FakeVirtualViewToGenerateException(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream){
                super(socket, objectInputStream, objectOutputStream);
            }
            @Override
            public Cell chooseMovePosition(ArrayList<Cell> possibleMoves){
                return(fakeGameController.getGame().getBoard().getCell(0,1));
            }
            @Override
            public Cell chooseBuildPosition(ArrayList<Cell> possibleBuilds){
                return(fakeGameController.getGame().getBoard().getCell(1,2));
            }
            @Override
            public boolean chooseYesNo(String query){
                return true;
            }
        }

        socket=new Socket();
        fakeVirtualView=new FakeVirtualViewToGenerateException(socket, objectInputStream, objectOutputStream);
        fakeGameController=new FakeGameController(fakeVirtualView, 1, "game");
        tritonController=new TritonController(fakeGameController);
        tritonController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);
        Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(1,1));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        tritonController.activeWorker=worker;
        fakeGameController.getGame().getBoard().getCell(0,1).buildDome();

        tritonController.movePhase();
    }

    @Test
    public void movePhase_noInputGiven_shouldMoveTheSecondTime() throws IOException, ClassNotFoundException {
        tritonController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);
        Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(4,4));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        tritonController.activeWorker=worker;
        fakeGameController.getGame().getBoard().getCell(3,4).buildDome();
        fakeGameController.getGame().getBoard().getCell(3,3).buildDome();
        fakeGameController.getGame().getBoard().getCell(4,2).buildDome();

        // the worker starts from 4,4. then moves to 4,3 and the second time to 3,2

        tritonController.movePhase();
        assertEquals(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition(), fakeGameController.getGame().getBoard().getCell(3,2));
    }
}