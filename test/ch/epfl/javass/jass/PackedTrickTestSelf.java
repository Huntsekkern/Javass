package ch.epfl.javass.jass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.bits.Bits32;

public class PackedTrickTestSelf {
    @Test
    void invalidIsCorrect() {
        System.out.println(Integer.toBinaryString(Bits32.mask(0, 32)));
        assertEquals(Bits32.mask(0, 32), PackedTrick.INVALID);
    }

    @Test
    void isValidWorksForInvalidTrick() {
        assertFalse(PackedTrick.isValid(PackedTrick.INVALID));
    }
    
    @Test
    void isValidWorksForFirstTrick() {
        assertTrue(PackedTrick.isValid(PackedTrick.firstEmpty(Card.Color.HEART, PlayerId.PLAYER_2)));
        assertTrue(PackedTrick.isValid(PackedTrick.firstEmpty(Card.Color.DIAMOND, PlayerId.PLAYER_1)));
        assertTrue(PackedTrick.isValid(PackedTrick.firstEmpty(Card.Color.SPADE, PlayerId.PLAYER_3)));
        assertTrue(PackedTrick.isValid(PackedTrick.firstEmpty(Card.Color.CLUB, PlayerId.PLAYER_4)));
    }

    @Test
    void isValidWorksForRandomCases() {
        assertTrue(PackedTrick.isValid(Bits32.pack(PackedCard.pack(Card.Color.DIAMOND, Card.Rank.ACE), 6, PackedCard.pack(Card.Color.DIAMOND, Card.Rank.SIX), 6,PackedCard.pack(Card.Color.DIAMOND, Card.Rank.TEN), 6, PackedCard.INVALID, 6, 3, 4, PlayerId.PLAYER_1.ordinal(), 2, Card.Color.SPADE.ordinal(), 2)));
        assertTrue(PackedTrick.isValid(Bits32.pack(PackedCard.pack(Card.Color.DIAMOND, Card.Rank.ACE), 6, PackedCard.pack(Card.Color.DIAMOND, Card.Rank.SIX), 6,PackedCard.pack(Card.Color.SPADE, Card.Rank.TEN), 6, PackedCard.INVALID, 6, 8, 4, PlayerId.PLAYER_4.ordinal(), 2, Card.Color.CLUB.ordinal(), 2)));
    }

    @Test
    void isValidWorksWhenIndexTooBig() throws Exception {
        assertFalse(PackedTrick.isValid(Bits32.pack(PackedCard.pack(Card.Color.DIAMOND, Card.Rank.ACE), 6, PackedCard.pack(Card.Color.DIAMOND, Card.Rank.SIX), 6,PackedCard.pack(Card.Color.DIAMOND, Card.Rank.TEN), 6, PackedCard.INVALID, 6, 9, 4, PlayerId.PLAYER_1.ordinal(), 2, Card.Color.SPADE.ordinal(), 2)));
        assertFalse(PackedTrick.isValid(Bits32.pack(PackedCard.pack(Card.Color.DIAMOND, Card.Rank.ACE), 6, PackedCard.pack(Card.Color.DIAMOND, Card.Rank.SIX), 6,PackedCard.pack(Card.Color.DIAMOND, Card.Rank.TEN), 6, PackedCard.INVALID, 6, 15, 4, PlayerId.PLAYER_1.ordinal(), 2, Card.Color.SPADE.ordinal(), 2)));
    }

    @Test
    void isValidWorksIfInvalidCardBeforeValid() throws Exception {
        assertFalse(PackedTrick.isValid(Bits32.pack(PackedCard.INVALID, 6, PackedCard.pack(Card.Color.DIAMOND, Card.Rank.SIX), 6,PackedCard.pack(Card.Color.DIAMOND, Card.Rank.TEN), 6, PackedCard.INVALID, 6, 3, 4, PlayerId.PLAYER_1.ordinal(), 2, Card.Color.SPADE.ordinal(), 2)));
        assertFalse(PackedTrick.isValid(Bits32.pack(PackedCard.pack(Card.Color.DIAMOND, Card.Rank.ACE), 6, PackedCard.INVALID, 6,PackedCard.pack(Card.Color.DIAMOND, Card.Rank.TEN), 6, PackedCard.INVALID, 6, 3, 4, PlayerId.PLAYER_1.ordinal(), 2, Card.Color.SPADE.ordinal(), 2)));
    }
    
