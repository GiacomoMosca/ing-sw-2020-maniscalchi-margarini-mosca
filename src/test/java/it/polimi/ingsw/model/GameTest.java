package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.turn_controllers.GodController;
import it.polimi.ingsw.controller.turn_controllers.GodControllerConcrete;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.game_board.Board;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.view.client.CLI;
import it.polimi.ingsw.view.PlayerInterface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameTest {

    GameController gamecontroller;
    Game game=null;
    Player player1,player2,player3=null;
    Card modifier1,modifier2=null;
    GodController godcontroller1,godcontroller2;

    @Before
    public void setUp() {
        player1=new Player("Arianna","Giallo");
        player2=new Player("Luigi","Verde");
        player3=new Player("Gian","Cachi");
        game=new Game(player1,2);
        gamecontroller=new GameController(new PlayerInterface(new CLI()),2);
        godcontroller1=new GodControllerConcrete(gamecontroller);
        godcontroller2=new GodControllerConcrete(gamecontroller);
        modifier1=new Card("dio1", "a", "b",1, false, godcontroller1);
        modifier1=new Card("dio2", "d", "e",1, true, godcontroller2);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getNextPlayer_CurrentActivePlayerGiven_ShouldReturnNextActivePlayer() {
        game.addPlayer(player2);
        assertEquals(game.getNextPlayer(),1);
        assertSame(game.getPlayers().get(game.getActivePlayer()),player2);
        assertEquals(game.getNextPlayer(),0);
        assertSame(game.getPlayers().get(game.getActivePlayer()),player1);
    }

    @Test
    public void addPlayer_PlayerToAddGiven_ShouldAddANewPlayer() {
        game.addPlayer(player2);
        assertEquals(game.getNextPlayer(),1);
        assertSame(game.getPlayers().get(game.getActivePlayer()),player2);
        assertTrue(game.getPlayers().contains(player1));
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void addPlayer_PlayerGiven_ShouldThrowException() {
        game.addPlayer(player2);
        game.addPlayer(player3);
    }

    @Test (expected = IllegalArgumentException.class)
    public void addPlayer_ExistingPlayerGiven_ShouldThrowException() {
        game.addPlayer(player1);
    }

    @Test
    public void getPlayerNum_NoInputGiven_ShouldReturnNumberOfPlayer() {
        assertEquals(game.getPlayerNum(),2);
    }

    @Test
    public void getBoard_NoInoutGiven_ShouldReturnBoard() {
        Board board=new Board();
        assertEquals(game.getBoard(),board);
    }

    @Test
    public void getDeck_NoInoutGiven_ShouldReturnDeck() {
        Deck deck=new Deck();
        assertEquals(game.getDeck(),deck);
    }

    @Test
    public void getActivePlayer_NoInputGiven_ShouldReturnActivePlayer() {
        assertEquals(game.getActivePlayer(),0);
        assertSame(game.getPlayers().get(game.getActivePlayer()),player1);
    }

    @Test
    public void getActiveModifiers_NoInputGiven_ShouldReturnActiveModifiers() {
        game.addModifier(modifier1);
        game.addModifier(modifier2);
        assertTrue(game.getActiveModifiers().contains(modifier1));
        assertTrue(game.getActiveModifiers().contains(modifier2));
    }

    @Test
    public void addModifier_ModifierToAddGiven_ShouldAddNewModifier() {
        game.addModifier(modifier1);
        assertTrue(game.getActiveModifiers().contains(modifier1));
        game.addModifier(modifier2);
        assertTrue(game.getActiveModifiers().contains(modifier2));
        assertTrue(game.getActiveModifiers().contains(modifier1));
    }

    @Test
    public void removeModifier_ModifierToRemoveGiven_ShouldRemoveModifier() {
        game.addModifier(modifier1);
        game.addModifier(modifier2);
        game.removeModifier(modifier1);
        assertFalse(game.getActiveModifiers().contains(modifier1));
        assertTrue(game.getActiveModifiers().contains(modifier2));
    }

    @Test
    public void getWinner_NoInputGiven_ShouldReturnWinner() {
        assertNull(game.getWinner());
        game.setWinner(player1);
        assertSame(game.getWinner(),player1);
    }

    @Test
    public void hasWinner_NoInputGiven_ShouldReturnIfThereIsAWinner() {
        assertFalse(game.hasWinner());
        game.setWinner(player1);
        assertTrue(game.hasWinner());
    }

    @Test
    public void setWinner_WinnerGiven_ShouldSetTheWinner() {
        game.setWinner(player1);
        assertSame(game.getWinner(),player1);
        assertTrue(game.hasWinner());
    }
}