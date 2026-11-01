package cn.edu.sicnu.cardgame.model;

import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import static org.junit.Assert.*;

public class CardMatchingGameTest {
    private CardMatchingGame game(Card... cards) { return new CardMatchingGame(Arrays.asList(cards)); }

    @Test public void rankMatchTakesPriorityOverSuit() {
        Card card = new Card(0, 0);
        assertEquals(4, card.match(new Card[]{new Card(1, 0)}));
        assertEquals(1, card.match(new Card[]{new Card(0, 2)}));
        assertEquals(0, card.match(new Card[]{new Card(2, 2)}));
        assertEquals(0, card.match(new Card[]{}));
        assertEquals(0, card.match(new Card[]{new Card(1, 0), new Card(2, 0)}));
    }
    @Test public void deckContains52UniqueCardsAndBecomesEmpty() {
        Deck deck = new Deck(new Random(42));
        Set<Integer> identities = new HashSet<>();
        for (int i = 0; i < 52; i++) {
            Card card = deck.drawRandomCard();
            assertNotNull(card);
            assertTrue(identities.add(card.getSuit() * 13 + card.getRank()));
        }
        assertEquals(52, identities.size()); assertEquals(0, deck.remaining()); assertNull(deck.drawRandomCard());
    }
    @Test public void firstChoiceCostsOneAndClosingCostsNothing() {
        CardMatchingGame game = game(new Card(0,0),new Card(1,0));
        assertEquals(CardMatchingGame.Turn.CHOSEN,game.chooseCardAtIndex(0));
        assertEquals(-1,game.getScore()); assertTrue(game.cardAtIndex(0).isChosen());
        assertEquals(CardMatchingGame.Turn.CLOSED,game.chooseCardAtIndex(0));
        assertEquals(-1,game.getScore()); assertFalse(game.cardAtIndex(0).isChosen());
    }
    @Test public void rankPairGivesFourteenNetPointsAndLocksBothCards() {
        CardMatchingGame game = game(new Card(0,0),new Card(1,0));
        game.chooseCardAtIndex(0);
        assertEquals(CardMatchingGame.Turn.RANK_MATCH,game.chooseCardAtIndex(1));
        assertEquals(14,game.getScore()); assertEquals(2,game.matchedCount());
        assertTrue(game.isComplete()); assertFalse(game.hasPossibleMatch());
        assertEquals(CardMatchingGame.Turn.IGNORED,game.chooseCardAtIndex(0));
        assertEquals(14,game.getScore());
    }
    @Test public void suitPairGivesTwoNetPoints() {
        CardMatchingGame game = game(new Card(0,0),new Card(0,1));
        game.chooseCardAtIndex(0);
        assertEquals(CardMatchingGame.Turn.SUIT_MATCH,game.chooseCardAtIndex(1));
        assertEquals(2,game.getScore()); assertEquals(2,game.matchedCount());
    }
    @Test public void mismatchCostsFourTotalAndLeavesOnlySecondCardOpen() {
        CardMatchingGame game = game(new Card(0,0),new Card(1,1),new Card(1,2));
        game.chooseCardAtIndex(0);
        assertEquals(CardMatchingGame.Turn.MISMATCH,game.chooseCardAtIndex(1));
        assertEquals(-4,game.getScore()); assertFalse(game.cardAtIndex(0).isChosen());
        assertTrue(game.cardAtIndex(1).isChosen()); assertEquals(0,game.matchedCount());
        assertEquals(CardMatchingGame.Turn.SUIT_MATCH,game.chooseCardAtIndex(2));
        assertEquals(-1,game.getScore());
    }
    @Test public void noRemainingPairCanBeDetectedWithoutChangingScore() {
        CardMatchingGame game = game(new Card(0,0),new Card(1,1),new Card(2,2));
        assertFalse(game.hasPossibleMatch()); assertFalse(game.isComplete()); assertEquals(0,game.getScore());
    }
    @Test public void randomSixteenCardGameHasNoDuplicates() {
        CardMatchingGame game = new CardMatchingGame(16);
        Set<Integer> cards = new HashSet<>();
        for (int i=0;i<game.size();i++) {
            Card card=game.cardAtIndex(i);
            assertTrue(cards.add(card.getSuit()*13+card.getRank()));
        }
        assertTrue(game.hasPossibleMatch()); assertEquals(0,game.getScore());
    }
    @Test public void serializedStateKeepsScoreOrderChosenAndMatchedCards() throws Exception {
        CardMatchingGame game=game(new Card(0,0),new Card(1,0),new Card(2,4),new Card(2,5));
        game.chooseCardAtIndex(0);game.chooseCardAtIndex(1);game.chooseCardAtIndex(2);
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        new ObjectOutputStream(bytes).writeObject(game);
        CardMatchingGame restored=(CardMatchingGame)new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray())).readObject();
        assertEquals(13,restored.getScore());assertEquals(2,restored.matchedCount());
        assertTrue(restored.cardAtIndex(2).isChosen());assertEquals(5,restored.cardAtIndex(3).getRank());
        restored.chooseCardAtIndex(3);assertEquals(16,restored.getScore());assertTrue(restored.isComplete());
    }
    @Test(expected=IllegalArgumentException.class) public void rejectsTooManyCards() { new CardMatchingGame(53); }
    @Test(expected=IllegalArgumentException.class) public void rejectsDuplicateCards() { game(new Card(0,0),new Card(0,0)); }
    @Test public void highestScoreNeverFallsAndHasZeroFloor() {
        PlayerProfile p=new PlayerProfile(null);
        p.recordScore(-4);assertEquals(0,p.getHighestScore());
        p.recordScore(14);p.recordScore(2);assertEquals(14,p.getHighestScore());
    }
}
