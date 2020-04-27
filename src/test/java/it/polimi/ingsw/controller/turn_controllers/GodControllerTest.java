package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.PlayerController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.view.FakeVirtualView;
import it.polimi.ingsw.view.UI;
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

public class GodControllerTest {

    FakeGameController fakeGameController;
    GodControllerConcrete genericController1, genericController2;
    FakeVirtualView fakeVirtualView1, fakeVirtualView2;
    Socket socket;
    ObjectInputStream ois;
    ObjectOutputStream ous;

    private class FakeGameController extends GameController{

        public FakeGameController(VirtualView client, int num) {
          super(client, num);
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
            Card card = new Card("god", "title", "description", 1, true, genericController1);
            Card card2 = new Card("god2", "title2", "description2", 1, true, genericController2);

            deck.addCard(card);
            deck.addCard(card2);
            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            players.get(1).setGodCard(deck.getCards().get(1));
            playerControllers.get(0).setGodController(genericController1);
            playerControllers.get(1).setGodController(genericController2);
            game.addModifier(card);
            game.addModifier(card2);

            placeWorkers();
            placeBuildings();
            playGame();
        }

        private void placeWorkers() {
            Worker worker=new Worker(players.get(0));
            worker.setPosition(game.getBoard().getCell(1,2));
            players.get(0).addWorker(worker);

            Worker worker2=new Worker(players.get(1));
            worker2.setPosition(game.getBoard().getCell(3,3));
            players.get(1).addWorker(worker2);
        }

        private void placeBuildings(){
            game.getBoard().getCell(3,3).setBuildLevel(1);
        }

        public void playGame() {
            String result = playerControllers.get(game.getActivePlayer()).playTurn();
            if(result.equals("WON"))
                game.setWinner(players.get(game.getActivePlayer()));
        }

        @Override
        public void displayBoard() { }
    }

    @Before
    public void setUp() {
        socket=new Socket();
        fakeVirtualView1=new FakeVirtualView(socket, ois, ous);
        fakeVirtualView1.setId("GenericGodControllerTest");
        fakeGameController=new FakeGameController(fakeVirtualView1,2 );
        genericController1=new GodControllerConcrete(fakeGameController);

        fakeVirtualView2=new FakeVirtualView(socket, ois, ous);
        genericController2=new GodControllerConcrete(fakeGameController);
    }

    @After
    public void tearDown(){ }

    @Test
    public void getPlayer_noInputGiven_shouldReturnPlayer() {
        //using addPlayer to add the second player to the game and call then GameSetUp()
        fakeGameController.addPlayer(fakeVirtualView1);
        assertSame(genericController1.getPlayer(), fakeGameController.getGame().getPlayers().get(0));
    }

    @Test
    public void getClient_noInputGiven_shouldReturnClient() {
        fakeGameController.addPlayer(fakeVirtualView2);
        assertSame(genericController1.getClient(), fakeVirtualView1);
    }

    @Test
    public void setPlayer_playerAndClientGiven_shouldLinkPlayerAndClientToGodController() {
        fakeGameController.addPlayer(fakeVirtualView2);
        assertSame(genericController1.getPlayer(), fakeGameController.getGame().getPlayers().get(0));
        assertSame(genericController1.getClient(), fakeVirtualView1);
    }

