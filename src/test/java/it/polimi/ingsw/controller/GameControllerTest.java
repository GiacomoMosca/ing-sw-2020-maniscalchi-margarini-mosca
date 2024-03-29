package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.turn_controllers.*;
import it.polimi.ingsw.exceptions.GameEndedException;
import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;
import it.polimi.ingsw.network.server.FakeLogger;
import it.polimi.ingsw.network.server.Logger;
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
    private FakeVirtualView virtualView1, virtualView2, virtualView3, virtualView4;
    private Socket socket1, socket2, socket3,socket4;
    private ObjectInputStream objectInputStream1, objectInputStream2, objectInputStream3, objectInputStream4;
    private ObjectOutputStream objectOutputStream1, objectOutputStream2, objectOutputStream3, objectOutputStream4;
    private GameController gameController;
    private FakePlayerController playerController1, playerController2;
    private Player player1, player2;
    private Game game;

    private FakeLogger fakeLogger;

    @Before
    public void setUp() {
        virtualView1 = new FakeVirtualView(socket1, objectInputStream1, objectOutputStream1);
        virtualView2 = new FakeVirtualView(socket2,objectInputStream2,objectOutputStream2);
        gameController = new GameController(virtualView1, 3, "Test");
        player1 = new Player("Luca","Green");
        player2 = new Player("Rebe", "Blu");
        playerController1 = new FakePlayerController(player1,virtualView1,gameController);
        playerController2 = new FakePlayerController(player2,virtualView2,gameController);
        virtualView1.setPlayerController(playerController1);
        virtualView2.setPlayerController(playerController2);
        game = new Game("test",player1,3);
        try {
            fakeLogger = new FakeLogger();
        } catch (IOException e) {
            //
        }
        gameController.setLogger(fakeLogger);
        gameController.setServer(null);
        gameController.game = game;
        gameController.game.addPlayer(player2);
        gameController.playerControllers.remove(0);
        gameController.playerControllers.add(playerController1);
        gameController.playerControllers.add(playerController2);
    }

    @After
    public void tearDown() {
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
        assertEquals(gameController.getControllers().get(1).getPlayer(), gameController.getGame().getPlayers().get(1));
    }

    @Test
    public void addPlayer_virtualViewGiven_shouldAddThePlayer() {
        virtualView3 = new FakeVirtualView(socket3, objectInputStream3, objectOutputStream3);
        try {
            gameController.addPlayer(virtualView3);
        } catch (NullPointerException | GameEndedException e) {
            //
        }

        assertEquals(gameController.getControllers().get(2).getPlayer(), gameController.getGame().getPlayers().get(2));
        assertEquals(gameController.getControllers().get(2).getClient(),virtualView3);
    }

    @Test (expected = GameEndedException.class)
    public void addPlayer_virtualViewGiven_shouldGenerateException() throws GameEndedException {
        virtualView3 = new FakeVirtualView(socket3, objectInputStream3, objectOutputStream3);
        try {
            gameController.addPlayer(virtualView3);
        } catch (NullPointerException e) {
            //
        }
        gameController.gameSetUp();
        virtualView4 = new FakeVirtualView(socket4, objectInputStream4, objectOutputStream4);
        try {
            gameController.addPlayer(virtualView4);
        } catch (NullPointerException e) {
            //
        }
    }

    @Test
    public void addPlayer_virtualViewGiven_shouldReturnWithoutAddingThePlayerAndWriteTheLog() throws GameEndedException {
        virtualView3 = new FakeVirtualView(socket3, objectInputStream3, objectOutputStream3);
        try {
            gameController.addPlayer(virtualView3);
        } catch (NullPointerException e) {
            //
        }

        virtualView4 = new FakeVirtualView(socket4, objectInputStream4, objectOutputStream4);
        try {
            gameController.addPlayer(virtualView4);
        } catch (NullPointerException e) {
            //
        }
    }

    @Test
    public void GameSetUp_noInputGiven_shouldSetUpTheGame() {
        virtualView3 = new FakeVirtualView(socket3, objectInputStream3, objectOutputStream3);
        try {
            gameController.addPlayer(virtualView3);
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

        Card limus = new Card(
                "Limus",
                "Goddess of Famine",
                "Opponent’s Turn: Opponent Workers cannot build on spaces neighboring " +
                        "\nyour Workers, unless building a dome to create a Complete Tower.",
                2,
                true,
                gameController.getGame().getDeck().getCards().get(7).getController()
        );

        assertEquals(gameController.getControllers().get(0).getPlayer().getGodCard(),limus);
        assertEquals(gameController.getControllers().get(1).getPlayer().getGodCard(),apollo);
        assertEquals(gameController.getControllers().get(2).getPlayer().getGodCard(),athena);
        assertEquals(gameController.getGame().getPlayers().get(gameController.getGame().getActivePlayer()),gameController.getControllers().get(1).getPlayer());

        assertEquals(gameController.getGame().getBoard().getCell(2,4),gameController.getGame().getPlayers().get(0).getWorkers().get(0).getPosition());
        assertEquals(gameController.getGame().getBoard().getCell(3,0),gameController.getGame().getPlayers().get(0).getWorkers().get(1).getPosition());
        assertEquals(gameController.getGame().getBoard().getCell(2,0),gameController.getGame().getPlayers().get(1).getWorkers().get(0).getPosition());
        assertEquals(gameController.getGame().getBoard().getCell(2,1),gameController.getGame().getPlayers().get(1).getWorkers().get(1).getPosition());
        assertEquals(gameController.getGame().getBoard().getCell(2,2),gameController.getGame().getPlayers().get(2).getWorkers().get(0).getPosition());
        assertEquals(gameController.getGame().getBoard().getCell(2,3),gameController.getGame().getPlayers().get(2).getWorkers().get(1).getPosition());

        assertEquals(gameController.getGame().getBoard().getCell(2,4),gameController.getControllers().get(0).getPlayer().getWorkers().get(0).getPosition());
        assertEquals(gameController.getGame().getBoard().getCell(3,0),gameController.getControllers().get(0).getPlayer().getWorkers().get(1).getPosition());
        assertEquals(gameController.getGame().getBoard().getCell(2,0),gameController.getControllers().get(1).getPlayer().getWorkers().get(0).getPosition());
        assertEquals(gameController.getGame().getBoard().getCell(2,1),gameController.getControllers().get(1).getPlayer().getWorkers().get(1).getPosition());
        assertEquals(gameController.getGame().getBoard().getCell(2,2),gameController.getControllers().get(2).getPlayer().getWorkers().get(0).getPosition());
        assertEquals(gameController.getGame().getBoard().getCell(2,3),gameController.getControllers().get(2).getPlayer().getWorkers().get(1).getPosition());
    }

    @Test
    public void CheckPlayersNumber_noInputGiven_shouldReturnIfThePresetPlayersNumberIsReached() {
        assertEquals(gameController.getGame().getPlayers().size(), gameController.getControllers().size());
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
        virtualView3 = new FakeVirtualView(socket3, objectInputStream3, objectOutputStream3);
        try {
            gameController.addPlayer(virtualView3);
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
        virtualView3 = new FakeVirtualView(socket3, objectInputStream3, objectOutputStream3);
        try {
            gameController.addPlayer(virtualView3);
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
        virtualView3 = new FakeVirtualView(socket3, objectInputStream3, objectOutputStream3);
        try {
            gameController.addPlayer(virtualView3);
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
        virtualView3 = new FakeVirtualView(socket3, objectInputStream3, objectOutputStream3);
        try {
            gameController.addPlayer(virtualView3);
        } catch (NullPointerException | GameEndedException e) {
            //
        }
        gameController.gameSetUp();
        revOemag();
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

    public void revOemag (){
        gameController.running.compareAndSet(false, true);
        int i=0;
        for (PlayerController controller : gameController.playerControllers) {
            controller.getClient().setPlayerController(gameController.getControllers().get(i));
            i++;
        }
    }

}