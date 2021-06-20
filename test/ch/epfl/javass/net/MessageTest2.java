/**
 *
 * @author Raoul Gerber (304502)
 */

package ch.epfl.javass.net;


import java.io.IOException;
import java.net.UnknownHostException;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.RandomPlayerSelf;


public final class MessageTest2 {
    public static void main(String[] args) {

        Player player = new RandomPlayerSelf(2019);
        try {
            player = new RemotePlayerClient("localhost");
        } catch (UnknownHostException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        CardSet hand = CardSet.EMPTY.add(Card.of(Card.Color.CLUB, Card.Rank.EIGHT));
        player.updateHand(hand);
        try {
            ((RemotePlayerClient) player).close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
