package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.FakeGameController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.view.FakeVirtualView;
import it.polimi.ingsw.view.VirtualView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static org.junit.Assert.assertEquals;

public class PanControllerTest {

    private PanController panController;
    private PanGameController fakeGameController;
    private FakeVirtualView fakeVirtualView;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    @Before
    public void setUp() throws Exception {
        socket = new Socket();
        fakeVirtualView = new FakeVirtualView(socket, objectInputStream, objectOutputStream);
        fakeGameController = new PanGameController(fakeVirtualView, 1, "PanTest");
        panController = new PanController(fakeGameController);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void generateCard_noInputGiven_shouldReturnTheGodCard() {
        Card testCard = new Card("Pan",
                "God of the Wild",
                "Win Condition: You also win if your Worker moves down two or more levels.",
                1,
                false,
                panController);
        assertEquals(panController.generateCard(), testCard);
    }

    @Test
    public void checkWin_noInputGiven_shouldReturnTrue() {
        fakeGameController.gameSetUp();
        assertEquals(panController.checkWin(), "godConditionAchieved");
    }

    public class PanGameController extends FakeGameController {

        public PanGameController(VirtualView client, int num, String gameName) {
            super(client, num, gameName);
        }

        @Override
        public void gameSetUp() {
            Deck deck = game.getDeck();
            deck.addCard(panController.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(panController);

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
            game.getBoard().getCell(1, 2).setBuildLevel(2);

            players.get(0).addWorker(worker);
        }

        private void placeBuildings() {
            game.getBoard().getCell(0, 1).setBuildLevel(0);
        }

    }
}