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

public class HestiaControllerTest {
    private HestiaController hestiaController = null;
    private FakeGameController fakeGameController = null;
    private FakeVirtualView fakeVirtualView;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public class FakeGameController extends GameController {

        public FakeGameController(VirtualView client, int num, String gameName) {
            super(client, num, gameName);
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
            deck.addCard(hestiaController.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(hestiaController);

            placeWorkers();
            placeBuildings();
            playGame();
        }

        private void placeWorkers() {
            Worker worker = new Worker(players.get(0));
            worker.setPosition(game.getBoard().getCell(2, 2));
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

    }

    @Before
    public void setUp() throws Exception {
        socket=new Socket();
        fakeVirtualView=new FakeVirtualView(socket, objectInputStream, objectOutputStream);
        fakeGameController=new FakeGameController(fakeVirtualView, 1, "game");
        hestiaController=new HestiaController(fakeGameController);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void generateCard_noInputGiven_shouldReturnTheGodCard() {
        Card testCard=new Card("Hestia",
                "Goddess of Hearth and Home",
                "Your Build: Your Worker may build one additional time. The additional build cannot be on a perimeter space.",
                2,
                false,
                hestiaController);
        assertEquals(hestiaController.generateCard(), testCard); }

    @Test
    public void runPhases_workerGiven_shouldReturnWON() throws IOException, ClassNotFoundException {
        Deck deck = fakeGameController.getGame().getDeck();
        Card card = hestiaController.generateCard();
        deck.addCard(card);
        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        hestiaController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);
        Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(1,2));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);

        fakeGameController.getGame().getBoard().getCell(1,2).setBuildLevel(2);
        fakeGameController.getGame().getBoard().getCell(0,1).setBuildLevel(3);

        assertEquals(hestiaController.runPhases(worker), "WON");
    }

    @Test
    public void runPhases_workerGiven_shouldBuildTwoTimesAndReturnNEXT() {
        //checking that the first building is on (0,0) and the second building on (1,2)
        //the worker starts from (2,2), moves to (1,1), builds the first time on (0,0) and the second time on (1,2)
        //because it's the first not-perimeter-cell

        //calling gameSetUp() will later invoke runPhases, covering the case of return "NEXT"
        fakeGameController.gameSetUp();
        assertEquals(fakeGameController.getGame().getBoard().getCell(0,0).getBuildLevel(),1);
        assertEquals(fakeGameController.getGame().getBoard().getCell(1,2).getBuildLevel(),1);
    }

    @Test
    public void findPossibleBuilds_workerPositionAndPossibleBuildsGiven_shouldReturnTheSameArrayListOfCells() {
        fakeGameController.gameSetUp();
        ArrayList<Cell> expectedArrayList = fakeGameController.getGame().getBoard().getNeighbors(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition());
        assertEquals(hestiaController.findLegalMoves(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition(), expectedArrayList), expectedArrayList);
    }
}