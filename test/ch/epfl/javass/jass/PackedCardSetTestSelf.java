package ch.epfl.javass.jass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class PackedCardSetTestSelf {
    @Test
    void constantsAreCorrect() {
        assertEquals(0, PackedCardSet.EMPTY);
        assertEquals(Long.parseLong("0000000111111111000000011111111100000001111111110000000111111111",2), PackedCardSet.ALL_CARDS);
    }

    @Test
    void isValidWorksForConstants() {
        assertTrue(PackedCardSet.isValid(PackedCardSet.EMPTY));
        assertTrue(PackedCardSet.isValid(PackedCardSet.ALL_CARDS));
    }

    @Test
    void isValidWorksWhenCardsAreWrong() throws Exception {
        assertFalse(PackedCardSet.isValid(Long.parseLong("0000000111111111000000011111111100000001111111110000001111111111",2)));
        assertFalse(PackedCardSet.isValid(Long.parseLong("0000000111111111000000011111111100000001111111110010000111111111",2)));
        assertFalse(PackedCardSet.isValid(Long.parseLong("0000000111111111000000011111111101000001111111110000000111111111",2)));
        assertFalse(PackedCardSet.isValid(Long.parseLong("0000000111111111010000011111111100000001111111110000000111111111",2)));
        assertFalse(PackedCardSet.isValid(Long.parseLong("0100000111111111000000011111111100000001111111110000000111111111",2)));
    }

    @Test
    void trumpAboveWorks() {
        assertEquals(Long.parseLong("000101000",2), PackedCardSet.trumpAbove(PackedCard.pack(Card.Color.SPADE, Card.Rank.ACE)));
        assertEquals(Long.parseLong("110101000", 2) << 48, PackedCardSet.trumpAbove(PackedCard.pack(Card.Color.CLUB, Card.Rank.QUEEN)));
    }
    
    @Test
    void singletonWorks() {
        assertEquals(Long.parseLong("000000001", 2), PackedCardSet.singleton(PackedCard.pack(Card.Color.SPADE, Card.Rank.SIX)));
        assertEquals(Long.parseLong("000001000", 2) << 16, PackedCardSet.singleton(PackedCard.pack(Card.Color.HEART, Card.Rank.NINE)));
        assertEquals(Long.parseLong("100000000", 2) << 48, PackedCardSet.singleton(PackedCard.pack(Card.Color.CLUB, Card.Rank.ACE)));
    }
    
    @Test
    void isEmptyWorks() {
        assertTrue(PackedCardSet.isEmpty(PackedCardSet.EMPTY));
        assertFalse(PackedCardSet.isEmpty(1L));
        assertFalse(PackedCardSet.isEmpty(Long.parseLong("000100000", 2) << 32));
        assertFalse(PackedCardSet.isEmpty(Long.parseLong("100000000", 2) << 48));
    }
    
    @Test
    void sizeWorks() {
        assertEquals(1, PackedCardSet.size(PackedCardSet.singleton(PackedCard.pack(Card.Color.DIAMOND, Card.Rank.JACK))));
        assertEquals(36, PackedCardSet.size(PackedCardSet.ALL_CARDS));
        assertEquals(0, PackedCardSet.size(PackedCardSet.EMPTY));
    }
    
    @Test
    void getWorks() {
        assertEquals(PackedCard.pack(Card.Color.DIAMOND, Card.Rank.JACK), PackedCardSet.get(PackedCardSet.singleton(PackedCard.pack(Card.Color.DIAMOND, Card.Rank.JACK)),0));
        assertEquals(PackedCard.pack(Card.Color.SPADE, Card.Rank.SIX), PackedCardSet.get(Long.parseLong("111111111", 2), 0));
        assertEquals(PackedCard.pack(Card.Color.SPADE, Card.Rank.NINE), PackedCardSet.get(Long.parseLong("111111111", 2), 3));
        assertEquals(PackedCard.pack(Card.Color.HEART, Card.Rank.SEVEN), PackedCardSet.get(Long.parseLong("111111111", 2) << 16, 1));
        assertEquals(PackedCard.pack(Card.Color.HEART, Card.Rank.TEN), PackedCardSet.get(Long.parseLong("111111000", 2) << 16, 1));
    }
    
    @Test
    void addWorks() {
        assertEquals(PackedCardSet.singleton(PackedCard.pack(Card.Color.DIAMOND, Card.Rank.JACK)), PackedCardSet.add(PackedCardSet.EMPTY,  PackedCard.pack(Card.Color.DIAMOND, Card.Rank.JACK)));
        assertEquals(Long.parseLong("111111111",2), PackedCardSet.add(Long.parseLong("111011111",2), PackedCard.pack(Card.Color.SPADE, Card.Rank.JACK)));
        assertEquals(Long.parseLong("111111111",2), PackedCardSet.add(Long.parseLong("111111111",2), PackedCard.pack(Card.Color.SPADE, Card.Rank.JACK)));
    }
    
    @Test 
    void removeWorks() {
        assertEquals(PackedCardSet.EMPTY, PackedCardSet.remove(PackedCardSet.singleton(PackedCard.pack(Card.Color.DIAMOND, Card.Rank.JACK)), PackedCard.pack(Card.Color.DIAMOND, Card.Rank.JACK)));
        assertEquals(Long.parseLong("111011111",2), PackedCardSet.remove(Long.parseLong("111111111",2), PackedCard.pack(Card.Color.SPADE, Card.Rank.JACK)));
        assertEquals(Long.parseLong("111011111",2), PackedCardSet.remove(Long.parseLong("111011111",2), PackedCard.pack(Card.Color.SPADE, Card.Rank.JACK)));
    }
    
    @Test
    void containsWorks() {
        assertFalse(PackedCardSet.contains(PackedCardSet.EMPTY, (PackedCard.pack(Card.Color.DIAMOND, Card.Rank.KING))));
        assertTrue(PackedCardSet.contains(Long.parseLong("111111111",2), PackedCard.pack(Card.Color.SPADE, Card.Rank.JACK)));
        assertFalse(PackedCardSet.contains(Long.parseLong("111111111",2), PackedCard.pack(Card.Color.DIAMOND, Card.Rank.KING)));
        assertTrue(PackedCardSet.contains(PackedCardSet.ALL_CARDS, PackedCard.pack(Card.Color.CLUB, Card.Rank.ACE)));
    }
    
    
    @Test
    void complementsWorks() {
        assertEquals(1, PackedCardSet.complement(Long.parseLong("0000000111111111000000011111111100000001111111110000000111111110",2)));
        assertEquals(Long.parseLong("0000000111111111000000011111111100000001111111110000000111111110",2), PackedCardSet.complement(1L));
    }
    

    @Test
    void unionWorks() {
        // probably no need to test
        // same for intersection, difference,
    }
    
    @Test
    void subsetOfColorWorksAndToString() {
        assertEquals(Long.parseLong("0000000111111110",2), PackedCardSet.subsetOfColor(Long.parseLong("0000000111111111000000011111111100000001111111110000000111111110",2), Card.Color.SPADE));
        assertEquals(Long.parseLong("1111001110000000000000000",2), PackedCardSet.subsetOfColor(Long.parseLong("0000000111111111000000011111111100000001111001110000000111111110",2), Card.Color.HEART));
        
        System.out.println(PackedCardSet.toString(Long.parseLong("0000000111111110",2)));
        System.out.println(PackedCardSet.toString(Long.parseLong("1111001110000000000000000",2)));
        System.out.println(PackedCardSet.toString(Long.parseLong("0000000111111111000000011111111100000001111111110000000111111110",2)));
    }
    
    
    
    
    
    
    
    
    
}