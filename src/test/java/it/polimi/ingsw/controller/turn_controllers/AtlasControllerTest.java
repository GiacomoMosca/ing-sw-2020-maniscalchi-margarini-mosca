package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.PlayerController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.view.FakeCLI;
import it.polimi.ingsw.view.PlayerInterface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AtlasControllerTest {

    AtlasController atlas=null;
    FakeGameController fakeGameController=null;
    PlayerInterface playerInterface=null;
    FakeCLI cli = null;

    public class FakeGameController extends GameController {

        public FakeGameController(PlayerInterface client, int num) {
            super(client,num);
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
            deck.addCard(atlas.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(atlas);

            placeWorkers();

            placeBuildings();

            playGame();
        }

        private void placeWorkers() {
            Worker worker = new Worker(players.get(0));
            worker.setPosition(game.getBoard().getCell(1, 2));
            players.get(0).addWorker(worker);

        }

        private void placeBuildings(){
            game.getBoard().getCell(0,0).setBuildLevel(1);
        }

        public void playGame() {
            String result = playerControllers.get(game.getActivePlayer()).playTurn();
            if(result.equals("WON"))
                game.setWinner(players.get(game.getActivePlayer()));
        }

        @Override
        public void displayBoard() {}

        @Override
        public void broadcastMessage(String message) {}
    }

    @Before
    public void setUp(){
        cli = new FakeCLI();
        playerInterface = new PlayerInterface(cli);
        fakeGameController = new FakeGameController(playerInterface,1);
        atlas = new AtlasController(fakeGameController);
    }

    @After
    public void tearDown(){
    }

    @Test
    public void generateCard() {
        Card testCard = new Card(
                "Atlas",
                "Titan Shouldering the Heavens",
                "Your Build: Your Worker may build a dome at any level including the ground.",
                1,
                false,
                atlas);
        assertEquals(atlas.generateCard(),testCard);
    }

    @Test
    public void buildPhase() {
        playerInterface.setId("AtlasTest");
        fakeGameController.gameSetUp();
        assertTrue(fakeGameController.getGame().getBoard().getCell(0,0).isDomed());
    }
    
}