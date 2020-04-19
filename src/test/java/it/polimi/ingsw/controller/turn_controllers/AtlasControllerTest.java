package it.polimi.ingsw.controller.turn_controllers;

import it.polimi.ingsw.controller.FakeGameController;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.players.Player;
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

    @Before
    public void setUp(){
        cli = new FakeCLI();
        playerInterface = new PlayerInterface(cli);
        atlas = new AtlasController(fakeGameController);
        fakeGameController = new FakeGameController(playerInterface,1,atlas);
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
        fakeGameController.addPlayer(playerInterface);
        assertTrue(fakeGameController.getGame().getBoard().getCell(0,0).isDomed());
    }
}