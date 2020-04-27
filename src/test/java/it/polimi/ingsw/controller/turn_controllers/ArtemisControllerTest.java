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

public class ArtemisControllerTest {

    ArtemisController artemisController = null;
    ArtemisControllerTest.FakeGameController fakeGameController = null;
    FakeVirtualView fakeVirtualView;
    Socket socket;
    ObjectInputStream ois;
    ObjectOutputStream ous;

    public class FakeGameController extends GameController {

        public FakeGameController(VirtualView virtualView, int num) {
            super(virtualView, num);
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
            deck.addCard(artemisController.generateCard());

            players = game.getPlayers();
            players.get(0).setGodCard(deck.getCards().get(0));
            playerControllers.get(0).setGodController(artemisController);

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
    }
        @Before
        public void setUp() throws Exception {
            //it's not okay to call fakeGameController.gameSetUp() because
            socket=new Socket();
            fakeVirtualView=new FakeVirtualView(socket, ois, ous);
            fakeVirtualView.setId("ArtemisTest");
            fakeGameController=new FakeGameController(fakeVirtualView, 1);
            artemisController=new ArtemisController(fakeGameController);
        }

        @After
        public void tearDown() throws Exception {
        }

        @Test
        public void generateCard_noInputGiven_shouldReturnTheGodCard() {
            Card testCard=new Card("Artemis", "Goddess of the Hunt", "Your Move: Your Worker may move one additional time, but not back to the space it started on.", 1, false, artemisController);
            assertEquals(artemisController.generateCard().getGod(), testCard.getGod());
            assertEquals(artemisController.generateCard().getTitle(), testCard.getTitle());
            assertEquals(artemisController.generateCard().getDescription(), testCard.getDescription());
            assertEquals(artemisController.generateCard().getSet(), testCard.getSet());
            assertEquals(artemisController.generateCard().hasAlwaysActiveModifier(), testCard.hasAlwaysActiveModifier());
            assertEquals(artemisController.generateCard().getController(), testCard.getController());
        }

        @Test
        public void runPhases_workerGiven_shouldReturnWONAfterTheFirstMove() throws IOException, ClassNotFoundException {
            Deck deck = fakeGameController.getGame().getDeck();
            Card card = artemisController.generateCard();
            deck.addCard(card);
            fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
            artemisController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);

            Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
            worker.setPosition(fakeGameController.getGame().getBoard().getCell(1,2));
            fakeGameController.getGame().getPlayers().get(0).addWorker(worker);

            fakeGameController.getGame().getBoard().getCell(1,2).setBuildLevel(2);
            fakeGameController.getGame().getBoard().getCell(0,1).setBuildLevel(3);

            assertEquals(artemisController.runPhases(worker), "WON");

        }

        @Test
        public void runPhasesAndfindPossibleMoves_workerGivenAndNoInputGiven_shouldReturnWONAfterTheSecondMoveAndAnArrayListContainingAllNeighbors() throws IOException, ClassNotFoundException {
            Deck deck = fakeGameController.getGame().getDeck();
            Card card = artemisController.generateCard();
            deck.addCard(card);
            fakeGameController.getGame().getPlayers().get(0).setGodCard(card);
            artemisController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);
            Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
            worker.setPosition(fakeGameController.getGame().getBoard().getCell(1,2));
            fakeGameController.getGame().getPlayers().get(0).addWorker(worker);
            fakeGameController.getGame().getBoard().getCell(1,2).setBuildLevel(2);
            fakeGameController.getGame().getBoard().getCell(0,1).setBuildLevel(2);
            fakeGameController.getGame().getBoard().getCell(0,0).setBuildLevel(3);

            assertEquals(artemisController.runPhases(worker), "WON");
            ArrayList<Cell> expectedArrayList = fakeGameController.getGame().getBoard().getNeighbors(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition());
            assertEquals(artemisController.findLegalMoves(fakeGameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition(), expectedArrayList), expectedArrayList);
        }

        @Test
        public void runPhases_workerGiven_shouldNotAcceptToMoveTheSecondTime() throws IOException, ClassNotFoundException {
            //a client who chooses not to move the second time
            class FakeVirtualViewToAnswerNo extends FakeVirtualView{

                public FakeVirtualViewToAnswerNo(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream){
                    super(socket, objectInputStream, objectOutputStream);
                }
                @Override
                public boolean chooseYesNo(String query){
                    return false;
                }
            }
            socket=new Socket();
            FakeVirtualViewToAnswerNo fakeVirtualViewToAnswerNo=new FakeVirtualViewToAnswerNo(socket, ois, ous);
            fakeVirtualViewToAnswerNo.setId("ArtemisTestToAnswerNo");
            fakeGameController=new FakeGameController(fakeVirtualView, 1);
            artemisController=new ArtemisController(fakeGameController);
            artemisController.setPlayer(fakeGameController.getGame().getPlayers().get(0), fakeVirtualView);
            Worker worker=new Worker(fakeGameController.getGame().getPlayers().get(0));
            worker.setPosition(fakeGameController.getGame().getBoard().getCell(0,0));
            fakeGameController.getGame().getPlayers().get(0).addWorker(worker);

            artemisController.runPhases(worker);
        }
    }