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

public class LimusControllerTest {

    private LimusController limusController;
    private LimusGameController fakeGameController;
    private FakeVirtualView fakeVirtualView;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    @Before
    public void setUp() throws Exception {
        socket = new Socket();
        fakeVirtualView = new FakeVirtualView(socket, objectInputStream, objectOutputStream);
        fakeGameController = new LimusGameController(fakeVirtualView, 1, "LimusTest");
        limusController = new LimusController(fakeGameController);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void generateCard_noInputGiven_shouldReturnTheGodCard() {
        Card testCard = new Card("Limus",
                "Goddess of Famine",
                "Opponent’s Turn: Opponent Workers cannot build on spaces neighboring " +
                        "\nyour Workers, unless building a dome to create a Complete Tower.",
                2,
                true,
                limusController);
        assertEquals(limusController.generateCard(), testCard);
    }

    @Test
    public void limitBuilds_workerPositionAndArrayListOfCellsGiven_shouldReturnAnArrayListOfCellNotContainingTheCellNeighboringLimus() {
        //supposing that a worker (not even created) stands on (0,0). his possible builds should be (1,0), (1,1), (0,1), but
        //a Limus worker stands on (0,2) and this limits the building possibilities of the worker on (0,0): only (1.0)!
        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0), 1);
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(0, 2));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        limusController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);

        ArrayList<Cell> possibleBuildsAfterLimusPower = new ArrayList<Cell>();
        possibleBuildsAfterLimusPower.add(fakeGameController.getGame().getBoard().getCell(1, 0));
        fakeGameController.getGame().getBoard().getCell(0, 1).setBuildLevel(1);
        fakeGameController.getGame().getBoard().getCell(1, 0).setBuildLevel(1);
        ArrayList<Cell> possibleBuildsBeforeLimusPower = fakeGameController.getGame().getBoard().getNeighbors(fakeGameController.getGame().getBoard().getCell(0, 0)); //0,1 - 1,0 - 1,1

        assertEquals(limusController.limitBuilds(fakeGameController.getGame().getBoard().getCell(0, 0), possibleBuildsBeforeLimusPower), possibleBuildsAfterLimusPower);
    }

    public class LimusGameController extends FakeGameController {

        public LimusGameController(VirtualView client, int num, String gameName) {
            super(client, num, gameName);
        }

        @Override
        public void gameSetUp() {
            Deck deck = game.getDeck();
            deck.addCard(limusController.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(limusController);

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