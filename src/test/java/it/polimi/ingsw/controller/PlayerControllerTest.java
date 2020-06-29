package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.turn_controllers.ApolloController;
import it.polimi.ingsw.controller.turn_controllers.GodController;
import it.polimi.ingsw.controller.turn_controllers.GodControllerConcrete;
import it.polimi.ingsw.exceptions.GameEndedException;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.view.FakeVirtualView;
import it.polimi.ingsw.view.VirtualView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static org.junit.Assert.assertEquals;

public class PlayerControllerTest {

    private FakeVirtualView fakeVirtualView, fakeVirtualView2;
    private Socket socket, socket2;
    private ObjectInputStream objectInputStream, objectInputStream2;
    private ObjectOutputStream objectOutputStream, objectOutputStream2;
    private GameController gameController;
    private PlayerController playerController;
    private Player player;
    private Game game;
    private GodControllerConcrete godControllerConcrete;

    @Before
    public void setUp() throws Exception {
        socket = new Socket();
        fakeVirtualView = new FakeVirtualView(socket, objectInputStream, objectOutputStream);
        gameController = new GameController(fakeVirtualView, 2, "Test");
        player = new Player("Rebe","Green");
        playerController = new PlayerController(player,fakeVirtualView,gameController);
        fakeVirtualView.setPlayerController(playerController);
        game = new Game("test",player,2);
        gameController.game = game;
        gameController.playerControllers.remove(0);
        gameController.playerControllers.add(playerController);
        godControllerConcrete = new GodControllerConcrete(gameController);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getPlayer_noInputGiven_shouldReturnThePlayer() {
        assertEquals(playerController.getPlayer(), player);
    }

    @Test
    public void getClient_noInputGiven_shouldReturnTheClient() {
        assertEquals(playerController.getClient(), fakeVirtualView);
    }

    @Test
    public void getGame_noInputGiven_shouldReturnTheGameController() {
        assertEquals(playerController.getGame(),gameController);
        assertEquals(playerController.getGame().getGame(),game);
    }

    @Test
    public void getGodController_noInputGiven_shouldReturnTheGodController() {
        fakeVirtualView2 = new FakeVirtualView(socket2, objectInputStream2, objectOutputStream2);
        try {
            gameController.addPlayer(fakeVirtualView2);
        } catch (NullPointerException | GameEndedException e) {
            //
        }
        gameController.gameSetUp();
        Card apollo = new Card(
                "Apollo",
                "God of Music",
                "Your Move: Your Worker may move into an opponent Worker’s space " +
                        "\n(using normal movement rules) and force their Worker to the space " +
                        "\nyours just vacated (swapping their positions).",
                1,
                false,
                gameController.getGame().getDeck().getCards().get(0).getController()
        );
        Card athena = new Card(
                "Athena",
                "Goddess of Wisdom",
                "Opponent’s Turn: If one of your Workers moved up on your " +
                        "\nlast turn, opponent Workers cannot move up this turn.",
                1,
                false,
                gameController.getGame().getDeck().getCards().get(2).getController()
        );

        assertEquals(athena.getController(), playerController.getGodController());
    }

    @Test
    public void setGodController_godControllerGiven_shouldSetTheGodController() {
        playerController.setGodController(godControllerConcrete);
        assertEquals(playerController.getGodController(),godControllerConcrete);
    }

    @Test
    public void playTurn_noInputGiven_shouldPlayAGameTurn() {
        fakeVirtualView2 = new FakeVirtualView(socket2, objectInputStream2, objectOutputStream2);
        try {
            gameController.addPlayer(fakeVirtualView2);
        } catch (NullPointerException | GameEndedException e) {
            //
        }
        gameController.gameSetUp();
        revOemag();
        gameController.playerControllers.get(0).setGodController(godControllerConcrete);
        playerController.setGodController(godControllerConcrete);
        try {
            assertEquals(playerController.playTurn(),"next");
        } catch (IOExceptionFromController ioExceptionFromController) {
            //
        }
    }

    @Test
    public void playTurn_noInputGiven_shouldReturnOutOfMoveString() {
        playerController.setGodController(godControllerConcrete);
        try {
            assertEquals(playerController.playTurn(),"outOfMoves");
        } catch (IOExceptionFromController ioExceptionFromController) {
            //
        }
    }

    public void revOemag (){
        gameController.running.compareAndSet(false, true);
        int i=0;
        for (PlayerController controller : gameController.playerControllers) {
            controller.getClient().setPlayerController(gameController.getControllers().get(i));
            i++;
        }
    }
}
