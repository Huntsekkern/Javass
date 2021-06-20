/**
 *
 * @author Raoul Gerber (304502)
 */

package ch.epfl.javass.jass;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

public class MctsPlayerSelf {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Player player = new MctsPlayer(PlayerId.PLAYER_2, 0, 100000);
        
        TurnState testTurn = TurnState.initial(Color.SPADE, Score.INITIAL, PlayerId.PLAYER_1);
        testTurn = testTurn.withNewCardPlayedAndTrickCollected(Card.of(Color.SPADE, Rank.JACK));
        CardSet mctsHand = CardSet.ofPacked(Long.parseLong("0000000010001000000000010000000000000000011010000000000000011100",2));
        
//        TurnState testTurn = TurnState.ofPackedComponents(0, 143835486954127871L, 285108577);
//        CardSet mctsHand = CardSet.ofPacked(9289262663991297L);
                
        System.out.println(PackedCardSet.toString(143835486954127871L));
        System.out.println(PackedTrick.toString(285108577));
                
        System.out.println(player.cardToPlay(testTurn, mctsHand).toString());
    }
//    285108577
//    143835486954127871
//    9289262663991297
}
