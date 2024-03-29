package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.FakeGameController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.game_board.Cell;
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
    private PrometheusController prometheusController = null;
    private PrometheusGameController fakeGameController = null;
    private FakeVirtualView fakeVirtualView;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    @Before
    public void setUp() throws Exception {
        socket = new Socket();
        fakeVirtualView = new FakeVirtualView(socket, objectInputStream, objectOutputStream);
        fakeGameController = new PrometheusGameController(fakeVirtualView, 1, "PrometheusTest");
        prometheusController = new PrometheusController(fakeGameController);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void generateCard() {
        Card testCard = new Card("Prometheus",
                "Titan Benefactor of Mankind",
                "Your Turn: If your Worker does not move up, it may build both " +
                        "\nbefore and after moving.",
                1,
                false,
                prometheusController);
        assertEquals(prometheusController.generateCard(), testCard);
    }

    @Test
    public void runPhases_workerGiven_shouldReturnNEXT() throws Exception {
        Deck deck = fakeGameController.getGame().getDeck();
        Card card = prometheusController.generateCard();
        deck.addCard(card);
        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        prometheusController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);

        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0), 1);
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(1, 2));

        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        prometheusController.activeWorker = worker;

        fakeGameController.getGame().getBoard().getCell(1, 2).setBuildLevel(2);
        fakeGameController.getGame().getBoard().getCell(0, 1).setBuildLevel(2);
        assertEquals(prometheusController.runPhases(worker), "next");
    }

    @Test
    public void checkMoves_noInputGiven_shouldReturnTrue() {
        Deck deck = fakeGameController.getGame().getDeck();
        Card card = prometheusController.generateCard();
        deck.addCard(card);
        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        prometheusController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);

        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0), 1);
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(1, 2));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        prometheusController.activeWorker = worker;

        fakeGameController.getGame().getBoard().getCell(1, 2).setBuildLevel(2);
        fakeGameController.getGame().getBoard().getCell(0, 1).setBuildLevel(3);

        assertTrue(prometheusController.checkMoves());
    }

    @Test
    public void checkMoves_noInputGiven_shouldReturnFalse() {
        Deck deck = fakeGameController.getGame().getDeck();
        Card card = prometheusController.generateCard();
        deck.addCard(card);
        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        prometheusController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);

        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0), 1);
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(0, 0));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        prometheusController.activeWorker = worker;

        fakeGameController.getGame().getBoard().getCell(0, 0).setBuildLevel(0);
        fakeGameController.getGame().getBoard().getCell(0, 1).setBuildLevel(2);
        fakeGameController.getGame().getBoard().getCell(1, 1).setBuildLevel(2);
        fakeGameController.getGame().getBoard().getCell(1, 0).setBuildLevel(2);

        assertFalse(prometheusController.checkMoves());
    }

    @Test
    public void movePhase_noInputGiven_shouldNotBuildBeforeAndGenerateMovingException() throws Exception {
        //a client who chooses to move in an illegal cell
        class FakeVirtualViewToGenerateException extends FakeVirtualView {
            public FakeVirtualViewToGenerateException(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
                super(socket, objectInputStream, objectOutputStream);
            }

            @Override
            public Cell chooseMovePosition(ArrayList<Cell> possibleMoves) {
                return (fakeGameController.getGame().getBoard().getCell(1, 1));
            }

            @Override
            public Cell chooseBuildPosition(ArrayList<Cell> possibleMoves) {
                return (fakeGameController.getGame().getBoard().getCell(2, 2));
            }
        }

        socket = new Socket();
        fakeVirtualView = new FakeVirtualViewToGenerateException(socket, objectInputStream, objectOutputStream);
        fakeGameController = new PrometheusGameController(fakeVirtualView, 1, "PrometheusTest");
        prometheusController = new PrometheusController(fakeGameController);
        prometheusController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);
        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0), 1);
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(0, 0));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        prometheusController.activeWorker = worker;
        fakeGameController.getGame().getBoard().getCell(0, 1).buildDome();
        fakeGameController.getGame().getBoard().getCell(1, 1).buildDome();
        fakeGameController.getGame().getBoard().getCell(1, 0).buildDome();

        prometheusController.runPhases(worker);
    }

    @Test
    public void findPossibleMovesNoUp_positionGiven_shouldReturnArrayListWithAllNeighborsExceptOneAtHigherLevel() {
        Deck deck = fakeGameController.getGame().getDeck();
        Card card = prometheusController.generateCard();
        deck.addCard(card);
        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        prometheusController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);

        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0), 1);
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(1, 2));
        fakeGameController.getGame().getBoard().getCell(2, 2).setBuildLevel(1);

        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        prometheusController.activeWorker = worker;

        ArrayList<Cell> expectedArrayList = fakeGameController.getGame().getBoard().getNeighbors(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition());
        expectedArrayList.remove(fakeGameController.getGame().getBoard().getCell(2, 2));
        assertEquals(prometheusController.findPossibleMovesNoUp(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition()), expectedArrayList);
    }

    public class PrometheusGameController extends FakeGameController {

        public PrometheusGameController(VirtualView client, int num, String gameName) {
            super(client, num, gameName);
        }

        @Override
        public void gameSetUp() {
            Deck deck = game.getDeck();
            deck.addCard(prometheusController.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(prometheusController);

            try {
                placeWorkers();
                placeBuildings();
                playGame();
            } catch (IOExceptionFromController e) {
                //
            }
        }

        private void placeWorkers() {
            Worker worker = new Worker(players.get(0), 1);
            worker.setPosition(game.getBoard().getCell(1, 2));
            players.get(0).addWorker(worker);
        }

        private void placeBuildings() {
            game.getBoard().getCell(2, 2).setBuildLevel(1);
        }

        @Override
        public void logError(String message) {
            //
        }
    }
}