package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * permet de s'assurer qu'un joueur met un temps minimum pour jouer
 * @author Raoul Gerber (304502)
 */
public final class PacedPlayer implements Player {

    private final Player underlyingPlayer;
    private final double minTime;

    /**
     * retourne un joueur qui se comporte exactement comme le joueur sous-jacent donné (underlyingPlayer), si ce n'est que la méthode cardToPlay ne retourne jamais son résultat en un temps inférieur à minTime secondes
     * @param underlyingPlayer joueur donné à mimétiser
     * @param minTime temps en secondes de délai minimum
     */
    public PacedPlayer(Player underlyingPlayer, double minTime) {
        Preconditions.checkArgument(minTime >= 0);
        
        this.underlyingPlayer = underlyingPlayer;
        this.minTime = minTime;
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#cardToPlay(ch.epfl.javass.jass.TurnState, ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        long currentTime = System.currentTimeMillis();
        Card card = underlyingPlayer.cardToPlay(state, hand);
        long newTime = System.currentTimeMillis();
        if((newTime - currentTime)/1000 < minTime) {
            try {
                Thread.sleep((long)(minTime*1000) - newTime + currentTime);
            } catch (InterruptedException e) { /* ignore */ }
        }
        return card;
    }


    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setPlayers(ch.epfl.javass.jass.PlayerId, java.util.Map)
     */
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        underlyingPlayer.setPlayers(ownId, playerNames);
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateHand(ch.epfl.javass.jass.CardSet)
     */
    @Override
    public void updateHand(CardSet newHand) {
        underlyingPlayer.updateHand(newHand);
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setTrump(ch.epfl.javass.jass.Card.Color)
     */
    @Override
    public void setTrump(Color trump) {
        underlyingPlayer.setTrump(trump);
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateTrick(ch.epfl.javass.jass.Trick)
     */
    @Override
    public void updateTrick(Trick newTrick) {
        underlyingPlayer.updateTrick(newTrick);
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateScore(ch.epfl.javass.jass.Score)
     */
    @Override
    public void updateScore(Score score) {
        underlyingPlayer.updateScore(score);
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setWinningTeam(ch.epfl.javass.jass.TeamId)
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        underlyingPlayer.setWinningTeam(winningTeam);
    }





}