    @Test
    void firstEmptyWorks() {
        assertEquals(Integer.parseInt("00000000111111111111111111111111",2), PackedTrick.firstEmpty(Card.Color.SPADE, PlayerId.PLAYER_1));
    }
    
    @Test
    void nextEmptyAndWithAddedCardWork() {
        int trick = PackedTrick.firstEmpty(Card.Color.SPADE, PlayerId.PLAYER_1);

        trick = PackedTrick.withAddedCard(trick, PackedCard.pack(Card.Color.SPADE, Card.Rank.TEN));
        assertEquals(Integer.parseInt("00000000111111111111111111000100",2), trick);

        trick = PackedTrick.withAddedCard(trick, PackedCard.pack(Card.Color.SPADE, Card.Rank.SIX));
        assertEquals(Integer.parseInt("00000000111111111111000000000100",2), trick);
        
        trick = PackedTrick.withAddedCard(trick, PackedCard.pack(Card.Color.SPADE, Card.Rank.JACK));
        assertEquals(Integer.parseInt("00000000111111000101000000000100",2), trick);
        
        trick = PackedTrick.withAddedCard(trick, PackedCard.pack(Card.Color.SPADE, Card.Rank.EIGHT));
        assertEquals(Integer.parseInt("00000000000010000101000000000100",2), trick);
        
        trick = PackedTrick.nextEmpty(trick);
        assertEquals(Integer.parseInt("00100001111111111111111111111111",2), trick);
        
    }
    
    @Test
    void sizeIsEmptyAndisFullWork() {
        int trick = PackedTrick.firstEmpty(Card.Color.SPADE, PlayerId.PLAYER_1);
        
        assertTrue(PackedTrick.isEmpty(trick));
        assertFalse(PackedTrick.isFull(trick));
        assertEquals(0, PackedTrick.size(trick));

        trick = PackedTrick.withAddedCard(trick, PackedCard.pack(Card.Color.SPADE, Card.Rank.TEN));
        assertEquals(Integer.parseInt("00000000111111111111111111000100",2), trick);
        assertFalse(PackedTrick.isEmpty(trick));
        assertFalse(PackedTrick.isFull(trick));
        assertEquals(1, PackedTrick.size(trick));
        
        trick = PackedTrick.withAddedCard(trick, PackedCard.pack(Card.Color.SPADE, Card.Rank.SIX));
        assertEquals(Integer.parseInt("00000000111111111111000000000100",2), trick);
        assertFalse(PackedTrick.isEmpty(trick));
        assertFalse(PackedTrick.isFull(trick));
        assertEquals(2, PackedTrick.size(trick));
        
        trick = PackedTrick.withAddedCard(trick, PackedCard.pack(Card.Color.SPADE, Card.Rank.JACK));
        assertEquals(Integer.parseInt("00000000111111000101000000000100",2), trick);
        assertFalse(PackedTrick.isEmpty(trick));
        assertFalse(PackedTrick.isFull(trick));
        assertEquals(3, PackedTrick.size(trick));
        
        trick = PackedTrick.withAddedCard(trick, PackedCard.pack(Card.Color.SPADE, Card.Rank.EIGHT));
        assertEquals(Integer.parseInt("00000000000010000101000000000100",2), trick);
        assertFalse(PackedTrick.isEmpty(trick));
        assertTrue(PackedTrick.isFull(trick));
        assertEquals(4, PackedTrick.size(trick));
        
        trick = PackedTrick.nextEmpty(trick);
        assertEquals(Integer.parseInt("00100001111111111111111111111111",2), trick);
        
        assertTrue(PackedTrick.isEmpty(trick));
        assertFalse(PackedTrick.isFull(trick));
        assertEquals(0, PackedTrick.size(trick));   
    }

    @Test
    void isLastWorks() {
        int trick = PackedTrick.firstEmpty(Card.Color.SPADE, PlayerId.PLAYER_1);
        assertFalse(PackedTrick.isLast(trick));
        
        assertFalse(PackedTrick.isLast(Integer.parseInt("00100011111111111111111111111111",2)));
        assertFalse(PackedTrick.isLast(Integer.parseInt("00100111111111111111111111111111",2)));
        assertTrue(PackedTrick.isLast(Integer.parseInt("00101000111111111111111111111111",2)));
        assertFalse(PackedTrick.isLast(Integer.parseInt("00100101111111111111111111111111",2)));
        
    }
    
