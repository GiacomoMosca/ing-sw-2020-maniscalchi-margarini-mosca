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
import static org.junit.Assert.assertTrue;

public class AtlasControllerTest {

    private AtlasController atlasController = null;
    private AtlasGameController fakeGameController = null;
    private FakeVirtualView fakeVirtualView;
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    @Before
    public void setUp() {
        socket = new Socket();
        fakeVirtualView = new FakeVirtualView(socket, objectInputStream, objectOutputStream);
        fakeGameController = new AtlasGameController(fakeVirtualView, 1, "AtlasTest");
        atlasController = new AtlasController(fakeGameController);
    }

    @Test
    public void generateCard_noInputGiven_shouldReturnTheGodCard() {
        Card testCard = new Card("Atlas",
                "Titan Shouldering the Heavens",
                "Your Build: Your Worker may build a dome at any level including the ground.",
                1,
                false,
                atlasController);
        assertEquals(atlasController.generateCard(), testCard);
    }

    /*@After
    public void tearDown(){
    }*/

    @Test
    public void buildPhase_noInputGiven_shouldBuildDome() {
        //buildPhase() is later called by gameSetUp()
        fakeGameController.gameSetUp();
        assertTrue(fakeGameController.getGame().getBoard().getCell(0, 0).isDomed());
    }

    @Test
    public void buildPhase_noInputGiven_shouldGenerateExceptionIllegalBuild() throws Exception {
        //a client who chooses to build in a domed cell
        class FakeVirtualViewToGenerateException extends FakeVirtualView {
            public FakeVirtualViewToGenerateException(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
                super(socket, objectInputStream, objectOutputStream);
            }

            @Override
            public Cell chooseMovePosition(ArrayList<Cell> possibleMoves) {
                return (fakeGameController.getGame().getBoard().getCell(1, 1));
            }

            @Override
            public Cell chooseBuildPosition(ArrayList<Cell> possibleMoves) {
                return (fakeGameController.getGame().getBoard().getCell(2, 2));
            }

            @Override
            public boolean chooseYesNo(String query) {
                return false;
            }
        }

        Socket socket = new Socket();
        fakeVirtualView = new FakeVirtualViewToGenerateException(socket, objectInputStream, objectOutputStream);
        fakeGameController = new AtlasGameController(fakeVirtualView, 1, "AtlasTest");
        atlasController = new AtlasController(fakeGameController);
        atlasController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);
        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0), 1);
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(0, 0));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        atlasController.activeWorker = worker;
        fakeGameController.getGame().getBoard().getCell(2, 2).buildDome();
        atlasController.buildPhase();
    }

    public class AtlasGameController extends FakeGameController {

        public AtlasGameController(VirtualView client, int num, String gameName) {
            super(client, num, gameName);
        }

        @Override
        public void gameSetUp() {
            Deck deck = game.getDeck();
            deck.addCard(atlasController.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(atlasController);

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
            game.getBoard().getCell(0, 0).setBuildLevel(1);
        }

    }
}