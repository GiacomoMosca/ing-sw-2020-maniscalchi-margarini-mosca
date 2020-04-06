package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.game_board.Board;
import it.polimi.ingsw.model.players.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameTest {

    Game game=null;
    Player player1,player2,player3=null;
    OpponentModifier modifier1,modifier2=null;

    @Before
    public void setUp() {
        player1=new Player("Arianna","Giallo");
        player2=new Player("Luigi","Verde");
        player3=new Player("Gian","Cachi");
        modifier1=new OpponentModifier(player1,false);
        modifier2=new OpponentModifier(player2,true);
        game=new Game(player1,2);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getNextPlayer_CurrentActivePlayerGiven_ShouldReturnNextActivePlayer() {
        game.addPlayer(player2);
        assertSame(game.getNextPlayer(player1),player2);
        assertSame(game.getNextPlayer(player2),player1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void getNextPlayer_NotExistingCurrentActivePlayerGiven_ShouldThrowException() {
        game.getNextPlayer(player2);
    }

    @Test
    public void addPlayer_PlayerToAddGiven_ShouldAddANewPlayer() {
        game.addPlayer(player2);
        assertSame(game.getNextPlayer(player1),player2);
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
        assertSame(game.getActivePlayer(),player1);
    }

    @Test
    public void setActivePlayer_NextActivePlayerGiven_ShouldSetNewActivePlayer() {
        game.setActivePlayer(player2);
        assertSame(game.getActivePlayer(),player2);
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