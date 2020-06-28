package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.turn_controllers.*;
import it.polimi.ingsw.exceptions.GameEndedException;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Card;
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

import static org.junit.Assert.*;

public class GameControllerTest {
    private FakeVirtualView virtualView1, virtualView2, virtualView3;
    private Socket socket1, socket2, socket3;
    private ObjectInputStream objectInputStream1, objectInputStream2, objectInputStream3;
    private ObjectOutputStream objectOutputStream1, objectOutputStream2, objectOutputStream3;
    private GameController gameController;
    private FakePlayerController playerController1, playerController2;
    private Player player1, player2;
    private LimusController limusController;
    private ApolloController apolloController;
    private Game game;

    @Before
    public void setUp() throws Exception {
        virtualView1 = new FakeVirtualView(socket1, objectInputStream1, objectOutputStream1);
        virtualView2 = new FakeVirtualView(socket2,objectInputStream2,objectOutputStream2);
        gameController = new GameController(virtualView1, 3, "Test");
        player1 = new Player("Luca","Green");
        player2 = new Player("Rebe", "Blu");
        playerController1 = new FakePlayerController(player1,virtualView1,gameController);
        playerController2 = new FakePlayerController(player2,virtualView2,gameController);
        virtualView1.setPlayerController(playerController1);
        virtualView2.setPlayerController(playerController2);
        gameController.game.addPlayer(player1);
        gameController.game.addPlayer(player2);
        gameController.playerControllers.remove(0);
        gameController.playerControllers.add(playerController1);
        gameController.playerControllers.add(playerController2);
        game = new Game("test",player1,3);
        gameController.game = game;
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void IsRunning_noInputGiven_shouldReturnIfTheGameIsRunning() {
        assertTrue(gameController.isRunning());
        gameController.gameOver();
        assertFalse(gameController.isRunning());
    }

    @Test
    public void IsSetup_noInputGiven_shouldReturnIfTheGameIsInTheSetUpPhase() {
        assertTrue(gameController.isSetup());
        virtualView3 = new FakeVirtualView(socket3, objectInputStream3, objectOutputStream3);
        try {
            gameController.addPlayer(virtualView3);
        } catch (NullPointerException | GameEndedException e) {
            //
        }
        gameController.gameSetUp();
        assertFalse(gameController.isSetup());
    }

    @Test
    public void getGame_noInputGiven_shouldReturnTheGame() {
        assertEquals(gameController.getGame(), gameController.game);
    }

    @Test
    public void GetControllers_noInputGiven_shouldReturnPlayerControllers() {
        assertEquals(gameController.getControllers().get(0).getPlayer(), gameController.getGame().getPlayers().get(0));
    }

    @Test
    public void addPlayer_virtualViewGiven_shouldAddThePlayer() {
        virtualView2 = new FakeVirtualView(socket2, objectInputStream2, objectOutputStream2);
        try {
            gameController.addPlayer(virtualView2);
        } catch (NullPointerException | GameEndedException e) {
            //
        }

        assertEquals(gameController.getControllers().get(1).getPlayer(), gameController.getGame().getPlayers().get(1));
        assertEquals(gameController.getControllers().get(1).getClient(),virtualView2);
    }

    @Test (expected = GameEndedException.class)
    public void addPlayer_virtualViewGiven_shouldGenerateException() throws GameEndedException {
        virtualView2 = new FakeVirtualView(socket2, objectInputStream2, objectOutputStream2);
        try {
            gameController.addPlayer(virtualView2);
        } catch (NullPointerException e) {
            //
        }
        gameController.gameSetUp();
        virtualView3 = new FakeVirtualView(socket3, objectInputStream3, objectOutputStream3);
        try {
            gameController.addPlayer(virtualView3);
        } catch (NullPointerException e) {
            //
        }
    }

    @Test
    public void GameSetUp_noInputGiven_shouldSetUpTheGame() {
        virtualView2 = new FakeVirtualView(socket2, objectInputStream2, objectOutputStream2);
        try {
            gameController.addPlayer(virtualView2);
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
        Card artemis = new Card(
                "Artemis",
                "Goddess of the Hunt",
                "Your Move: Your Worker may move one additional time, " +
                        "\nbut not back to the space it started on.",
                1,
                false,
                gameController.getGame().getDeck().getCards().get(1).getController()
        );
        assertEquals(gameController.getControllers().get(0).getPlayer().getGodCard(),artemis);
        assertEquals(gameController.getControllers().get(1).getPlayer().getGodCard(),apollo);
        assertEquals(gameController.getGame().getPlayers().get(gameController.getGame().getActivePlayer()),gameController.getControllers().get(1).getPlayer());
        assertEquals(gameController.getGame().getBoard().getCell(2,2),gameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition());
        assertEquals(gameController.getGame().getBoard().getCell(2,3),gameController.getGame().getPlayers().get(0).getWorkers().get(1).getPosition());
        assertEquals(gameController.getGame().getBoard().getCell(2,0),gameController.getGame().getPlayers().get(1).getWorkers().get(0).getPosition());
        assertEquals(gameController.getGame().getBoard().getCell(2,1),gameController.getGame().getPlayers().get(1).getWorkers().get(1).getPosition());
        //Alternatively
        assertEquals(gameController.getGame().getBoard().getCell(2,2),gameController.getControllers().get(0).getPlayer().getWorkers().get(0).getPosition());
        assertEquals(gameController.getGame().getBoard().getCell(2,3),gameController.getControllers().get(0).getPlayer().getWorkers().get(1).getPosition());
        assertEquals(gameController.getGame().getBoard().getCell(2,0),gameController.getControllers().get(1).getPlayer().getWorkers().get(0).getPosition());
        assertEquals(gameController.getGame().getBoard().getCell(2,1),gameController.getControllers().get(1).getPlayer().getWorkers().get(1).getPosition());
    }

    @Test
    public void CheckPlayersNumber_noInputGiven_shouldReturnIfThePresetPlayersNumberIsReached() {
        assertEquals(gameController.getGame().getPlayers().size(), gameController.getControllers().size()); //Extra
        assertFalse(gameController.checkPlayersNumber());
        virtualView3 = new FakeVirtualView(socket3, objectInputStream3, objectOutputStream3);
        try {
            gameController.addPlayer(virtualView3);
        } catch (NullPointerException | GameEndedException e) {
            //
        }
        assertTrue(gameController.checkPlayersNumber());
    }

    @Test
    public void CheckWorkers_noInputGiven_shouldEliminateOutOfWorkersPlayers() {
        virtualView2 = new FakeVirtualView(socket2, objectInputStream2, objectOutputStream2);
        try {
            gameController.addPlayer(virtualView2);
        } catch (NullPointerException | GameEndedException e) {
            //
        }
        gameController.gameSetUp();
        gameController.getControllers().get(0).getPlayer().removeWorker(gameController.getGame().getPlayers().get(0).getWorkers().get(1));
        gameController.getControllers().get(0).getPlayer().removeWorker(gameController.getGame().getPlayers().get(0).getWorkers().get(0));
        try {
            gameController.checkWorkers();
        } catch (IOExceptionFromController e) {
            //
        }
        assertTrue(gameController.getControllers().get(0).getPlayer().hasLost());
    }

    @Test
    public void CheckDisconnection_ExceptionAndPlayerControllerGiven_shouldSetNullPlayerControllerIfPlayerLost() {
        virtualView2 = new FakeVirtualView(socket2, objectInputStream2, objectOutputStream2);
        try {
            gameController.addPlayer(virtualView2);
        } catch (NullPointerException | GameEndedException e) {
            //
        }
        gameController.gameSetUp();
        gameController.getControllers().get(1).getPlayer().setLost();

        try {
            gameController.checkDisconnection(new Exception(), gameController.getControllers().get(1));
        } catch (IOExceptionFromController ioExceptionFromController) {
            //
        }
        assertNull(gameController.getControllers().get(1));
    }

    @Test(expected = IOExceptionFromController.class)
    public void CheckDisconnection_noInputGiven_shouldGenerateException() throws IOExceptionFromController {
        virtualView2 = new FakeVirtualView(socket2, objectInputStream2, objectOutputStream2);
        try {
            gameController.addPlayer(virtualView2);
        } catch (NullPointerException | GameEndedException e) {
            //
        }
        gameController.gameSetUp();

        gameController.checkDisconnection(new Exception(), gameController.getControllers().get(1));
    }

    @Test
    public void HandleDisconnection_playerControllerGiven_shouldSetNullPlayerControllerIfHasLost() {
        gameController.getControllers().get(0).getPlayer().setLost();
        gameController.handleDisconnection(gameController.getControllers().get(0));
        assertNull(gameController.getControllers().get(0));
    }

    @Test
    public void HandleDisconnection_playerControllerGiven_shouldRemovePlayerController() {
        virtualView2 = new FakeVirtualView(socket2, objectInputStream2, objectOutputStream2);
        try {
            gameController.addPlayer(virtualView2);
        } catch (NullPointerException | GameEndedException e) {
            //
        }
        gameController.gameSetUp();
        gameController.handleDisconnection(gameController.getControllers().get(0));
        assertEquals(gameController.getControllers().get(0).getClient(),virtualView2);
    }

    @Test
    public void BroadcastBuild() {
    }

    @Test
    public void BroadcastGameInfo() {
    }

    @Test
    public void BroadcastMessage() {
    }

    @Test
    public void BroadcastMove() {
    }

    @Test
    public void BroadcastPlaceWorker() {
    }

    @Test
    public void NotifyDisconnection() {
    }

    @Test
    public void GameOver_noInputGiven_shouldTerminateTheGame() {
        virtualView2 = new FakeVirtualView(socket2, objectInputStream2, objectOutputStream2);
        try {
            gameController.addPlayer(virtualView2);
        } catch (NullPointerException | GameEndedException e) {
            //
        }
        gameController.gameSetUp();
        gameController.gameOver();
        assertFalse(gameController.isRunning());
        assertNull(virtualView1.getPlayerController());
        assertNull(virtualView2.getPlayerController());
    }

}