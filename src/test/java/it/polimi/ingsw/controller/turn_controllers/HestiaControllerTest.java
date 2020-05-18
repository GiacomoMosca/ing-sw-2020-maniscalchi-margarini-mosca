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

public class HestiaControllerTest {
    private HestiaController hestiaController = null;
    private HestiaGameController fakeGameController = null;
    private FakeVirtualView fakeVirtualView;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    @Before
    public void setUp() throws Exception {
        socket = new Socket();
        fakeVirtualView = new FakeVirtualView(socket, objectInputStream, objectOutputStream);
        fakeGameController = new HestiaGameController(fakeVirtualView, 1, "HestiaTest");
        hestiaController = new HestiaController(fakeGameController);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void generateCard_noInputGiven_shouldReturnTheGodCard() {
        Card testCard = new Card("Hestia",
                "Goddess of Hearth and Home",
                "Your Build: Your Worker may build one additional time. The additional build cannot be on a perimeter space.",
                2,
                false,
                hestiaController);
        assertEquals(hestiaController.generateCard(), testCard);
    }

    @Test
    public void runPhases_workerGiven_shouldReturnWin() throws IOException, ClassNotFoundException, IOExceptionFromController {
        Deck deck = fakeGameController.getGame().getDeck();
        Card card = hestiaController.generateCard();
        deck.addCard(card);
        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        hestiaController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);
        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0), 1);
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(1, 2));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);

        fakeGameController.getGame().getBoard().getCell(1, 2).setBuildLevel(2);
        fakeGameController.getGame().getBoard().getCell(0, 1).setBuildLevel(3);

        assertEquals(hestiaController.runPhases(worker), "winConditionAchieved");
    }

    @Test
    public void runPhases_workerGiven_shouldBuildTwoTimesAndReturnNext() {
        //checking that the first building is on (0,0) and the second building on (1,2)
        //the worker starts from (2,2), moves to (1,1), builds the first time on (0,0) and the second time on (1,2)
        //because it's the first not-perimeter-cell

        //calling gameSetUp() will later invoke runPhases, covering the case of return "next"
        fakeGameController.gameSetUp();
        assertEquals(fakeGameController.getGame().getBoard().getCell(0, 0).getBuildLevel(), 1);
        assertEquals(fakeGameController.getGame().getBoard().getCell(1, 2).getBuildLevel(), 1);
    }

    @Test
    public void buildPhase_noInputGiven_shouldGenerateIllegalBuildException() throws IOException, ClassNotFoundException, IOExceptionFromController {
        //a client who chooses to build in an illegal cell
        class FakeVirtualViewToGenerateException extends FakeVirtualView {
            public FakeVirtualViewToGenerateException(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
                super(socket, objectInputStream, objectOutputStream);
            }

            @Override
            public Cell chooseBuildPosition(ArrayList<Cell> possibleBuilds) {
                return (fakeGameController.getGame().getBoard().getCell(2, 2));
            }
        }
        //need new initialization to use FakeVirtualViewToGenerateException instead of FakeVirtualView
        socket = new Socket();
        fakeVirtualView = new FakeVirtualViewToGenerateException(socket, objectInputStream, objectOutputStream);
        fakeGameController = new HestiaGameController(fakeVirtualView, 1, "HestiaTest");
        hestiaController = new HestiaController(fakeGameController);
        hestiaController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);
        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0), 1);
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(0, 0));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        hestiaController.activeWorker = worker;
        fakeGameController.getGame().getBoard().getCell(2, 2).buildDome();

        hestiaController.buildPhase();
    }

    @Test
    public void findPossibleBuilds_workerPositionAndPossibleBuildsGiven_shouldReturnTheSameArrayListOfCells() {
        fakeGameController.gameSetUp();
        ArrayList<Cell> expectedArrayList = fakeGameController.getGame().getBoard().getNeighbors(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition());
        assertEquals(hestiaController.findLegalMoves(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition(), expectedArrayList), expectedArrayList);
    }

    public class HestiaGameController extends FakeGameController {

        public HestiaGameController(VirtualView client, int num, String gameName) {
            super(client, num, gameName);
        }

        @Override
        public void gameSetUp() {
            Deck deck = game.getDeck();
            deck.addCard(hestiaController.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(hestiaController);

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

    }
}