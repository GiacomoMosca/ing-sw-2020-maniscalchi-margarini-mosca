package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.PlayerController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.view.FakeCLI;
import it.polimi.ingsw.view.virtualView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class AtlasControllerTest {

    AtlasController atlasController=null;
    FakeGameController fakeGameController=null;
    VirtualView virtualView=null;
    FakeCLI cli = null;

    public class FakeGameController extends GameController {

        public FakeGameController(VirtualView client, int num) {
            super(client,num);
        }

        @Override
        public void addPlayer(VirtualView client) {
            Player player = new Player(client.getId(), colors.get(playerControllers.size()));
            PlayerController playerController = new VirtualView(player, client);
            game.addPlayer(player);
            playerControllers.add(playerController);
            gameSetUp();
        }

        @Override
        public void gameSetUp() {

            Deck deck = game.getDeck();
            deck.addCard(atlasController.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(atlasController);

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
        public void broadcastBoard() {}

        @Override
        public void displayMessage(String message) {}
    }

    @Before
    public void setUp(){
        cli = new FakeCLI();
        virtualView = new VirtualView(cli);
        virtualView.setId("AtlasTest");
        fakeGameController = new FakeGameController(virtualView,1);
        atlasController = new AtlasController(fakeGameController);
    }

    @After
    public void tearDown(){
    }

    @Test
    public void generateCard_noInputGiven_shouldReturnTheGodCard() {
        Card testCard = new Card(
                "Atlas",
                "Titan Shouldering the Heavens",
                "Your Build: Your Worker may build a dome at any level including the ground.",
                1,
                false,
                atlasController);
        assertEquals(atlasController.generateCard().getGod(), testCard.getGod());
        assertEquals(atlasController.generateCard().getTitle(), testCard.getTitle());
        assertEquals(atlasController.generateCard().getDescription(), testCard.getDescription());
        assertEquals(atlasController.generateCard().getSet(), testCard.getSet());
        assertEquals(atlasController.generateCard().hasAlwaysActiveModifier(), testCard.hasAlwaysActiveModifier());
        assertEquals(atlasController.generateCard().getController(), testCard.getController());

    }

    @Test
    public void buildPhase_noInputGiven_shouldBuildDome() {
        //buildPhase() is later called by gameSetUp()
        fakeGameController.gameSetUp();
        assertTrue(fakeGameController.getGame().getBoard().getCell(0,0).isDomed());
    }

    @Test
    public void buildPhase_noInputGiven_shouldGenerateExceptionIllegalBuild() {
        //a client who chooses to build in a domed cell
        class FakeCLItoGenerateException extends FakeCLI{
            @Override
            public Cell chooseMovePosition(ArrayList<Cell> possibleMoves){
                return(fakeGameController.getGame().getBoard().getCell(1,1));
            }
            @Override
            public Cell chooseBuildPosition(ArrayList<Cell> possibleMoves){
                return(fakeGameController.getGame().getBoard().getCell(2,2));
            }
            @Override
            public boolean chooseYesNo(String query){
                return false;
            }
        }

        FakeCLItoGenerateException cli=new FakeCLItoGenerateException();
        VirtualView virtualView=new VirtualView(cli);
        virtualView1.setId("AtlasTestToGenerateException");
        fakeGameController=new FakeGameController(virtualView1, 1);
        atlasController=new AtlasController(fakeGameController);
        atlasController.setPlayer(fakeGameController.getGame().getPlayers().get(0), virtualView1);
        Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
        worker.setPosition(fakeGameController.getGame().getBoard().getCell(0,0));
        fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
        atlasController.activeWorker=worker;
        fakeGameController.getGame().getBoard().getCell(2,2).buildDome();
        atlasController.buildPhase();
    }

}