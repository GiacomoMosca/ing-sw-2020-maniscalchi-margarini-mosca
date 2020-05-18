package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.FakeGameController;
import it.polimi.ingsw.controller.PlayerController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
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

import static org.junit.Assert.assertEquals;

public class MedusaControllerTest<MedudsaController> {

    private MedusaController medusaController;
    private GodController genericController;
    private MedusaGameController fakeGameController;
    private FakeVirtualView fakeVirtualView1, fakeVirtualView2;
    private Socket socket1, socket2;
    private ObjectInputStream objectInputStream1, objectInputStream2;
    private ObjectOutputStream objectOutputStream1, objectOutputStream2;

    @Before
    public void setUp() throws Exception {
        socket1 = new Socket();
        fakeVirtualView1 = new FakeVirtualView(socket1, objectInputStream1, objectOutputStream1);
        fakeGameController = new MedusaGameController(fakeVirtualView1, 2, "MedusaTest");
        medusaController = new MedusaController(fakeGameController);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void generateCard_noInputGiven_shouldReturnTheGodCard() {
        Card testCard = new Card("Medusa",
                "Petrifying Gorgon",
                "End of Your Turn: If any of your opponent’s Workers occupy lower neighboring spaces, replace them all with blocks and remove them from the game.",
                2,
                false,
                medusaController);
        assertEquals(medusaController.generateCard(), testCard);
    }

    @Test
    public void runPhases_workerGiven_shouldReturnWON() throws IOException, ClassNotFoundException, IOExceptionFromController {
        Deck deck = fakeGameController.getGame().getDeck();
        Card card = medusaController.generateCard();
        deck.addCard(card);
        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        medusaController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView1);
        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0), 1);
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(1, 1));
        fakeGameController.getGame().getBoard().getCell(0, 0).setBuildLevel(3);
        fakeGameController.getGame().getBoard().getCell(1, 1).setBuildLevel(2);
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        medusaController.activeWorker = worker;

        assertEquals(medusaController.runPhases(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0)), "winConditionAchieved");
    }

    @Test
    public void runPhases_workerGiven_shouldReturnNextAndBuildWhereAnOpponentWorkerStood() throws IOException, ClassNotFoundException, IOExceptionFromController {
        socket2 = new Socket();
        genericController = new GodControllerConcrete(fakeGameController);
        fakeVirtualView2 = new FakeVirtualView(socket2, objectInputStream2, objectOutputStream2);
        Player player2 = new Player(fakeVirtualView2.getId(), "color");
        PlayerController playerController = new PlayerController(player2, fakeVirtualView2, fakeGameController);
        fakeGameController.getGame().addPlayer(player2);
        Deck deck = fakeGameController.getGame().getDeck();
        Card card = medusaController.generateCard();
        deck.addCard(card);
        deck.addCard(new Card("god", "title", "description", 1, false, genericController));
        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        medusaController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView1);
        fakeGameController.getGame().getPlayers().get(1).setGodCard(deck.getCards().get(1));
        genericController.setPlayer(fakeGameController.getGame().getPlayers().get(1), fakeVirtualView2);
        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0), 1);
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(2, 2));
        fakeGameController.getGame().getBoard().getCell(1, 1).setBuildLevel(1);
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        medusaController.activeWorker = worker;
        Worker worker2 = new Worker(fakeGameController.getGame().getPlayers().get(1), 1);
        worker2.setPosition(fakeGameController.getGame().getBoard().getCell(0, 0));
        fakeGameController.getGame().getPlayers().get(1).addWorker(worker2);

        assertEquals(medusaController.runPhases(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0)), "next");
        assertEquals(fakeGameController.getGame().getBoard().getCell(0, 0).getBuildLevel(), 1);
    }

    @Test
    public void runPhases_workerGiven_shouldGenerateIllegalBuildException() throws IOException, ClassNotFoundException, IOExceptionFromController {
        socket2 = new Socket();
        genericController = new GodControllerConcrete(fakeGameController);
        fakeVirtualView2 = new FakeVirtualView(socket2, objectInputStream2, objectOutputStream2);
        Player player2 = new Player(fakeVirtualView2.getId(), "color");
        PlayerController playerController = new PlayerController(player2, fakeVirtualView2, fakeGameController);
        fakeGameController.getGame().addPlayer(player2);
        Deck deck = fakeGameController.getGame().getDeck();
        Card card = medusaController.generateCard();
        deck.addCard(card);
        deck.addCard(new Card("god", "title", "description", 1, false, genericController));
        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        medusaController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView1);
        fakeGameController.getGame().getPlayers().get(1).setGodCard(deck.getCards().get(1));
        genericController.setPlayer(fakeGameController.getGame().getPlayers().get(1), fakeVirtualView2);
        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0), 1);
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(2, 2));
        fakeGameController.getGame().getBoard().getCell(1, 1).setBuildLevel(1);
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        medusaController.activeWorker = worker;
        Worker worker2 = new Worker(fakeGameController.getGame().getPlayers().get(1), 1);
        worker2.setPosition(fakeGameController.getGame().getBoard().getCell(0, 0));
        fakeGameController.getGame().getBoard().getCell(0, 0).buildDome();
        fakeGameController.getGame().getPlayers().get(1).addWorker(worker2);

        medusaController.runPhases(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0));
    }

    public class MedusaGameController extends FakeGameController {

        public MedusaGameController(VirtualView client, int num, String gameName) {
            super(client, num, gameName);
        }

        @Override
        public void gameSetUp() {
            Deck deck = game.getDeck();
            deck.addCard(medusaController.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(medusaController);

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
            game.getBoard().getCell(1, 1).setBuildLevel(2);
            players.get(0).addWorker(worker);
        }

        private void placeBuildings() {
            game.getBoard().getCell(0, 0).setBuildLevel(3);
            game.getBoard().getCell(0, 1).buildDome();
            game.getBoard().getCell(0, 2).buildDome();
            game.getBoard().getCell(1, 0).buildDome();
            game.getBoard().getCell(1, 2).buildDome();
            game.getBoard().getCell(2, 0).buildDome();
            game.getBoard().getCell(2, 1).buildDome();
            game.getBoard().getCell(2, 2).buildDome();
        }

    }
}