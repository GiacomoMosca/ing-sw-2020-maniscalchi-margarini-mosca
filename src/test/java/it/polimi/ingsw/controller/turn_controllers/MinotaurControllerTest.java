package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.FakeGameController;
import it.polimi.ingsw.controller.PlayerController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
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

import static org.junit.Assert.assertEquals;

public class MinotaurControllerTest {
    private MinotaurController minotaurController;
    private MinotaurGameController fakeGameController;
    private FakeVirtualView fakeVirtualView1, fakeVirtualView2;
    private Socket socket1, socket2;
    private ObjectInputStream objectInputStream1, objectInputStream2;
    private ObjectOutputStream objectOutputStream1, objectOutputStream2;

    @Before
    public void setUp() throws Exception {
        socket1 = new Socket();
        fakeVirtualView1 = new FakeVirtualView(socket1, objectInputStream1, objectOutputStream1);
        fakeGameController = new MinotaurGameController(fakeVirtualView1, 2, "MinotaurTest");
        minotaurController = new MinotaurController(fakeGameController);

        GodControllerConcrete genericController = new GodControllerConcrete(fakeGameController);
        socket2 = new Socket();
        fakeVirtualView2 = new FakeVirtualView(socket2, objectInputStream2, objectOutputStream2);

        Player player2 = new Player(fakeVirtualView2.getId(), "color");
        PlayerController playerController = new PlayerController(player2, fakeVirtualView2, fakeGameController);
        fakeGameController.getGame().addPlayer(player2);

        Deck deck = fakeGameController.getGame().getDeck();
        Card card = minotaurController.generateCard();
        deck.addCard(card);
        deck.addCard(new Card("god", "title", "description", 1, false, genericController));

        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        minotaurController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView1);
        fakeGameController.getGame().getPlayers().get(1).setGodCard(deck.getCards().get(1));
        genericController.setPlayer(fakeGameController.getGame().getPlayers().get(1), fakeVirtualView2);

        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0), 1);
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(3, 3));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        minotaurController.activeWorker = worker;

        Worker worker2 = new Worker(fakeGameController.getGame().getPlayers().get(1), 1);
        worker2.setPosition(fakeGameController.getGame().getBoard().getCell(2, 2));
        fakeGameController.getGame().getPlayers().get(1).addWorker(worker2);
        genericController.activeWorker = worker2;

        Worker worker3 = new Worker(fakeGameController.getGame().getPlayers().get(1), 2);
        worker3.setPosition(fakeGameController.getGame().getBoard().getCell(2, 3));
        fakeGameController.getGame().getPlayers().get(1).addWorker(worker3);

        Worker worker4 = new Worker(fakeGameController.getGame().getPlayers().get(1), 3);
        worker4.setPosition(fakeGameController.getGame().getBoard().getCell(2, 4));
        fakeGameController.getGame().getPlayers().get(1).addWorker(worker3);

        fakeGameController.getGame().getBoard().getCell(1, 3).buildDome();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void generateCard_noInputGiven_shouldReturnTheGodCard() {
        Card testCard = new Card("Minotaur",
                "Bull-headed Monster",
                "Your Move: Your Worker may move into an opponent Worker’s space (using normal movement rules), if the next space in the same direction is unoccupied. Their Worker is forced into that space (regardless of its level).",
                1,
                false,
                minotaurController);
        assertEquals(minotaurController.generateCard(), testCard);
    }

    @Test
    public void movePhase_noInputGiven_shouldMoveTheWorkerPushingAwayTheOpponent() throws IOException, ClassNotFoundException, IOExceptionFromController {
        minotaurController.movePhase();
        assertEquals(fakeGameController.getGame().getBoard().getCell(2, 2).getWorker(), fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0));
        assertEquals(fakeGameController.getGame().getBoard().getCell(1, 1).getWorker(), fakeGameController.getGame().getPlayers().get(1).getWorkers().get(0));
    }

    @Test
    public void findPossibleMoves_workerPositionGiven_shouldReturnAnArrayListWithAllTheNeighbors() {
        ArrayList<Cell> goodNeighbors = fakeGameController.getGame().getBoard().getNeighbors(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition());
        goodNeighbors.remove(fakeGameController.getGame().getBoard().getCell(2, 3));
        goodNeighbors.remove(fakeGameController.getGame().getBoard().getCell(2, 4));
        assertEquals(minotaurController.findPossibleMoves(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition()), goodNeighbors);
    }

    @Test
    public void movePhase_noInputGiven_shouldGenerateTwoInvalidMoveExceptions() throws IOException, ClassNotFoundException, IOExceptionFromController {
        //a client who chooses to move in an illegal cell
        class FakeVirtualViewToGenerateException extends FakeVirtualView {
            public FakeVirtualViewToGenerateException(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
                super(socket, objectInputStream, objectOutputStream);
            }

            @Override
            public Cell chooseMovePosition(ArrayList<Cell> possibleMoves) {
                return (fakeGameController.getGame().getBoard().getCell(2, 2));
            }

            @Override
            public Cell chooseBuildPosition(ArrayList<Cell> possibleMoves) {
                return (fakeGameController.getGame().getBoard().getCell(3, 3));
            }
        }

        socket1 = new Socket();
        fakeVirtualView1 = new FakeVirtualViewToGenerateException(socket1, objectInputStream1, objectOutputStream1);
        fakeGameController = new MinotaurGameController(fakeVirtualView1, 1, "MinotaurTest");
        minotaurController = new MinotaurController(fakeGameController);
        minotaurController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView1);
        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0), 1);
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(0, 0));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        minotaurController.activeWorker = worker;
        fakeGameController.getGame().getBoard().getCell(2, 2).buildDome();

        //to generate the first moving exception
        minotaurController.movePhase();

        //to generate the second moving exception
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(2, 2));
        minotaurController.movePhase();
    }

    public class MinotaurGameController extends FakeGameController {

        public MinotaurGameController(VirtualView client, int num, String gameName) {
            super(client, num, gameName);
        }

        @Override
        public void gameSetUp() {
            Deck deck = game.getDeck();
            deck.addCard(minotaurController.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(minotaurController);

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