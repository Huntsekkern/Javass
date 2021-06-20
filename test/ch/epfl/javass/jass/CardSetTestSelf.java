package ch.epfl.javass.jass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CardSetTestSelf {

//    private static long randomPkCardSet(SplittableRandom rng) {
//        int t1 = rng.nextInt(10);
//        int p1 = rng.nextInt(258);
//        int g1 = rng.nextInt(2000);
//        int t2 = rng.nextInt(10 - t1);
//        int p2 = rng.nextInt(258 - p1);
//        int g2 = rng.nextInt(2000 - g1);
//        return PackedCardSet.pack(t1, p1, g1, t2, p2, g2);
//    }

    @Test
    void initialIsCorrect() {
        assertEquals(PackedCardSet.EMPTY, CardSet.EMPTY.packed());
        assertEquals(PackedCardSet.ALL_CARDS, CardSet.ALL_CARDS.packed());
    }

    @Test
    void ofPackedAndPackedAreInverses() {
            long pkCardSet = Long.parseLong("0000000111111110",2);
            assertEquals(pkCardSet, CardSet.ofPacked(pkCardSet).packed());
            long pkCardSet2 = Long.parseLong("1111001110000000000000000",2);
            assertEquals(pkCardSet2, CardSet.ofPacked(pkCardSet2).packed());
            long pkCardSet3 = Long.parseLong("0000000111111111000000011111111100000001111111110000000111111110",2);
            assertEquals(pkCardSet3, CardSet.ofPacked(pkCardSet3).packed());
    }



    @Test
    void equalsIsFalseForUnequalCardSets() {
            long pkCardSet1 = Long.parseLong("0000000111111110",2);
            long pkCardSet2 = Long.parseLong("1111001110000000000000000",2);

            CardSet s1 = CardSet.ofPacked(pkCardSet1);
            CardSet s2 = CardSet.ofPacked(pkCardSet2);
            assertFalse(s1.equals(s2));
    }

    @Test
    void equalsIsTrueOnEqualButDifferentInstances() {
            long pkCardSet = Long.parseLong("0000000111111111000000011111111100000001111111110000000111111110",2);
            CardSet s1 = CardSet.ofPacked(pkCardSet);
            CardSet s2 = CardSet.ofPacked(pkCardSet);
            assertTrue(s1.equals(s2));
    }
}
