/**
 *
 * @author Raoul Gerber (304502)
 */

package ch.epfl.javass.jass;

import java.util.Map;
import java.util.Map.Entry;

import ch.epfl.javass.jass.Card.Color;

public final class PrintingPlayerSelf implements Player {
    private final Player underlyingPlayer;

    public PrintingPlayerSelf(Player underlyingPlayer) {
      this.underlyingPlayer = underlyingPlayer;
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
      System.out.print("C'est à moi de jouer... Je joue : ");
      Card c = underlyingPlayer.cardToPlay(state, hand);
      System.out.println(c);
      return c;
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        underlyingPlayer.setPlayers(ownId, playerNames);
        System.out.println("Les joueurs sont :");
        for(Entry<PlayerId, String> player : playerNames.entrySet()) {
            if(player.getKey().equals(ownId)) {
                System.out.println(player.getValue() + " (moi)");
            } else {
                System.out.println(player.getValue());          
            }
        }
    }

    @Override
    public void updateHand(CardSet newHand) {
        underlyingPlayer.updateHand(newHand);
        System.out.println("Ma nouvelle main : " + newHand.toString());
    }

    @Override
    public void setTrump(Color trump) {
        underlyingPlayer.setTrump(trump);
        System.out.println("Atout : " + trump.toString());
    }

    @Override
    public void updateTrick(Trick newTrick) {
        underlyingPlayer.updateTrick(newTrick);
        System.out.println(newTrick.toString());
    }

    @Override
    public void updateScore(Score score) {
        underlyingPlayer.updateScore(score);
        System.out.println(score.toString());
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        underlyingPlayer.setWinningTeam(winningTeam);
        System.out.println("Winning Team : " + winningTeam.toString());
    }

    
  }