    @Test
    public void canPlay_workerGiven_shouldReturnTrue() {
        fakeGameController.addPlayer(fakeVirtualView2);
        assertTrue(genericController1.canPlay(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0)));
    }

    @Test
    public void runPhases_workerGiven_shouldReturnWON() throws IOException, ClassNotFoundException {
        //it's not okay to call fakeGameController.gameSetUp(): it would later call runPhases(), changing the expected result
        //so recreating here the game situation: worker will move from (1,2) to (0,1)
        //setting their building levels respectively to 2 and 3 so that the worker wins
        Deck deck = fakeGameController.getGame().getDeck();
        Card card = new Card("god", "title", "description", 1, true, genericController1);
        deck.addCard(card);
        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        genericController1.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView1);
        Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(1,2));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        fakeGameController.getGame().getBoard().getCell(1,2).setBuildLevel(2);
        fakeGameController.getGame().getBoard().getCell(0,1).setBuildLevel(3);

        assertEquals(genericController1.runPhases(worker), "WON");
    }

    @Test
    public void runPhases_workerGiven_shouldReturnNEXT() throws IOException, ClassNotFoundException {
        Deck deck = fakeGameController.getGame().getDeck();
        Card card = new Card("god", "title", "description", 1, true, genericController1);
        deck.addCard(card);
        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        genericController1.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView1);
        Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(1,2));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        fakeGameController.getGame().getBoard().getCell(1,2).setBuildLevel(2);
        fakeGameController.getGame().getBoard().getCell(0,1).setBuildLevel(2);

        assertEquals(genericController1.runPhases(worker), "NEXT");
    }

    @Test
    public void movePhase_noInputGiven_shouldMoveTheWorkerInTheExpectedCell() throws IOException, ClassNotFoundException {
        Deck deck = fakeGameController.getGame().getDeck();
        Card card = new Card("god", "title", "description", 1, true, genericController1);
        deck.addCard(card);
        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        genericController1.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView1);
        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(1, 2));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        genericController1.activeWorker=worker;
        genericController1.movePhase();

        assertSame(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition(), fakeGameController.getGame().getBoard().getCell(0, 1));
    }

    @Test
    public void runPhases_noInputGiven_shouldGenerateMovingAndBuildingExceptions() throws IOException, ClassNotFoundException {
        //a client who chooses to move and to build in a domed cell
        class FakeVirtualViewToGenerateException extends FakeVirtualView{
            public FakeVirtualViewToGenerateException(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream){
                super(socket, objectInputStream, objectOutputStream);
            }
            @Override
            public Cell chooseMovePosition(ArrayList<Cell> possibleMoves){
                return(fakeGameController.getGame().getBoard().getCell(3,3));
            }
            @Override
            public Cell chooseBuildPosition(ArrayList<Cell> possibleMoves){
                return(fakeGameController.getGame().getBoard().getCell(3,3));
            }
        }


        socket=new Socket();
        fakeVirtualView1=new FakeVirtualViewToGenerateException(socket, ois, ous);
        fakeVirtualView1.setId("GodControllerTestToGenerateException");
        fakeVirtualView1.setId("GenericTestToGenerateException");
        fakeGameController=new FakeGameController(fakeVirtualView1, 1);
        genericController1=new GodControllerConcrete(fakeGameController);
        genericController1.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView1);
        Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(0,0));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        genericController1.activeWorker=worker;
        fakeGameController.getGame().getBoard().getCell(3,3).buildDome();

        genericController1.runPhases(worker);
    }

    @Test
    public void buildPhase_noInputGiven_shouldBuildOnTheExpectedCell() throws IOException, ClassNotFoundException {
        Deck deck = fakeGameController.getGame().getDeck();
        Card card = new Card("god", "title", "description", 1, true, genericController1);
        deck.addCard(card);
        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        genericController1.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView1);
        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(1, 2));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        genericController1.activeWorker=worker;
        genericController1.buildPhase();

        assertEquals(fakeGameController.getGame().getBoard().getCell(0,1).getBuildLevel(), 1);
    }

    @Test
    public void checkWin_noInputGiven_shouldReturnTrue() {
        fakeGameController.addPlayer(fakeVirtualView2);
        fakeGameController.getGame().getBoard().getCell(2,2).setBuildLevel(2);
        genericController1.startingPosition=fakeGameController.getGame().getBoard().getCell(2,2);
        fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).setPosition(fakeGameController.getGame().getBoard().getCell(2,3));
        fakeGameController.getGame().getBoard().getCell(2,3).setBuildLevel(3);
        fakeGameController.getGame().getBoard().getCell(2,3).setWorker(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0));

        assertTrue(genericController1.checkWin());
    }

    @Test
    public void findPossibleMoves_workerPositionGiven_shouldReturnTheSameArrayListOfCells() {
        fakeGameController.addPlayer(fakeVirtualView2);
        ArrayList<Cell> expectedArrayList = fakeGameController.getGame().getBoard().getNeighbors(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition());
        assertEquals(genericController1.findPossibleMoves(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition()), expectedArrayList);
    }

    @Test
    public void findLegalMoves_workerPositionAndArrayListOfCellsGiven_shouldReturnTheSameArrayListOfCells() {
        fakeGameController.addPlayer(fakeVirtualView2);
        ArrayList<Cell> expectedArrayList = fakeGameController.getGame().getBoard().getNeighbors(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition());
        assertEquals(genericController1.findLegalMoves(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition(), expectedArrayList), expectedArrayList);
    }

    @Test
    public void findPossibleBuilds_workerPositionGiven_shouldReturnTheSameArrayListOfCells() {
        fakeGameController.addPlayer(fakeVirtualView2);
        ArrayList<Cell> expectedArrayList = fakeGameController.getGame().getBoard().getNeighbors(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition());
        assertEquals(genericController1.findPossibleBuilds(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition()), expectedArrayList);
    }

    @Test
    public void findLegalBuilds_workerPositionAndArrayListOfCellsGiven_shouldReturnTheSameArrayListOfCells() {
        fakeGameController.addPlayer(fakeVirtualView2);
        ArrayList<Cell> expectedArrayList = fakeGameController.getGame().getBoard().getNeighbors(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition());
        assertEquals(genericController1.findLegalMoves(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition(), expectedArrayList), expectedArrayList);
    }

    @Test
    public void limitMoves_workerPositionAndArrayListOfCellsGiven_shouldReturnTheSameArrayListOfCells() {
        fakeGameController.addPlayer(fakeVirtualView2);
        ArrayList<Cell> expectedArrayList = fakeGameController.getGame().getBoard().getNeighbors(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition());
        assertSame(genericController1.limitMoves(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition(), expectedArrayList), expectedArrayList);
    }

    @Test
    public void limitBuilds_workerPositionAndArrayListOfCellsGiven_shouldReturnTheSameArrayListOfCells() {
        fakeGameController.addPlayer(fakeVirtualView2);
        ArrayList<Cell> expectedArrayList = fakeGameController.getGame().getBoard().getNeighbors(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition());
        assertEquals(genericController1.limitBuilds(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition(), expectedArrayList), expectedArrayList);
    }
}