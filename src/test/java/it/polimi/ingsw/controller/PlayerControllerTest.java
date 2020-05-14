package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.turn_controllers.GodController;
import it.polimi.ingsw.controller.turn_controllers.GodControllerConcrete;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.view.VirtualView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static org.junit.Assert.assertEquals;

//not complete
public class PlayerControllerTest {

    private Player player;
    private VirtualView virtualView;
    private PlayerController playerController;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private GameController gameController;
    private GodController godController;

    @Before
    public void setUp() throws Exception {
        player = new Player("player", "color");
        socket = new Socket();
        virtualView = new VirtualView(socket, objectInputStream, objectOutputStream);
        gameController = new GameController(virtualView, 2, "Test");
        playerController = new PlayerController(player, virtualView, gameController);
        godController = new GodControllerConcrete(gameController);
        godController.setPlayer(player, virtualView);
        playerController.setGodController(godController);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getPlayer_noInputGiven_shouldReturnThePlayer() {
        assertEquals(playerController.getPlayer(), player);
    }

    @Test
    public void getClient_noInputGiven_shouldReturnTheClient() {
        assertEquals(playerController.getClient(), virtualView);
    }

    @Test
    public void getGodController_noInputGiven_shouldReturnTheGodController() {
        assertEquals(godController, playerController.getGodController());
    }


    @Test
    public void playTurn() {
    }
}
