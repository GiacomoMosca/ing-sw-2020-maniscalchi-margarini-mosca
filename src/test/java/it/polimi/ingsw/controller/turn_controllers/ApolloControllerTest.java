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

public class ApolloControllerTest {

    ApolloController apolloController;
    GodControllerConcrete genericController;
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
            deck.addCard(apolloController.generateCard());
            deck.addCard(new Card("god", "title", "description", 1, false, genericController));

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(apolloController);

            placeWorkers();
            placeBuildings();
            playGame();
        }

        private void placeWorkers() {
            Worker worker1 = new Worker(players.get(0));
            worker1.setPosition(game.getBoard().getCell(1, 2));
            players.get(0).addWorker(worker1);
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
        //need two players to simulate swapping of their positions
        //it's not okay to call fakeGameController.gameSetUp(): it would play an entire round with the second player playing too
        socket1=new Socket();
        fakeVirtualView1=new FakeVirtualView(socket1, ois1, ous1);
        fakeVirtualView1.setId("ApolloTest");
        fakeGameController=new FakeGameController(fakeVirtualView1, 2);
        apolloController=new ApolloController(fakeGameController);

        socket2=new Socket();
        genericController=new GodControllerConcrete(fakeGameController);
        fakeVirtualView2=new FakeVirtualView(socket2, ois2, ous2);
        fakeVirtualView2.setId("AdditionalPlayer");

        Player player2 = new Player(fakeVirtualView2.getId(), "color");
        PlayerController playerController = new PlayerController(player2, fakeVirtualView2);
        fakeGameController.getGame().addPlayer(player2);

        Deck deck = fakeGameController.getGame().getDeck();
        Card card = apolloController.generateCard();
        deck.addCard(card);
        deck.addCard(new Card("god", "title", "description", 1, false, genericController));

        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        apolloController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView1);
        fakeGameController.getGame().getPlayers().get(1).setGodCard(deck.getCards().get(1));
        genericController.setPlayer(fakeGameController.getGame().getPlayers().get(1), fakeVirtualView2);

        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(1, 2));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        apolloController.activeWorker = worker;

        Worker worker2 = new Worker(fakeGameController.getGame().getPlayers().get(1));
        worker2.setPosition(fakeGameController.getGame().getBoard().getCell(0, 1));
        fakeGameController.getGame().getPlayers().get(1).addWorker(worker2);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void generateCard_noInputGiven_shouldReturnTheGodCard() {
        Card testCard = new Card("Apollo", "God of Music", "Your Move: Your Worker may move into an opponent Worker’s space (using normal movement rules) and force their Worker to the space yours just vacated (swapping their positions).", 1, false, apolloController);
        assertEquals(apolloController.generateCard().getGod(), testCard.getGod());
        assertEquals(apolloController.generateCard().getTitle(), testCard.getTitle());
        assertEquals(apolloController.generateCard().getDescription(), testCard.getDescription());
        assertEquals(apolloController.generateCard().getSet(), testCard.getSet());
        assertEquals(apolloController.generateCard().hasAlwaysActiveModifier(), testCard.hasAlwaysActiveModifier());
        assertEquals(apolloController.generateCard().getController(), testCard.getController());
    }

    @Test
    public void movePhase_noInputGiven_shouldSwapTheTwoWorkers() throws IOException, ClassNotFoundException {
        apolloController.movePhase();

        assertSame(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition(), fakeGameController.getGame().getBoard().getCell(0, 1));
        assertSame(fakeGameController.getGame().getPlayers().get(1).getWorkers().get(0).getPosition(), fakeGameController.getGame().getBoard().getCell(1, 2));
    }

    @Test
    public void movePhase_noInputGiven_shouldGenerateExceptionIllegalMove() throws IOException, ClassNotFoundException {
        //a client who tries to move in a domed cell
        class FakeVirtualViewToGenerateException extends FakeVirtualView {

            public FakeVirtualViewToGenerateException(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream){
                super(socket, objectInputStream, objectOutputStream);
            }
            @Override
            public Cell chooseMovePosition(ArrayList<Cell> possibleMoves) {
                return (fakeGameController.getGame().getBoard().getCell(1, 1));
            }
        }

        //new inizialization needed to use FakeCLItoGenerateException
        socket1=new Socket();
        fakeVirtualView1=new FakeVirtualViewToGenerateException(socket1, ois1, ous1);
        fakeVirtualView1.setId("ApolloTestToGenerateException");
        fakeGameController=new FakeGameController(fakeVirtualView1, 1);
        apolloController=new ApolloController(fakeGameController);
        apolloController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView1);
        Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(1,2));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        apolloController.activeWorker = worker;
        fakeGameController.getGame().getBoard().getCell(1, 1).buildDome();

        apolloController.movePhase();
    }

    @Test
    public void findPossibleMoves_workerPositionGiven_shouldReturnAllNeighborsIncludedTheCellOccupiedByOpponentWorker() {
        fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).setPosition(fakeGameController.getGame().getBoard().getCell(3, 3));
        fakeGameController.getGame().getPlayers().get(1).getWorkers().get(0).setPosition(fakeGameController.getGame().getBoard().getCell(3, 2));
        ArrayList<Cell> expectedMoves = fakeGameController.getGame().getBoard().getNeighbors(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition());

        assertEquals(apolloController.findPossibleMoves(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition()), expectedMoves);
    }
}