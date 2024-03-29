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

import static org.junit.Assert.assertEquals;

public class HephaestusControllerTest {
    private HephaestusController hephaestusController = null;
    private HephaestusGameController fakeGameController = null;
    private FakeVirtualView fakeVirtualView;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    @Before
    public void setUp() throws Exception {
        socket = new Socket();
        fakeVirtualView = new FakeVirtualView(socket, objectInputStream, objectOutputStream);
        fakeGameController = new HephaestusGameController(fakeVirtualView, 1, "HephaestusTest");
        hephaestusController = new HephaestusController(fakeGameController);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void generateCard_noInputGiven_shouldReturnTheGodCard() {
        Card testCard = new Card("Hephaestus",
                "God of Blacksmiths",
                "Your Build: Your Worker may build one additional block (not dome) " +
                        "\non top of your first block.",
                1,
                false,
                hephaestusController);
        assertEquals(hephaestusController.generateCard(), testCard);
    }

    @Test
    public void buildPhase_noInputGiven_shouldBuildTwoTimes() {
        fakeGameController.gameSetUp();
        assertEquals(fakeGameController.getGame().getBoard().getCell(0, 0).getBuildLevel(), 2);
    }

    @Test
    public void buildPhase_noInputGiven_shouldGenerateTwoBuildingExceptions() throws Exception {
        //a client who chooses to build twice in a domed cell
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
        fakeGameController = new HephaestusGameController(fakeVirtualView, 1, "HephaestusTest");
        hephaestusController = new HephaestusController(fakeGameController);
        hephaestusController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);
        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0), 1);
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(0, 0));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        hephaestusController.activeWorker = worker;
        fakeGameController.getGame().getBoard().getCell(2, 2).buildDome();

        hephaestusController.buildPhase();
    }

    public class HephaestusGameController extends FakeGameController {

        public HephaestusGameController(VirtualView client, int num, String gameName) {
            super(client, num, gameName);
        }

        @Override
        public void gameSetUp() {
            Deck deck = game.getDeck();
            deck.addCard(hephaestusController.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(hephaestusController);

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
            worker.setPosition(game.getBoard().getCell(2, 2));
            players.get(0).addWorker(worker);
        }

        private void placeBuildings() {
            game.getBoard().getCell(3, 3).setBuildLevel(1);
        }

        @Override
        public void logError(String message) {
            //
        }
    }
}