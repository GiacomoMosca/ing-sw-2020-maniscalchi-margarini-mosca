package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.PlayerController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.view.FakeVirtualView;
import it.polimi.ingsw.view.VirtualView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static org.junit.Assert.*;

public class PanControllerTest {

    PanController panController;
    FakeGameController fakeGameController;
    FakeVirtualView fakeVirtualView;
    Socket socket;
    ObjectInputStream ois;
    ObjectOutputStream ous;

    public class FakeGameController extends GameController {

        public FakeGameController(VirtualView client, int num) {
            super(client, num);
        }

        @Override
        public void addPlayer(VirtualView client) {
            Player player = new Player(client.getId(), colors.get(playerControllers.size()));
            PlayerController playerController = new PlayerController(player, client);
            game.addPlayer(player);
            playerControllers.add(playerController);
            gameSetUp();
        }

        @Override
        public void gameSetUp() {

            Deck deck = game.getDeck();
            deck.addCard(panController.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(panController);

            placeWorkers();
            placeBuildings();
            playGame();
        }

        private void placeWorkers() {
            Worker worker = new Worker(players.get(0));
            worker.setPosition(game.getBoard().getCell(1, 2));
            game.getBoard().getCell(1, 2).setBuildLevel(2);

            players.get(0).addWorker(worker);
        }

        private void placeBuildings() {
            game.getBoard().getCell(0, 1).setBuildLevel(0);
        }

        public void playGame() {
            String result = playerControllers.get(game.getActivePlayer()).playTurn();
            if (result.equals("WON"))
                game.setWinner(players.get(game.getActivePlayer()));
        }

        @Override
        public void broadcastBoard() {
        }
    }

    @Before
    public void setUp() throws Exception {
        socket=new Socket();
        fakeVirtualView=new FakeVirtualView(socket, ois, ous);
        fakeVirtualView.setId("PanTest");
        fakeGameController=new FakeGameController(fakeVirtualView, 1);
        panController=new PanController(fakeGameController);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void generateCard_noInputGiven_shouldReturnTheGodCard() {
        Card testCard=new Card("Pan", "God of the Wild", "Win Condition: You also win if your Worker moves down two or more levels.", 1, false, panController);
        assertEquals(panController.generateCard().getGod(), testCard.getGod());
        assertEquals(panController.generateCard().getTitle(), testCard.getTitle());
        assertEquals(panController.generateCard().getDescription(), testCard.getDescription());
        assertEquals(panController.generateCard().getSet(), testCard.getSet());
        assertEquals(panController.generateCard().hasAlwaysActiveModifier(), testCard.hasAlwaysActiveModifier());
        assertEquals(panController.generateCard().getController(), testCard.getController());
    }

    @Test
    public void checkWin_noInputGiven_shouldReturnTrue() {
        fakeGameController.gameSetUp();
        assertTrue(panController.checkWin());
    }
}