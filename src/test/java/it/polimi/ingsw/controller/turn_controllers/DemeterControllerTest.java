package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.FakeGameController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.view.FakeVirtualView;
import it.polimi.ingsw.view.VirtualView;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class DemeterControllerTest {

    private DemeterController demeterController;
    private DemeterGameController fakeGameController;
    private FakeVirtualView fakeVirtualView;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    @Before
    public void setUp() throws Exception {
        socket = new Socket();
        fakeVirtualView = new FakeVirtualView(socket, objectInputStream, objectOutputStream);
        fakeGameController = new DemeterGameController(fakeVirtualView, 1, "DemeterTest");
        demeterController = new DemeterController(fakeGameController);
    }

    @Test
    public void generateCard_noInputGiven_shouldReturnTheGodCard() {
        Card testCard = new Card(
                "Demeter",
                "Goddess of the Harvest",
                "Your Build: Your Worker may build one additional time, " +
                        "\nbut not on the same space.",
                1,
                false,
                demeterController);
        assertEquals(demeterController.generateCard(), testCard);
    }

    /*@After
    public void tearDown() throws Exception {
    }*/

    @Test
    public void buildPhase_noInputGiven_shouldBuildTwoTimesInTwoDifferentCells() {
        //after GameSetUp, the worker will be in (0,1). He built one time in (0,0) and one time in (0,2),
        //since the cell (0,0) has been removed from the possibleBuilds.
        // 0,0 - (0,1) - 0,2
        // 1,0 -  1,1  - 1,2

        fakeGameController.gameSetUp();
        assertEquals(fakeGameController.getGame().getBoard().getCell(0, 0).getBuildLevel(), 1);
        assertEquals(fakeGameController.getGame().getBoard().getCell(0, 2).getBuildLevel(), 1);
    }

    @Test
    public void buildPhase_noInputGiven_shouldGenerateTwoIllegalBuildExceptions() throws Exception {
        //a client who chooses to build two times in an illegal cell
        class FakeVirtualViewToGenerateException extends FakeVirtualView {
            public FakeVirtualViewToGenerateException(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
                super(socket, objectInputStream, objectOutputStream);
            }

            @Override
            public Cell chooseBuildPosition(ArrayList<Cell> possibleMoves) {
                return (fakeGameController.getGame().getBoard().getCell(2, 2));
            }
        }
        //need new initialization to use FakeVirtualViewToGenerateException instead of FakeVirtualView
        socket = new Socket();
        fakeVirtualView = new FakeVirtualViewToGenerateException(socket, objectInputStream, objectOutputStream);
        fakeGameController = new DemeterGameController(fakeVirtualView, 1, "DemeterTest");
        demeterController = new DemeterController(fakeGameController);
        demeterController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);
        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0), 1);
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(0, 0));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        demeterController.activeWorker = worker;
        fakeGameController.getGame().getBoard().getCell(2, 2).buildDome();

        demeterController.buildPhase();
    }

    public class DemeterGameController extends FakeGameController {

        public DemeterGameController(VirtualView client, int num, String gameName) {
            super(client, num, gameName);
        }

        @Override
        public void gameSetUp() {
            Deck deck = game.getDeck();
            deck.addCard(demeterController.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(demeterController);

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
            game.getBoard().getCell(3, 3).setBuildLevel(1);
        }

        @Override
        public void logError(String message) {
            //
        }
    }
}
