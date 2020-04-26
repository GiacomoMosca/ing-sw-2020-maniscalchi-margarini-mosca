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

public class LimusControllerTest {

    LimusController limusController;
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
            deck.addCard(limusController.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(limusController);

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
        playerInterface.setId("LimusTest");
        fakeGameController=new FakeGameController(playerInterface, 1);
        limusController=new LimusController(fakeGameController);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void generateCard_noInputGiven_shouldReturnTheGodCard() {
        Card testCard=new Card("Limus", "Goddess of Famine", "Opponent’s Turn: Opponent Workers cannot build on spaces neighboring your Workers, unless building a dome to create a Complete Tower.", 2, true, limusController);
        assertEquals(limusController.generateCard().getGod(), testCard.getGod());
        assertEquals(limusController.generateCard().getTitle(), testCard.getTitle());
        assertEquals(limusController.generateCard().getDescription(), testCard.getDescription());
        assertEquals(limusController.generateCard().getSet(), testCard.getSet());
        assertEquals(limusController.generateCard().hasAlwaysActiveModifier(), testCard.hasAlwaysActiveModifier());
        assertEquals(limusController.generateCard().getController(), testCard.getController());
    }

    @Test
    public void limitBuilds_workerPositionAndArrayListOfCellsGiven_shouldReturnAnArrayListOfCellNotContainingTheCellNeighboringLimus() {
        //supposing that a worker (not even created) stands on (0,0). his possible builds should be (1,0), (1,1), (0,1), but
        //a Limus worker stands on (0,2) and this limits the building possibilities of the worker on (0,0): only (1.0)!
        Worker worker = new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(0, 2));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        limusController.setPlayer(fakeGameController.getGame().getPlayers().get(0), playerInterface);

        ArrayList<Cell> possibleBuildsAfterLimusPower = new ArrayList<Cell>();
        possibleBuildsAfterLimusPower.add(fakeGameController.getGame().getBoard().getCell(1,0));
        fakeGameController.getGame().getBoard().getCell(0,1).setBuildLevel(1);
        fakeGameController.getGame().getBoard().getCell(1,0).setBuildLevel(1);
        ArrayList<Cell> possibleBuildsBeforeLimusPower= fakeGameController.getGame().getBoard().getNeighbors(fakeGameController.getGame().getBoard().getCell(0,0)); //0,1 - 1,0 - 1,1

        assertEquals(limusController.limitBuilds(fakeGameController.getGame().getBoard().getCell(0,0), possibleBuildsBeforeLimusPower), possibleBuildsAfterLimusPower);
    }
}