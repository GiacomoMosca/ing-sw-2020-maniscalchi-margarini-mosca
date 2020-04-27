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

public class AthenaControllerTest {
    AthenaController athenaController;
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
            deck.addCard(athenaController.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(athenaController);

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
            game.getBoard().getCell(0, 1).setBuildLevel(1);
            game.getBoard().getCell(0,0).setBuildLevel(2);
        }

        public void playGame() {
            String result = playerControllers.get(game.getActivePlayer()).playTurn();
            if (result.equals("WON"))
                game.setWinner(players.get(game.getActivePlayer()));
        }

        @Override
        public void displayBoard() {
        }

    }


    @Before
    public void setUp() throws Exception {
        socket=new Socket();
        fakeVirtualView=new FakeVirtualView(socket, ois, ous);
        fakeVirtualView.setId("AthenaTest");
        fakeGameController=new FakeGameController(fakeVirtualView, 1);
        athenaController=new AthenaController(fakeGameController);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void generateCard_noInputGiven_shouldReturnTheGodCard() {
        Card testCard=new Card("Athena", "Goddess of Wisdom", "Opponent’s Turn: If one of your Workers moved up on your last turn, opponent Workers cannot move up this turn.", 1, false, athenaController);
        assertEquals(athenaController.generateCard().getGod(), testCard.getGod());
        assertEquals(athenaController.generateCard().getTitle(), testCard.getTitle());
        assertEquals(athenaController.generateCard().getDescription(), testCard.getDescription());
        assertEquals(athenaController.generateCard().getSet(), testCard.getSet());
        assertEquals(athenaController.generateCard().hasAlwaysActiveModifier(), testCard.hasAlwaysActiveModifier());
        assertEquals(athenaController.generateCard().getController(), testCard.getController());
    }

    @Test
    public void movePhase() {
        //checking if the modifier was added
        fakeGameController.gameSetUp();
        assertSame(fakeGameController.getGame().getActiveModifiers().get(0), athenaController.card);
    }

    @Test
    public void movePhase_noInputGiven_shouldGenerateExceptionIllegalMove() throws IOException, ClassNotFoundException {
        //a client who chooses to move in a domed cell
        class FakeVirtualViewToGenerateException extends FakeVirtualView{

            public FakeVirtualViewToGenerateException(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream){
                super(socket, objectInputStream, objectOutputStream);
            }
            @Override
            public Cell chooseMovePosition(ArrayList<Cell> possibleMoves){
                return(fakeGameController.getGame().getBoard().getCell(1,1));
            }
        }

        //need new inizialization to use FakeVirtualViewToGenerateException
        socket=new Socket();
        fakeVirtualView=new FakeVirtualViewToGenerateException(socket, ois, ous);
        fakeVirtualView.setId("AthenaTestToGenerateException");
        fakeGameController=new FakeGameController(fakeVirtualView, 1);
        athenaController=new AthenaController(fakeGameController);
        athenaController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);
        Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(1,2));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        athenaController.activeWorker=worker;
        fakeGameController.getGame().getBoard().getCell(1,1).buildDome();

        athenaController.movePhase();
    }

    @Test
    public void limitMoves() {
        //after gameSetUp the worker will be in (0,1). Cell (0,0) will be one level higher than (0,1) so that it will be removed from the possible moves
        //checking if this happens
        fakeGameController.gameSetUp();

        ArrayList<Cell> n = fakeGameController.getGame().getBoard().getNeighbors(fakeGameController.getGame().getBoard().getCell(0,1));
        ArrayList<Cell> a = fakeGameController.getGame().getBoard().getNeighbors(fakeGameController.getGame().getBoard().getCell(0,1));
        a.remove(fakeGameController.getGame().getBoard().getCell(0,0));
        assertEquals(athenaController.limitMoves(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition(), n), a);
    }
}