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

public class PrometheusControllerTest {
    PrometheusController prometheusController = null;
    FakeGameController fakeGameController = null;
    FakeVirtualView fakeVirtualView;
    Socket socket;
    ObjectInputStream ois;
    ObjectOutputStream ous;

    public class FakeGameController extends GameController {

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
            deck.addCard(prometheusController.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(prometheusController);

            placeWorkers();
            placeBuildings();
            playGame();
        }

        private void placeWorkers() {
            Worker worker = new Worker(players.get(0));
            worker.setPosition(game.getBoard().getCell(1, 2));
            players.get(0).addWorker(worker);
        }

        private void placeBuildings() {
            game.getBoard().getCell(2, 2).setBuildLevel(1);
        }

        public void playGame() {
            String result = playerControllers.get(game.getActivePlayer()).playTurn();
            if (result.equals("WON"))
                game.setWinner(players.get(game.getActivePlayer()));
        }

        @Override
        public void displayBoard() {
        }
    }

    @Before
    public void setUp() throws Exception {
        socket=new Socket();
        fakeVirtualView=new FakeVirtualView(socket, ois, ous);
        fakeVirtualView.setId("PrometheusTest");
        fakeGameController=new FakeGameController(fakeVirtualView, 1);
        prometheusController=new PrometheusController(fakeGameController);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void generateCard() {
        Card testCard=new Card("Prometheus", "Titan Benefactor of Mankind", "Your Turn: If your Worker does not move up, it may build both before and after moving.", 1, false, prometheusController);
        assertEquals(prometheusController.generateCard().getGod(), testCard.getGod());
        assertEquals(prometheusController.generateCard().getTitle(), testCard.getTitle());
        assertEquals(prometheusController.generateCard().getDescription(), testCard.getDescription());
        assertEquals(prometheusController.generateCard().getSet(), testCard.getSet());
        assertEquals(prometheusController.generateCard().hasAlwaysActiveModifier(), testCard.hasAlwaysActiveModifier());
        assertEquals(prometheusController.generateCard().getController(), testCard.getController());
    }

    @Test
    public void runPhases_workerGiven_shouldReturnNEXT() throws IOException, ClassNotFoundException {
        Deck deck = fakeGameController.getGame().getDeck();
        Card card = prometheusController.generateCard();
        deck.addCard(card);
        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        prometheusController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);

        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(1, 2));

        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        prometheusController.activeWorker = worker;

        fakeGameController.getGame().getBoard().getCell(1, 2).setBuildLevel(2);
        fakeGameController.getGame().getBoard().getCell(0, 1).setBuildLevel(2);
        assertEquals(prometheusController.runPhases(worker), "NEXT");
    }

    @Test
    public void checkMoves_noInputGiven_shouldReturnTrue(){
        Deck deck = fakeGameController.getGame().getDeck();
        Card card = prometheusController.generateCard();
        deck.addCard(card);
        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        prometheusController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);

        Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(1,2));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        prometheusController.activeWorker=worker;

        fakeGameController.getGame().getBoard().getCell(1,2).setBuildLevel(2);
        fakeGameController.getGame().getBoard().getCell(0,1).setBuildLevel(3);

        assertTrue(prometheusController.checkMoves());
    }

    @Test
    public void checkMoves_noInputGiven_shouldReturnFalse(){
        Deck deck = fakeGameController.getGame().getDeck();
        Card card = prometheusController.generateCard();
        deck.addCard(card);
        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        prometheusController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);

        Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(0,0));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        prometheusController.activeWorker=worker;

        fakeGameController.getGame().getBoard().getCell(0,0).setBuildLevel(0);
        fakeGameController.getGame().getBoard().getCell(0,1).setBuildLevel(2);
        fakeGameController.getGame().getBoard().getCell(1,1).setBuildLevel(2);
        fakeGameController.getGame().getBoard().getCell(1,0).setBuildLevel(2);

        assertFalse(prometheusController.checkMoves());
    }

    @Test
    public void movePhase_noInputGiven_shouldNotBuildBeforeAndGenerateMovingException() throws IOException, ClassNotFoundException {
        //a client who chooses to move in an illegal cell
        class FakeVirtualViewToGenerateException extends FakeVirtualView{
            public FakeVirtualViewToGenerateException(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream){
                super(socket, objectInputStream, objectOutputStream);
            }
            @Override
            public Cell chooseMovePosition(ArrayList<Cell> possibleMoves){
                return(fakeGameController.getGame().getBoard().getCell(1,1));
            }
            @Override
            public Cell chooseBuildPosition(ArrayList<Cell> possibleMoves){
                return(fakeGameController.getGame().getBoard().getCell(2,2));
            }
        }

        socket=new Socket();
        fakeVirtualView=new FakeVirtualViewToGenerateException(socket, ois, ous);
        fakeVirtualView.setId("PrometheusTestToGenerateException");
        fakeGameController=new FakeGameController(fakeVirtualView, 1);
        prometheusController=new PrometheusController(fakeGameController);
        prometheusController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);
        Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(0,0));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        prometheusController.activeWorker=worker;
        fakeGameController.getGame().getBoard().getCell(0,1).buildDome();
        fakeGameController.getGame().getBoard().getCell(1,1).buildDome();
        fakeGameController.getGame().getBoard().getCell(1,0).buildDome();

        prometheusController.runPhases(worker);
    }

    @Test
    public void findPossibleMovesNoUp() {
        Deck deck = fakeGameController.getGame().getDeck();
        Card card = prometheusController.generateCard();
        deck.addCard(card);
        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        prometheusController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);

        Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(1,2));
        fakeGameController.getGame().getBoard().getCell(2, 2).setBuildLevel(1);

        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        prometheusController.activeWorker=worker;

        ArrayList<Cell> expectedArrayList = fakeGameController.getGame().getBoard().getNeighbors(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition());
        expectedArrayList.remove(fakeGameController.getGame().getBoard().getCell(2,2));
        assertEquals(prometheusController.findPossibleMovesNoUp(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition()), expectedArrayList);
    }
}