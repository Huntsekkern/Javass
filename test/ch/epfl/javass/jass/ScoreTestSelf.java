package ch.epfl.javass.jass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public final class ScoreTestSelf {
    
    @Test 
    // Not an official J Unit test, but the console display matches what is expected !
    void pointScoringUnpacked() {
        Score s = Score.INITIAL;
        System.out.println(s.toString());
        for (int i = 0; i < Jass.TRICKS_PER_TURN; ++i) {
          int p = (i == 0 ? 13 : 18);
          TeamId w = (i % 2 == 0 ? TeamId.TEAM_1 : TeamId.TEAM_2);
          s = s.withAdditionalTrick(w, p);
          System.out.println(s.toString());
        }
        s = s.nextTurn();
        System.out.println(s.toString());
    }
    
    
    @Test
    void ofPackedAndRetrieveWorks() throws Exception {
        long pkScore = PackedScore.pack(3, 50, 980, 1, 14, 732);
        Score score = Score.ofPacked(pkScore);
        
        assertEquals(3, score.turnTricks(TeamId.TEAM_1));
        assertEquals(50, score.turnPoints(TeamId.TEAM_1));
        assertEquals(980, score.gamePoints(TeamId.TEAM_1));
        assertEquals((50+980), score.totalPoints(TeamId.TEAM_1));
        
        assertEquals(1, score.turnTricks(TeamId.TEAM_2));
        assertEquals(14, score.turnPoints(TeamId.TEAM_2));
        assertEquals(732, score.gamePoints(TeamId.TEAM_2));
        assertEquals((14+732), score.totalPoints(TeamId.TEAM_2));
    }
    

    @Test
    void equalsIsCorrect() {
        long pkScore = PackedScore.pack(3, 50, 980, 1, 14, 732);
        long pkScore2 = PackedScore.pack(3, 50, 980, 1, 14, 732);
        long pkScore3 = PackedScore.pack(3, 51, 980, 1, 14, 732);
        
        Score score = Score.ofPacked(pkScore);
        Score score2 = Score.ofPacked(pkScore2);
        Score score3 = Score.ofPacked(pkScore3);
        
        assertEquals((pkScore == pkScore2), score.equals(score2));
        assertEquals((pkScore == pkScore3), score.equals(score3));
        assertEquals((pkScore3 == pkScore2), score3.equals(score2));
    }

    @Test
    void allHashCodesAreDifferent() {
        long pkScore = PackedScore.pack(3, 50, 980, 1, 14, 732);
        long pkScore2 = PackedScore.pack(3, 50, 980, 1, 14, 732);
        long pkScore3 = PackedScore.pack(3, 51, 980, 1, 14, 732);
        
        Score score = Score.ofPacked(pkScore);
        Score score2 = Score.ofPacked(pkScore2);
        Score score3 = Score.ofPacked(pkScore3);
        
        assertTrue(score.hashCode() == score2.hashCode());
        assertTrue(score2.hashCode() != score3.hashCode());
       
    }
}