    @Test
    void indexPlayerTrumpCardBaseColorAndWinningPlayerAndPointsAndToStringWork() {
        int trick = PackedTrick.firstEmpty(Card.Color.SPADE, PlayerId.PLAYER_1);
        
        assertTrue(PackedTrick.isValid(trick));

        
        assertEquals(PlayerId.PLAYER_1, PackedTrick.player(trick, 0));

        assertEquals(0, PackedTrick.index(trick));
        
        assertEquals(Card.Color.SPADE, PackedTrick.trump(trick));
        
        System.out.println(PackedTrick.toString(trick));

        trick = PackedTrick.withAddedCard(trick, PackedCard.pack(Card.Color.SPADE, Card.Rank.TEN));
        System.out.println(PackedTrick.toString(trick));
        
        assertEquals(PlayerId.PLAYER_1, PackedTrick.winningPlayer(trick));

        assertEquals(Card.Color.SPADE, PackedTrick.baseColor(trick));
        assertEquals(PackedCard.pack(Card.Color.SPADE, Card.Rank.TEN), PackedTrick.card(trick, 0));

        trick = PackedTrick.withAddedCard(trick, PackedCard.pack(Card.Color.DIAMOND, Card.Rank.SIX));
        System.out.println(PackedTrick.toString(trick));
        
        assertEquals(PlayerId.PLAYER_1, PackedTrick.winningPlayer(trick));

        assertEquals(Card.Color.SPADE, PackedTrick.baseColor(trick));
        assertEquals(0, PackedTrick.index(trick));
        assertEquals(PlayerId.PLAYER_1, PackedTrick.player(trick, 0));
        assertEquals(PlayerId.PLAYER_2, PackedTrick.player(trick, 1));
        assertEquals(Card.Color.SPADE, PackedTrick.trump(trick));
        
        assertEquals(PackedCard.pack(Card.Color.SPADE, Card.Rank.TEN), PackedTrick.card(trick, 0));
        assertEquals(PackedCard.pack(Card.Color.DIAMOND, Card.Rank.SIX), PackedTrick.card(trick, 1));
        
        trick = PackedTrick.withAddedCard(trick, PackedCard.pack(Card.Color.SPADE, Card.Rank.JACK));
        System.out.println(PackedTrick.toString(trick));
        
        assertEquals(PlayerId.PLAYER_3, PackedTrick.winningPlayer(trick));

        assertEquals(Card.Color.SPADE, PackedTrick.baseColor(trick));
        assertEquals(PackedCard.pack(Card.Color.SPADE, Card.Rank.TEN), PackedTrick.card(trick, 0));
        assertEquals(PackedCard.pack(Card.Color.DIAMOND, Card.Rank.SIX), PackedTrick.card(trick, 1));
        assertEquals(PackedCard.pack(Card.Color.SPADE, Card.Rank.JACK), PackedTrick.card(trick, 2));
        
        trick = PackedTrick.withAddedCard(trick, PackedCard.pack(Card.Color.SPADE, Card.Rank.EIGHT));
        System.out.println(PackedTrick.toString(trick));
        
        assertEquals(PlayerId.PLAYER_3, PackedTrick.winningPlayer(trick));

        assertEquals(Card.Color.SPADE, PackedTrick.baseColor(trick));
        assertEquals(0, PackedTrick.index(trick));
        assertEquals(Card.Color.SPADE, PackedTrick.trump(trick));
       
        assertEquals(PackedCard.pack(Card.Color.SPADE, Card.Rank.TEN), PackedTrick.card(trick, 0));
        assertEquals(PackedCard.pack(Card.Color.DIAMOND, Card.Rank.SIX), PackedTrick.card(trick, 1));
        assertEquals(PackedCard.pack(Card.Color.SPADE, Card.Rank.JACK), PackedTrick.card(trick, 2));
        assertEquals(PackedCard.pack(Card.Color.SPADE, Card.Rank.EIGHT), PackedTrick.card(trick, 3));
        
        assertEquals(30, PackedTrick.points(trick));
        
        trick = PackedTrick.nextEmpty(trick);
        System.out.println(PackedTrick.toString(trick));

        
        assertEquals(PlayerId.PLAYER_3, PackedTrick.player(trick, 0));
        assertEquals(PlayerId.PLAYER_4, PackedTrick.player(trick, 1));
        assertEquals(1, PackedTrick.index(trick));
        assertEquals(Card.Color.SPADE, PackedTrick.trump(trick));
        
    }
    
