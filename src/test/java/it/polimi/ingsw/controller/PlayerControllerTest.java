package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.turn_controllers.GodController;
import it.polimi.ingsw.controller.turn_controllers.GodControllerConcrete;
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

//not complete
public class PlayerControllerTest {

    private FakeVirtualView fakeVirtualView;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private GameController gameController;

    @Before
    public void setUp() throws Exception {
        socket = new Socket();
        fakeVirtualView = new FakeVirtualView(socket, objectInputStream, objectOutputStream);
        gameController = new GameController(fakeVirtualView, 2, "Test");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getPlayer_noInputGiven_shouldReturnThePlayer() {
        //assertEquals(playerController.getPlayer(), player);
    }

    @Test
    public void getClient_noInputGiven_shouldReturnTheClient() {
        //assertEquals(playerController.getClient(), fakeVirtualView);
    }

    @Test
    public void getGame() {
    }

    @Test
    public void getGodController_noInputGiven_shouldReturnTheGodController() {
        //assertEquals(godController, playerController.getGodController());
    }

    @Test
    public void setGodController() {
    }

    @Test
    public void playTurn() {
    }
}
