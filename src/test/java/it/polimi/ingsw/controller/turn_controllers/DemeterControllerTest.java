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

public class DemeterControllerTest {

    DemeterController demeterController;
    FakeGameController fakeGameController;
    PlayerInterface playerInterface;
    FakeCLI cli;

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
            deck.addCard(demeterController.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(demeterController);

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

        @Override
        public void displayMessage(String message) {
        }

    }

    @Before
    public void setUp() throws Exception {
        cli = new FakeCLI();
        playerInterface = new PlayerInterface(cli);
        playerInterface.setId("DemeterTest");
        fakeGameController = new FakeGameController(playerInterface,1);
        demeterController = new DemeterController(fakeGameController);

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void generateCard_noInputGiven_shouldReturnTheGodCard() {
        Card testCard = new Card(
                "Demeter",
                "Goddess of the Harvest",
                "Your Build: Your Worker may build one additional time, but not on the same space.",
                1,
                false,
                demeterController);
        assertEquals(demeterController.generateCard().getGod(), testCard.getGod());
        assertEquals(demeterController.generateCard().getTitle(), testCard.getTitle());
        assertEquals(demeterController.generateCard().getDescription(), testCard.getDescription());
        assertEquals(demeterController.generateCard().getSet(), testCard.getSet());
        assertEquals(demeterController.generateCard().hasAlwaysActiveModifier(), testCard.hasAlwaysActiveModifier());
        assertEquals(demeterController.generateCard().getController(), testCard.getController());
    }

    @Test
    public void buildPhase_noInputGiven_shouldBuildTwoTimesInTwoDifferentCells() {
        //after GameSetUp, the worker will be in (0,1), building one time in (0,0) and one time in (0,2),
        //since the cell (0,0) has been removed from the possibleBuilds.
        // 0,0 - (0,1) - 0,2
        // 1,0 -  1,1  - 1,2

        fakeGameController.gameSetUp();
        assertEquals(fakeGameController.getGame().getBoard().getCell(0,0).getBuildLevel(),1);
        assertEquals(fakeGameController.getGame().getBoard().getCell(0,2).getBuildLevel(),1);
    }

    @Test
    public void buildPhase_noInputGiven_shouldNotBuildDomeAndGenerateTwoIllegalBuildsExceptions() {
        //a client who chooses to build two times in an illegal cell
        class FakeCLItoGenerateException extends FakeCLI{
            @Override
            public Cell chooseBuildPosition(ArrayList<Cell> possibleMoves){
                return(fakeGameController.getGame().getBoard().getCell(2,2));
            }
        }

        FakeCLItoGenerateException cli=new FakeCLItoGenerateException();
        PlayerInterface playerInterface1=new PlayerInterface(cli);
        playerInterface1.setId("DemeterTestToGenerateException");
        fakeGameController=new FakeGameController(playerInterface1, 1);
        demeterController=new DemeterController(fakeGameController);
        demeterController.setPlayer(fakeGameController.getGame().getPlayers().get(0), playerInterface1);
        Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(0,0));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        demeterController.activeWorker=worker;
        fakeGameController.getGame().getBoard().getCell(2,2).buildDome();

        demeterController.buildPhase();
    }
}

