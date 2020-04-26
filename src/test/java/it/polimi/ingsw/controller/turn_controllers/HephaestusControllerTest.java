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

public class HephaestusControllerTest {
    HephaestusController hephaestusController = null;
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
            deck.addCard(hephaestusController.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(hephaestusController);

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
        playerInterface.setId("HephaestusTest");
        fakeGameController=new FakeGameController(playerInterface,1);
        hephaestusController=new HephaestusController(fakeGameController);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void generateCard_noInputGiven_shouldReturnTheGodCard() {
        Card testCard=new Card("Hephaestus", "God of Blacksmiths", "Your Build: Your Worker may build one additional block (not dome) on top of your first block.", 1, false, hephaestusController);
        assertEquals(hephaestusController.generateCard().getGod(), testCard.getGod());
        assertEquals(hephaestusController.generateCard().getTitle(), testCard.getTitle());
        assertEquals(hephaestusController.generateCard().getDescription(), testCard.getDescription());
        assertEquals(hephaestusController.generateCard().getSet(), testCard.getSet());
        assertEquals(hephaestusController.generateCard().hasAlwaysActiveModifier(), testCard.hasAlwaysActiveModifier());
        assertEquals(hephaestusController.generateCard().getController(), testCard.getController());
    }

    @Test
    public void buildPhase() {
        fakeGameController.gameSetUp();
        assertEquals(fakeGameController.getGame().getBoard().getCell(0,0).getBuildLevel(), 2);
    }

    @Test
    public void buildPhase_noInputGiven_shouldGenerateTwoBuildingExceptions() {
        //a client who chooses to build twice in a domed cell
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
        playerInterface1.setId("HephaestusTestToGenerateException");
        fakeGameController=new FakeGameController(playerInterface1, 1);
        hephaestusController=new HephaestusController(fakeGameController);
        hephaestusController.setPlayer(fakeGameController.getGame().getPlayers().get(0), playerInterface1);
        Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(0,0));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        hephaestusController.activeWorker=worker;
        fakeGameController.getGame().getBoard().getCell(2,2).buildDome();

        hephaestusController.buildPhase();
    }
}