package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.turn_controllers.GodController;
import it.polimi.ingsw.controller.turn_controllers.GodControllerConcrete;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.game_board.Board;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.view.VirtualView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static org.junit.Assert.*;

public class GameTest {

    GameController gamecontroller;
    Game game = null;
    Player player1, player2, player3 = null;
    Card modifier1, modifier2 = null;
    GodController godcontroller1, godcontroller2;
    Socket socket;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    @Before
    public void setUp() {
        player1 = new Player("Arianna", "Giallo");
        player2 = new Player("Luigi", "Verde");
        player3 = new Player("Gian", "Cachi");
        game = new Game("Test", player1, 3);
        gamecontroller = new GameController(new VirtualView(socket, objectInputStream, objectOutputStream), 3, "Test");
        godcontroller1 = new GodControllerConcrete(gamecontroller);
        godcontroller2 = new GodControllerConcrete(gamecontroller);
        modifier1 = new Card("dio1", "a", "b", 1, false, godcontroller1);
        modifier2 = new Card("dio2", "d", "e", 1, true, godcontroller2);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void nextPlayer_noInputGiven_shouldSetTheNextPlayer() {
        game.addPlayer(player2);
        game.addPlayer(player3);
        assertEquals(game.getPlayers().get(game.getActivePlayer()), player1);
        game.nextPlayer();
        assertEquals(game.getPlayers().get(game.getActivePlayer()), player2);
        game.nextPlayer();
        assertEquals(game.getPlayers().get(game.getActivePlayer()), player3);
        game.nextPlayer();
        assertEquals(game.getPlayers().get(game.getActivePlayer()), player1);
    }

    @Test
    public void setActivePlayer_PlayerNumberGiven_shouldSetTheActivePlayer() {
        game.addPlayer(player2);
        game.addPlayer(player3);
        game.setActivePlayer(0);
        assertEquals(game.getPlayers().get(game.getActivePlayer()), player1);
        game.setActivePlayer(1);
        assertEquals(game.getPlayers().get(game.getActivePlayer()), player2);
        game.setActivePlayer(2);
        assertEquals(game.getPlayers().get(game.getActivePlayer()), player3);
        game.setActivePlayer(3);
        assertEquals(game.getPlayers().get(game.getActivePlayer()), player3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void addPlayer_PlayerGiven_ShouldThrowException() {
        game.addPlayer(player2);
        game.addPlayer(player3);
        game.addPlayer(new Player("player4", "viola"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addPlayer_ExistingPlayerGiven_ShouldThrowException() {
        game.addPlayer(player1);
    }

    @Test
    public void getPlayerNum_NoInputGiven_ShouldReturnNumberOfPlayer() {
        game.addPlayer(player2);
        game.addPlayer(player3);
        assertEquals(game.getPlayerNum(), 3);
    }

    @Test
    public void getBoard_NoInoutGiven_ShouldReturnBoard() {
        Board board = new Board();
        assertEquals(board, game.getBoard());
    }

    @Test
    public void getDeck_NoInoutGiven_ShouldReturnDeck() {
        Deck deck = new Deck();
        assertEquals(deck, game.getDeck());

    }

    @Test
    public void getActivePlayer_NoInputGiven_ShouldReturnActivePlayer() {
        assertEquals(game.getActivePlayer(), 0);
        assertSame(game.getPlayers().get(game.getActivePlayer()), player1);
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
        assertSame(game.getWinner(), player1);
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
        assertSame(game.getWinner(), player1);
        assertTrue(game.hasWinner());
    }

    @Test
    public void getName_NoInputGiven_ShouldReturnName() {
        assertSame(game.getName(), "Test");
    }
}