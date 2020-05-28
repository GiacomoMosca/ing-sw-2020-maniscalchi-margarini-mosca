package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.FakeGameController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.exceptions.IllegalMoveException;
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

public class ArtemisControllerTest {

    private ArtemisController artemisController = null;
    private ArtemisGameController fakeGameController = null;
    private FakeVirtualView fakeVirtualView, fakeVirtualView1;
    private Socket socket, socket1;
    private ObjectInputStream ois, objectInputStream1;
    private ObjectOutputStream ous, objectOutputStream1;

    @Before
    public void setUp() throws Exception {
        //it's not okay to call fakeGameController.gameSetUp() for these tests because it would play a turn and we couldn't check what we need
        socket = new Socket();
        fakeVirtualView = new FakeVirtualView(socket, ois, ous);
        fakeGameController = new ArtemisGameController(fakeVirtualView, 1, "ArtemisTest");
        artemisController = new ArtemisController(fakeGameController);
        Deck deck = fakeGameController.getGame().getDeck();
        Card card = artemisController.generateCard();
        deck.addCard(card);
        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        artemisController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);

        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0), 1);
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(1, 2));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
    }

    @Test
    public void generateCard_noInputGiven_shouldReturnTheGodCard() {
        Card testCard = new Card("Artemis",
                "Goddess of the Hunt",
                "Your Move: Your Worker may move one additional time, but not back to the space it started on.",
                1,
                false,
                artemisController);
        assertEquals(artemisController.generateCard(), testCard);
    }

        /*@After
        public void tearDown() throws Exception {
        }*/

    @Test
    public void runPhases_workerGiven_shouldReturnWonAfterTheFirstMove() throws Exception {
        fakeGameController.getGame().getBoard().getCell(1, 2).setBuildLevel(2);
        fakeGameController.getGame().getBoard().getCell(0, 1).setBuildLevel(3);

        assertEquals(artemisController.runPhases(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0)), "winConditionAchieved");

    }

    @Test
    public void runPhasesAndFindPossibleMoves_workerGivenAndNoInputGiven_shouldReturnWonAfterTheSecondMoveAndArrayListOfAllNeighbors() throws Exception {
        fakeGameController.getGame().getBoard().getCell(1, 2).setBuildLevel(2);
        fakeGameController.getGame().getBoard().getCell(0, 1).setBuildLevel(2);
        fakeGameController.getGame().getBoard().getCell(0, 0).setBuildLevel(3);

        assertEquals(artemisController.runPhases(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0)), "winConditionAchieved");
        ArrayList<Cell> expectedArrayList = fakeGameController.getGame().getBoard().getNeighbors(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition());
        assertEquals(artemisController.findLegalMoves(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition(), expectedArrayList), expectedArrayList);
    }

    @Test
    public void runPhases_workerGiven_shouldNotAcceptToMoveTheSecondTime() throws Exception {
        //a client who chooses not to move the second time
        class FakeVirtualViewToAnswerNo extends FakeVirtualView {
            public FakeVirtualViewToAnswerNo(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
                super(socket, objectInputStream, objectOutputStream);
            }

            @Override
            public boolean chooseYesNo(String query) {
                return false;
            }
        }
        socket = new Socket();
        fakeVirtualView = new FakeVirtualViewToAnswerNo(socket, ois, ous);
        fakeGameController = new ArtemisGameController(fakeVirtualView, 1, "ArtemisTest");
        artemisController = new ArtemisController(fakeGameController);
        artemisController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);
        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0), 1);
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(0, 0));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);

        artemisController.runPhases(worker);
    }

    @Test
    public void movePhase_noInputGiven_shouldGenerateExceptionIllegalMove() throws Exception {
        //a client who tries to move in a domed cell
        class FakeVirtualViewToGenerateException extends FakeVirtualView {
            public FakeVirtualViewToGenerateException(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
                super(socket, objectInputStream, objectOutputStream);
            }

            @Override
            public Cell chooseMovePosition(ArrayList<Cell> possibleMoves) {
                return (fakeGameController.getGame().getBoard().getCell(1, 1));
            }
        }

        //new initialization needed to use FakeVirtualViewToGenerateException instead of FakeVirtualView
        socket1 = new Socket();
        fakeVirtualView1 = new FakeVirtualViewToGenerateException(socket1, objectInputStream1, objectOutputStream1);
        fakeGameController = new ArtemisGameController(fakeVirtualView1, 1, "ArtemisTest");
        artemisController = new ArtemisController(fakeGameController);
        artemisController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView1);
        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0), 1);
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(1, 2));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        artemisController.activeWorker = worker;
        fakeGameController.getGame().getBoard().getCell(1, 1).buildDome();

        artemisController.movePhase();
    }

    public class ArtemisGameController extends FakeGameController {

        public ArtemisGameController(VirtualView client, int num, String gameName) {
            super(client, num, gameName);
        }

        @Override
        public void gameSetUp() {
            Deck deck = game.getDeck();
            deck.addCard(artemisController.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(artemisController);

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

    }
}
