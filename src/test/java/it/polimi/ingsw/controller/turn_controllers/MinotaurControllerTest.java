package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.PlayerController;
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

import static org.junit.Assert.*;

public class MinotaurControllerTest {
    MinotaurController minotaurController;
    FakeGameController fakeGameController;
    FakeVirtualView fakeVirtualView1, fakeVirtualView2;
    Socket socket1, socket2;
    ObjectInputStream ois1, ois2;
    ObjectOutputStream ous1, ous2;

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
            deck.addCard(minotaurController.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(minotaurController);

            placeWorkers();
            placeBuildings();
            playGame();
        }

        private void placeWorkers() {
            Worker worker = new Worker(players.get(0));
            worker.setPosition(game.getBoard().getCell(1, 2));
            players.get(0).addWorker(worker);
        }

        private void placeBuildings() {
            game.getBoard().getCell(3, 3).setBuildLevel(1);
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
        socket1=new Socket();
        fakeVirtualView1=new FakeVirtualView(socket1, ois1, ous1);
        fakeVirtualView1.setId("MinotaurTest");
        fakeGameController=new FakeGameController(fakeVirtualView1, 2);
        minotaurController=new MinotaurController(fakeGameController);

        GodControllerConcrete genericController=new GodControllerConcrete(fakeGameController);
        socket2=new Socket();
        fakeVirtualView2=new FakeVirtualView(socket2, ois2, ous2);
        fakeVirtualView2.setId("AdditionalPlayer");

        Player player2 = new Player(fakeVirtualView2.getId(), "color");
        PlayerController playerController = new PlayerController(player2, fakeVirtualView2);
        fakeGameController.getGame().addPlayer(player2);

        Deck deck = fakeGameController.getGame().getDeck();
        Card card = minotaurController.generateCard();
        deck.addCard(card);
        deck.addCard(new Card("god", "title", "description", 1, false, genericController));

        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        minotaurController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView1);
        fakeGameController.getGame().getPlayers().get(1).setGodCard(deck.getCards().get(1));
        genericController.setPlayer(fakeGameController.getGame().getPlayers().get(1), fakeVirtualView2);

        Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(0,0));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        minotaurController.activeWorker=worker;

        Worker worker2=new Worker(fakeGameController.getGame().getPlayers().get(1));
        worker2.setPosition(fakeGameController.getGame().getBoard().getCell(0, 1));
        fakeGameController.getGame().getPlayers().get(1).addWorker(worker2);
        genericController.activeWorker=worker2;
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void generateCard_noInputGiven_shouldReturnTheGodCard() {
        Card testCard=new Card("Minotaur", "Bull-headed Monster", "Your Move: Your Worker may move into an opponent Worker’s space (using normal movement rules), if the next space in the same direction is unoccupied. Their Worker is forced into that space (regardless of its level).", 1, false, minotaurController);
        assertEquals(minotaurController.generateCard().getGod(), testCard.getGod());
        assertEquals(minotaurController.generateCard().getTitle(), testCard.getTitle());
        assertEquals(minotaurController.generateCard().getDescription(), testCard.getDescription());
        assertEquals(minotaurController.generateCard().getSet(), testCard.getSet());
        assertEquals(minotaurController.generateCard().hasAlwaysActiveModifier(), testCard.hasAlwaysActiveModifier());
        assertEquals(minotaurController.generateCard().getController(), testCard.getController());
    }

    @Test
    public void movePhase_noInputGiven_shouldMoveTheWorkersPushingAwayTheOpponent() throws IOException, ClassNotFoundException {
        minotaurController.movePhase();
        assertTrue(fakeGameController.getGame().getBoard().getCell(0,1).getWorker().equals(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0)));
        assertTrue(fakeGameController.getGame().getBoard().getCell(0,2).getWorker().equals(fakeGameController.getGame().getPlayers().get(1).getWorkers().get(0)));
    }

    @Test
    public void findPossibleMoves_workerPositionGiven_shouldReturnAnArrayListWithAllTheNeighbors() {
        assertEquals(fakeGameController.getGame().getBoard().getNeighbors(fakeGameController.getGame().getBoard().getCell(0,0)), minotaurController.findPossibleMoves(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition()));
    }

    @Test
    public void movePhase_noInputGiven_shouldGenerateTwoMovingExceptions() throws IOException, ClassNotFoundException {
        //a client who chooses to move in an illegal cell
        class FakeVirtualViewToGenerateException extends FakeVirtualView {
            public FakeVirtualViewToGenerateException(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream){
                super(socket, objectInputStream, objectOutputStream);
            }
            @Override
            public Cell chooseMovePosition(ArrayList<Cell> possibleMoves){
                return(fakeGameController.getGame().getBoard().getCell(2,2));
            }
            @Override
            public Cell chooseBuildPosition(ArrayList<Cell> possibleMoves){
                return(fakeGameController.getGame().getBoard().getCell(3,3));
            }
        }

        socket1=new Socket();
        fakeVirtualView1=new FakeVirtualViewToGenerateException(socket1, ois1, ous1);
        fakeVirtualView1.setId("MinotaurTestToGenerateException");
        fakeGameController=new FakeGameController(fakeVirtualView1, 1);
        minotaurController=new MinotaurController(fakeGameController);
        minotaurController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView1);
        Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(0,0));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        minotaurController.activeWorker=worker;
        fakeGameController.getGame().getBoard().getCell(2,2).buildDome();

        //to generate the first moving exception
        minotaurController.movePhase();

        //to generate the second moving exception
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(2,2));
        minotaurController.movePhase();
    }
}