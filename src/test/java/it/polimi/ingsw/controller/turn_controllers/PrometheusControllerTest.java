package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.PlayerController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.view.FakeCLI;
import it.polimi.ingsw.view.PlayerInterface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class PrometheusControllerTest {
    PrometheusController prometheusController = null;
    FakeGameController fakeGameController = null;
    PlayerInterface playerInterface = null;
    FakeCLI cli = null;

    public class FakeGameController extends GameController {

        public FakeGameController(PlayerInterface client, int num) {
            super(client, num);
        }

        @Override
        public void addPlayer(PlayerInterface client) {
            Player player = new Player(client.getId(), colors.get(playerControllers.size()));
            PlayerController playerController = new PlayerController(player, client);
            game.addPlayer(player);
            playerControllers.add(playerController);
            gameSetUp();
        }

        @Override
        public void gameSetUp() {
            Deck deck = game.getDeck();
            deck.addCard(prometheusController.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(prometheusController);

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
            game.getBoard().getCell(2, 2).setBuildLevel(1);
        }

        public void playGame() {
            String result = playerControllers.get(game.getActivePlayer()).playTurn();
            if (result.equals("WON"))
                game.setWinner(players.get(game.getActivePlayer()));
        }

        @Override
        public void displayBoard() {
        }

        @Override
        public void displayMessage(String message) {
        }

    }

    @Before
    public void setUp() throws Exception {
        cli=new FakeCLI();
        playerInterface=new PlayerInterface(cli);
        playerInterface.setId("PrometheusTest");
        fakeGameController=new FakeGameController(playerInterface, 1);
        prometheusController=new PrometheusController(fakeGameController);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void generateCard() {
        Card testCard=new Card("Prometheus", "Titan Benefactor of Mankind", "Your Turn: If your Worker does not move up, it may build both before and after moving.", 1, false, prometheusController);
        assertEquals(prometheusController.generateCard().getGod(), testCard.getGod());
        assertEquals(prometheusController.generateCard().getTitle(), testCard.getTitle());
        assertEquals(prometheusController.generateCard().getDescription(), testCard.getDescription());
        assertEquals(prometheusController.generateCard().getSet(), testCard.getSet());
        assertEquals(prometheusController.generateCard().hasAlwaysActiveModifier(), testCard.hasAlwaysActiveModifier());
        assertEquals(prometheusController.generateCard().getController(), testCard.getController());
    }

    @Test
    public void runPhases_workerGiven_shouldReturnNEXT() {
        Deck deck = fakeGameController.getGame().getDeck();
        Card card = prometheusController.generateCard();
        deck.addCard(card);
        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        prometheusController.setPlayer(fakeGameController.getGame().getPlayers().get(0), playerInterface);

        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(1, 2));

        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        prometheusController.activeWorker = worker;

        fakeGameController.getGame().getBoard().getCell(1, 2).setBuildLevel(2);
        fakeGameController.getGame().getBoard().getCell(0, 1).setBuildLevel(2);
        assertEquals(prometheusController.runPhases(worker), "NEXT");
    }

    @Test
    public void checkMoves_noInputGiven_shouldReturnTrue(){
        Deck deck = fakeGameController.getGame().getDeck();
        Card card = prometheusController.generateCard();
        deck.addCard(card);
        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        prometheusController.setPlayer(fakeGameController.getGame().getPlayers().get(0), playerInterface);

        Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(1,2));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        prometheusController.activeWorker=worker;

        fakeGameController.getGame().getBoard().getCell(1,2).setBuildLevel(2);
        fakeGameController.getGame().getBoard().getCell(0,1).setBuildLevel(3);

        assertTrue(prometheusController.checkMoves());
    }

    @Test
    public void checkMoves_noInputGiven_shouldReturnFalse(){
        Deck deck = fakeGameController.getGame().getDeck();
        Card card = prometheusController.generateCard();
        deck.addCard(card);
        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        prometheusController.setPlayer(fakeGameController.getGame().getPlayers().get(0), playerInterface);

        Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(0,0));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        prometheusController.activeWorker=worker;

        fakeGameController.getGame().getBoard().getCell(0,0).setBuildLevel(0);
        fakeGameController.getGame().getBoard().getCell(0,1).setBuildLevel(2);
        fakeGameController.getGame().getBoard().getCell(1,1).setBuildLevel(2);
        fakeGameController.getGame().getBoard().getCell(1,0).setBuildLevel(2);

        assertFalse(prometheusController.checkMoves());
    }

    @Test
    public void movePhase_noInputGiven_shouldNotBuildBeforeAndGenerateMovingException() {
        //a client who chooses to move in an illegal cell
        class FakeCLItoGenerateException extends FakeCLI{
            @Override
            public Cell chooseMovePosition(ArrayList<Cell> possibleMoves){
                return(fakeGameController.getGame().getBoard().getCell(1,1));
            }
            @Override
            public Cell chooseBuildPosition(ArrayList<Cell> possibleMoves){
                return(fakeGameController.getGame().getBoard().getCell(2,2));
            }
        }

        FakeCLItoGenerateException cli=new FakeCLItoGenerateException();
        PlayerInterface playerInterface1=new PlayerInterface(cli);
        playerInterface1.setId("PrometheusTestToGenerateException");
        fakeGameController=new FakeGameController(playerInterface1, 1);
        prometheusController=new PrometheusController(fakeGameController);
        prometheusController.setPlayer(fakeGameController.getGame().getPlayers().get(0), playerInterface1);
        Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(0,0));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        prometheusController.activeWorker=worker;
        fakeGameController.getGame().getBoard().getCell(0,1).buildDome();
        fakeGameController.getGame().getBoard().getCell(1,1).buildDome();
        fakeGameController.getGame().getBoard().getCell(1,0).buildDome();

        prometheusController.runPhases(worker);
    }

    @Test
    public void findPossibleMovesNoUp() {
        Deck deck = fakeGameController.getGame().getDeck();
        Card card = prometheusController.generateCard();
        deck.addCard(card);
        fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
        prometheusController.setPlayer(fakeGameController.getGame().getPlayers().get(0), playerInterface);

        Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(1,2));
        fakeGameController.getGame().getBoard().getCell(2, 2).setBuildLevel(1);

        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        prometheusController.activeWorker=worker;

        ArrayList<Cell> expectedArrayList = fakeGameController.getGame().getBoard().getNeighbors(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition());
        expectedArrayList.remove(fakeGameController.getGame().getBoard().getCell(2,2));
        assertEquals(prometheusController.findPossibleMovesNoUp(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition()), expectedArrayList);
    }
}