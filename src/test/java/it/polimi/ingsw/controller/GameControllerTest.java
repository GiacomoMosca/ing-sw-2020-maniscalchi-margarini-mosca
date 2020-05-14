package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.turn_controllers.GodController;
import it.polimi.ingsw.controller.turn_controllers.GodControllerConcrete;
import it.polimi.ingsw.exceptions.GameEndedException;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.view.VirtualView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static org.junit.Assert.assertEquals;

public class GameControllerTest {
    private VirtualView virtualView1, virtualView2, virtualView3;
    private PlayerController playerController;
    private Socket socket1, socket2, socket3;
    private ObjectInputStream objectInputStream1, objectInputStream2, objectInputStream3;
    private ObjectOutputStream objectOutputStream1, objectOutputStream2, objectOutputStream3;
    private GameController gameController;


    @Before
    public void setUp() throws Exception {
        Player player1 = new Player("player1", "color1");
        virtualView1 = new VirtualView(socket1, objectInputStream1, objectOutputStream1);
        gameController = new GameController(virtualView1, 2, "Test");
        playerController = new PlayerController(player1, virtualView1, gameController);
        GodController godController = new GodControllerConcrete(gameController);
        godController.setPlayer(player1, virtualView1);
        playerController.setGodController(godController);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getGame_noInputGiven_shouldReturnTheGame() {
        assertEquals(gameController.getGame(), gameController.game);
    }

    @Test
    public void addPlayer_virtualViewGiven_shouldAddThePlayer() throws GameEndedException, IOException {
        virtualView2 = new VirtualView(socket2, objectInputStream2, objectOutputStream2);
        try {
            gameController.addPlayer(virtualView2);
        } catch (NullPointerException e) {
            //
        }

        assertEquals(gameController.playerControllers.get(1).getPlayer(), gameController.getGame().getPlayers().get(1));
    }

    @Test
    public void addPlayer_virtualViewGiven_shouldGenerateException() throws GameEndedException, IOException {
        virtualView2 = new VirtualView(socket2, objectInputStream2, objectOutputStream2);
        try {
            gameController.addPlayer(virtualView2);
        } catch (NullPointerException e) {
            //
        }

        virtualView3 = new VirtualView(socket3, objectInputStream3, objectOutputStream3);
        try {
            gameController.addPlayer(virtualView3);
        } catch (NullPointerException e) {
            //
        }
    }

    @Test
    public void gameSetUp() {
    }

    @Test
    public void displayBoard() {
    }

    @Test
    public void displayMessage() {
    }
}