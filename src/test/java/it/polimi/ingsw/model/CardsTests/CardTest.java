package it.polimi.ingsw.model.CardsTests;

import it.polimi.ingsw.model.Cards.Card;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CardTest {

    Card card=null;

    @Before
    public void setUp() {
        card=new Card("testcard",null);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getGod_NoInputGiven_ShouldReturnGodCardName() {
        assertEquals(card.getGod(),"testcard");
    }

    @Test
    public void getController_NoInputGiven_ShouldReturnGodCardController() {
        assertNull(card.getController());
    }

}