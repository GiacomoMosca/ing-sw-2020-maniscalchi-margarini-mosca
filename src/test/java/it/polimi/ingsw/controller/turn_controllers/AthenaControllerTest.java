package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.FakeGameController;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
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
import static org.junit.Assert.assertSame;

public class AthenaControllerTest {
    AthenaController athenaController;
    AthenaGameController fakeGameController;
    FakeVirtualView fakeVirtualView;
    Socket socket;
    ObjectInputStream ois;
    ObjectOutputStream ous;

    @Before
    public void setUp() throws Exception {
        socket = new Socket();
        fakeVirtualView = new FakeVirtualView(socket, ois, ous);
        fakeGameController = new AthenaGameController(fakeVirtualView, 1, "AthenaTest");
        athenaController = new AthenaController(fakeGameController);
    }

    @Test
    public void generateCard_noInputGiven_shouldReturnTheGodCard() {
        Card testCard = new Card("Athena",
                "Goddess of Wisdom",
                "Opponent’s Turn: If one of your Workers moved up on your last turn, opponent Workers cannot move up this turn.",
                1,
                false,
                athenaController);
        assertEquals(athenaController.generateCard(), testCard);
    }

    /*@After
    public void tearDown() throws Exception {
    }*/

    @Test
    public void movePhase_noInputGiven_shouldMoveTheWorkerAndAddTheModifier() {
        fakeGameController.gameSetUp();
        assertEquals(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition(), fakeGameController.getGame().getBoard().getCell(0, 1));
        assertSame(fakeGameController.getGame().getActiveModifiers().get(0), athenaController.card);
    }

    @Test
    public void movePhase_noInputGiven_shouldGenerateExceptionIllegalMove() throws IOException, ClassNotFoundException, IOExceptionFromController {
        //a client who chooses to move in a domed cell
        class FakeVirtualViewToGenerateException extends FakeVirtualView {
            public FakeVirtualViewToGenerateException(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
                super(socket, objectInputStream, objectOutputStream);
            }

            @Override
            public Cell chooseMovePosition(ArrayList<Cell> possibleMoves) {
                return (fakeGameController.getGame().getBoard().getCell(1, 1));
            }
        }

        //need new initialization to use FakeVirtualViewToGenerateException instead of FakeVirtualView
        socket = new Socket();
        fakeVirtualView = new FakeVirtualViewToGenerateException(socket, ois, ous);
        fakeGameController = new AthenaGameController(fakeVirtualView, 1, "AthenaTest");
        athenaController = new AthenaController(fakeGameController);
        athenaController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);
        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0), 1);
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(1, 2));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        athenaController.activeWorker = worker;
        fakeGameController.getGame().getBoard().getCell(1, 1).buildDome();

        athenaController.movePhase();
    }

    @Test
    public void limitMoves_positionAndPossibleMovesGiven_shouldReturnPossibleMovesExcludingOneCellAtHigherLevel() {
        //after gameSetUp the worker is in (0,1). Cell (0,0) is a level higher than (0,1) so that it will be removed from the possible moves
        //checking if this happens
        fakeGameController.gameSetUp();

        ArrayList<Cell> n = fakeGameController.getGame().getBoard().getNeighbors(fakeGameController.getGame().getBoard().getCell(0, 1));
        ArrayList<Cell> a = fakeGameController.getGame().getBoard().getNeighbors(fakeGameController.getGame().getBoard().getCell(0, 1));
        a.remove(fakeGameController.getGame().getBoard().getCell(0, 0));

        assertEquals(athenaController.limitMoves(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition(), n), a);
    }

    public class AthenaGameController extends FakeGameController {

        public AthenaGameController(VirtualView client, int num, String gameName) {
            super(client, num, gameName);
        }

        @Override
        public void gameSetUp() {
            Deck deck = game.getDeck();
            deck.addCard(athenaController.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(athenaController);

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
            game.getBoard().getCell(0, 1).setBuildLevel(1);
            game.getBoard().getCell(0, 0).setBuildLevel(2);
        }

    }
}