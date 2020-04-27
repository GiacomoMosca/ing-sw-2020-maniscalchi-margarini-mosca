package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.PlayerController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.game_board.Cell;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.view.FakeCLI;
import it.polimi.ingsw.view.VirtualView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class AthenaControllerTest {
    AthenaController athenaController;
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
        public void broadcastBoard() {
        }

        @Override
        public void displayMessage(String message) {
        }

    }


    @Before
    public void setUp() throws Exception {
        cli=new FakeCLI();
        playerInterface=new PlayerInterface(cli);
        fakeGameController=new FakeGameController(playerInterface, 1);
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
    public void movePhase_noInputGiven_shouldGenerateExceptionIllegalMove() {
        //a client who chooses to move in a domed cell
        class FakeCLItoGenerateException extends FakeCLI{
            @Override
            public Cell chooseMovePosition(ArrayList<Cell> possibleMoves){
                return(fakeGameController.getGame().getBoard().getCell(1,1));
            }
        }

        //need new inizialization to use FakeCLItoGenerateException
        FakeCLItoGenerateException cli=new FakeCLItoGenerateException();
        PlayerInterface playerInterface1=new PlayerInterface(cli);
        playerInterface1.setId("AthenaTestToGenerateException");
        fakeGameController=new FakeGameController(playerInterface1, 1);
        athenaController=new AthenaController(fakeGameController);
        athenaController.setPlayer(fakeGameController.getGame().getPlayers().get(0), playerInterface1);
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