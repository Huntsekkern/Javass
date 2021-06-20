package ch.epfl.javass.jass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public final class PackedScoreTestSelf {

    @Test 
    // Not an official J Unit test, but the console display matches what is expected !
    void pointScoring() {
        long s = PackedScore.INITIAL;
        System.out.println(PackedScore.toString(s));
        for (int i = 0; i < Jass.TRICKS_PER_TURN; ++i) {
          int p = (i == 0 ? 13 : 18);
          TeamId w = (i % 2 == 0 ? TeamId.TEAM_1 : TeamId.TEAM_2);
          s = PackedScore.withAdditionalTrick(s, w, p);
          System.out.println(PackedScore.toString(s));
        }
        s = PackedScore.nextTurn(s);
        System.out.println(PackedScore.toString(s));
    }
    

    
    @Test
    void isValidWorksForValidScores() {
        assertTrue(PackedScore.isValid(PackedScore.INITIAL));
        assertTrue(PackedScore.isValid(PackedScore.pack(3, 50, 980, 1, 14, 732)));
        assertTrue(PackedScore.isValid(PackedScore.pack(9, 257, 1400, 0, 0, 1200)));
        assertTrue(PackedScore.isValid(PackedScore.pack(0, 0, 20, 9, 257, 200)));
    }

    @Test
    void isValidWorksForSomeInvalidScore() {
            assertFalse(PackedScore.isValid(PackedScore.pack(10, 200, 50, 0, 0, 50)));
            assertFalse(PackedScore.isValid(PackedScore.pack(6, 258, 50, 0, 0, 50)));
            assertFalse(PackedScore.isValid(PackedScore.pack(6, 130, 2010, 0, 0, 50)));
            
            assertFalse(PackedScore.isValid(PackedScore.pack(0, 200, 50, 10, 0, 50)));
            assertFalse(PackedScore.isValid(PackedScore.pack(6, 0, 50, 2, 258, 50)));
            assertFalse(PackedScore.isValid(PackedScore.pack(6, 200, 50, 2, 20, 2001)));
       
            assertFalse(PackedScore.isValid(PackedScore.pack(6, 200, 50, 5, 0, 50)));
            assertFalse(PackedScore.isValid(PackedScore.pack(6, 200, 50, 2, 100, 50)));
    }

    @Test
    void packAndRetrieveWork() throws Exception {
             
        long pkScore = PackedScore.pack(3, 50, 980, 1, 14, 732);
        
        assertEquals(3, PackedScore.turnTricks(pkScore, TeamId.TEAM_1));
        assertEquals(50, PackedScore.turnPoints(pkScore, TeamId.TEAM_1));
        assertEquals(980, PackedScore.gamePoints(pkScore, TeamId.TEAM_1));
        assertEquals((50+980), PackedScore.totalPoints(pkScore, TeamId.TEAM_1));
        
        assertEquals(1, PackedScore.turnTricks(pkScore, TeamId.TEAM_2));
        assertEquals(14, PackedScore.turnPoints(pkScore, TeamId.TEAM_2));
        assertEquals(732, PackedScore.gamePoints(pkScore, TeamId.TEAM_2));
        assertEquals((14+732), PackedScore.totalPoints(pkScore, TeamId.TEAM_2));
    }

    
    @Test
    void nextTurnWorksForDefault() {
        long pkScore = PackedScore.pack(3, 50, 980, 1, 14, 732);     
        assertEquals(PackedScore.pack(0, 0, 980+50, 0, 0, 732+14), PackedScore.nextTurn(pkScore));
    }
    
    // Meh, too complex and not needed, cf first non-J-unit test.
//    @Test
//    void withAdditionalTricksWorksForDefault() {
//        long s = PackedScore.INITIAL;
//        assertEquals(PackedScore.pack(0, 0, 0, 0, 0, 0), s);
//        for (int i = 0; i < Jass.TRICKS_PER_TURN; ++i) {
//          int p = (i == 0 ? 13 : 18);
//          TeamId w = (i % 2 == 0 ? TeamId.TEAM_1 : TeamId.TEAM_2);
//          s = PackedScore.withAdditionalTrick(s, w, p);
//          System.out.println(i);
//          assertEquals(PackedScore.pack((int) Math.floor(i/2)+1, (int) Math.floor(i/2)*18 +13, 0, (int) Math.ceil(i/2), (int) (Math.ceil(i/2))*18, 0), s);
//        }
//        s = PackedScore.nextTurn(s);
//        assertEquals(PackedScore.pack(0, 0, 85, 0, 0, 72), s);
//    }


    
    
}
