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

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ZeusControllerTest {

    private ZeusController zeusController;
    private ZeusGameController fakeGameController;
    private FakeVirtualView fakeVirtualView;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    @Before
    public void setUp() throws Exception {
        socket = new Socket();
        fakeVirtualView = new FakeVirtualView(socket, objectInputStream, objectOutputStream);
        fakeGameController = new ZeusGameController(fakeVirtualView, 1, "ZeusTest");
        zeusController = new ZeusController(fakeGameController);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void generateCard_noInputGiven_shouldReturnTheGodCard() {
        Card testCard = new Card("Zeus",
                "God of the Sky",
                "Your Build: Your Worker may build under itself in its current space, " +
                        "\nforcing it up one level. You do not win by forcing yourself up " +
                        "\nto the third level.",
                2,
                false,
                zeusController);
        assertEquals(zeusController.generateCard(), testCard);
    }

    @Test
    public void findPossibleBuilds_workerPositionGiven_shouldReturnArrayListContainingAlsoThePositionOfTheWorker() {
        fakeGameController.gameSetUp();
        ArrayList<Cell> a = fakeGameController.getGame().getBoard().getNeighbors(fakeGameController.getGame().getBoard().getCell(0, 0));
        a.add(fakeGameController.getGame().getBoard().getCell(0, 0));
        assertEquals(zeusController.findPossibleBuilds(fakeGameController.getGame().getBoard().getCell(0, 0)), a);
    }

    public class ZeusGameController extends FakeGameController {

        public ZeusGameController(VirtualView client, int num, String gameName) {
            super(client, num, gameName);
        }

        @Override
        public void gameSetUp() {
            Deck deck = game.getDeck();
            deck.addCard(zeusController.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(zeusController);

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
            worker.setPosition(game.getBoard().getCell(1, 1));
            game.getBoard().getCell(1, 1).setBuildLevel(0);
            players.get(0).addWorker(worker);
        }

        private void placeBuildings() {
            game.getBoard().getCell(0, 0).setBuildLevel(1);
        }

        @Override
        public void logError(String message) {
            //
        }
    }
}