    @Test 
    void playableCardsWorks() {
        int trick = PackedTrick.firstEmpty(Card.Color.HEART, PlayerId.PLAYER_4);
        
        // to test: all regular cases (cf website) + the case with only a bour and a six when somebody cut before
        long hand4 = PackedCardSet.add(PackedCardSet.singleton(PackedCard.pack(Card.Color.DIAMOND, Card.Rank.ACE)), PackedCard.pack(Card.Color.SPADE, Card.Rank.JACK));
        long hand1 = PackedCardSet.add(PackedCardSet.singleton(PackedCard.pack(Card.Color.HEART, Card.Rank.QUEEN)), PackedCard.pack(Card.Color.CLUB, Card.Rank.KING));
        long hand2 = PackedCardSet.add(PackedCardSet.singleton(PackedCard.pack(Card.Color.HEART, Card.Rank.SEVEN)), PackedCard.pack(Card.Color.HEART, Card.Rank.JACK));
        long hand3 = PackedCardSet.add(PackedCardSet.singleton(PackedCard.pack(Card.Color.DIAMOND, Card.Rank.TEN)), PackedCard.pack(Card.Color.HEART, Card.Rank.TEN));
        // has ace diamond + spade jack
        
        System.out.println(PackedCardSet.toString(hand4));
        System.out.println(PackedCardSet.toString(hand1));
        System.out.println(PackedCardSet.toString(hand2));
        System.out.println(PackedCardSet.toString(hand3));
        
        System.out.println("--");
        
        System.out.println(PackedCardSet.toString(PackedTrick.playableCards(trick, hand4)));
        
        trick = PackedTrick.withAddedCard(trick, PackedCard.pack(Card.Color.DIAMOND, Card.Rank.ACE));
        
        // has queen heart + club king
        System.out.println(PackedCardSet.toString(PackedTrick.playableCards(trick, hand1)));
        trick = PackedTrick.withAddedCard(trick, PackedCard.pack(Card.Color.HEART, Card.Rank.QUEEN));
        
        //has jack and seven of hearts
        //proves that no undercut predominates on not-Bour forced
        System.out.println(PackedCardSet.toString(PackedTrick.playableCards(trick, hand2)));
        trick = PackedTrick.withAddedCard(trick, PackedCard.pack(Card.Color.HEART, Card.Rank.JACK));
        
        // had ten diamond + ten hearts
        // proves that can't undercut
        System.out.println(PackedCardSet.toString(PackedTrick.playableCards(trick, hand3)));
        trick = PackedTrick.withAddedCard(trick, PackedCard.pack(Card.Color.DIAMOND, Card.Rank.TEN));
        
        assertTrue(PackedTrick.isFull(trick));
        assertFalse(PackedTrick.isLast(trick));
        
        trick = PackedTrick.nextEmpty(trick);
        System.out.println(PackedCardSet.toString(PackedTrick.playableCards(trick, hand4)));
        
        
    }
    
