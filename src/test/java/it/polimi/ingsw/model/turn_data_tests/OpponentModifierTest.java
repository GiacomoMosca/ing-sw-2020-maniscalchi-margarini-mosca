package it.polimi.ingsw.model.turn_data_tests;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.OpponentModifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class OpponentModifierTest {

    OpponentModifier modifier=null;
    Player player=null;
    Card card=null;

    @Before
    public void setUp() {
        card=new Card("Figi",null);
        player=new Player("Dani","Nero");
        player.setGodCard(card);
        modifier=new OpponentModifier(player,true);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getPlayer_NoInputGiven_ShouldReturnPlayerLinkedWithModifier() {
        assertEquals(modifier.getPlayer(),player);
    }

    @Test
    public void getGodCard_NoInputGiven_ShouldReturnGodCardLinkedWithModifier() {
        assertEquals(modifier.getGodCard(),card);
    }

    @Test
    public void isAlwaysActive_NoInputGiven_ShouldReturnIfModifiersIsAlwaysActiveOrNot() {
        assertTrue(modifier.isAlwaysActive());
    }
}