    @Test 
    void playableCardsWorks2() {
        int trick = PackedTrick.firstEmpty(Card.Color.HEART, PlayerId.PLAYER_4);
        
        // to test: Bour is ot a mandatory play
        long hand4 = PackedCardSet.add(PackedCardSet.singleton(PackedCard.pack(Card.Color.HEART, Card.Rank.ACE)), PackedCard.pack(Card.Color.SPADE, Card.Rank.JACK));
        long hand1 = PackedCardSet.add(PackedCardSet.singleton(PackedCard.pack(Card.Color.HEART, Card.Rank.QUEEN)), PackedCard.pack(Card.Color.CLUB, Card.Rank.KING));
        long hand2 = PackedCardSet.add(PackedCardSet.singleton(PackedCard.pack(Card.Color.CLUB, Card.Rank.SEVEN)), PackedCard.pack(Card.Color.HEART, Card.Rank.JACK));
        long hand3 = PackedCardSet.add(PackedCardSet.singleton(PackedCard.pack(Card.Color.DIAMOND, Card.Rank.TEN)), PackedCard.pack(Card.Color.SPADE, Card.Rank.TEN));
        // has ace heart + spade jack
        
        System.out.println(PackedCardSet.toString(hand4));
        System.out.println(PackedCardSet.toString(hand1));
        System.out.println(PackedCardSet.toString(hand2));
        System.out.println(PackedCardSet.toString(hand3));
        
        System.out.println("--");
        
        System.out.println(PackedCardSet.toString(PackedTrick.playableCards(trick, hand4)));
        
        trick = PackedTrick.withAddedCard(trick, PackedCard.pack(Card.Color.HEART, Card.Rank.ACE));
        
        // has queen heart + club king
        System.out.println(PackedCardSet.toString(PackedTrick.playableCards(trick, hand1)));
        trick = PackedTrick.withAddedCard(trick, PackedCard.pack(Card.Color.HEART, Card.Rank.QUEEN));
        
        //has jack heart and seven of club
        //proves that Bour is not mandatory
        System.out.println(PackedCardSet.toString(PackedTrick.playableCards(trick, hand2)));
        trick = PackedTrick.withAddedCard(trick, PackedCard.pack(Card.Color.HEART, Card.Rank.JACK));
        
        // had ten diamond + ten hearts
        // proves that non-base can play any
        System.out.println(PackedCardSet.toString(PackedTrick.playableCards(trick, hand3)));
        trick = PackedTrick.withAddedCard(trick, PackedCard.pack(Card.Color.DIAMOND, Card.Rank.TEN));
        
        assertTrue(PackedTrick.isFull(trick));
        assertFalse(PackedTrick.isLast(trick));
        
        trick = PackedTrick.nextEmpty(trick);
        System.out.println(PackedCardSet.toString(PackedTrick.playableCards(trick, hand4)));
        
        
    }
    
    
    @Test 
    void playableCardsWorks3() {
        int trick = PackedTrick.firstEmpty(Card.Color.HEART, PlayerId.PLAYER_4);
        
        // to test: basic playable rules
        long hand4 = PackedCardSet.add(PackedCardSet.singleton(PackedCard.pack(Card.Color.CLUB, Card.Rank.ACE)), PackedCard.pack(Card.Color.SPADE, Card.Rank.JACK));
        long hand1 = PackedCardSet.add(PackedCardSet.singleton(PackedCard.pack(Card.Color.DIAMOND, Card.Rank.QUEEN)), PackedCard.pack(Card.Color.CLUB, Card.Rank.KING));
        long hand2 = PackedCardSet.add(PackedCardSet.singleton(PackedCard.pack(Card.Color.CLUB, Card.Rank.SEVEN)), PackedCard.pack(Card.Color.SPADE, Card.Rank.JACK));
        long hand3 = PackedCardSet.add(PackedCardSet.singleton(PackedCard.pack(Card.Color.DIAMOND, Card.Rank.TEN)), PackedCard.pack(Card.Color.SPADE, Card.Rank.TEN));
        // has ace heart + spade jack
        
        System.out.println(PackedCardSet.toString(hand4));
        System.out.println(PackedCardSet.toString(hand1));
        System.out.println(PackedCardSet.toString(hand2));
        System.out.println(PackedCardSet.toString(hand3));
        
        System.out.println("--");
        
        System.out.println(PackedCardSet.toString(PackedTrick.playableCards(trick, hand4)));
        
        trick = PackedTrick.withAddedCard(trick, PackedCard.pack(Card.Color.CLUB, Card.Rank.ACE));
        
        // has queen heart + club king
        System.out.println(PackedCardSet.toString(PackedTrick.playableCards(trick, hand1)));
        trick = PackedTrick.withAddedCard(trick, PackedCard.pack(Card.Color.CLUB, Card.Rank.KING));
        
        //has jack heart and seven of club
        System.out.println(PackedCardSet.toString(PackedTrick.playableCards(trick, hand2)));
        trick = PackedTrick.withAddedCard(trick, PackedCard.pack(Card.Color.CLUB, Card.Rank.SEVEN));
        
        // had ten diamond + ten hearts
        System.out.println(PackedCardSet.toString(PackedTrick.playableCards(trick, hand3)));
        trick = PackedTrick.withAddedCard(trick, PackedCard.pack(Card.Color.DIAMOND, Card.Rank.TEN));
        
        assertTrue(PackedTrick.isFull(trick));
        assertFalse(PackedTrick.isLast(trick));
        
        trick = PackedTrick.nextEmpty(trick);
        System.out.println(PackedCardSet.toString(PackedTrick.playableCards(trick, hand4)));
        
        
    }
    
}