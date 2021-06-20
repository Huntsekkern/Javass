package ch.epfl.javass.gui;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import javafx.application.Platform;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

/**
 * un adaptateur permettant d'adapter l'interface graphique (c-à-d la classe GraphicalPlayer) 
 * pour en faire un joueur, c-à-d une valeur de type Player
 * @author Raoul Gerber (304502)
 */
public final class GraphicalPlayerAdapter implements Player {

    private final ScoreBean sb;
    private final TrickBean tb;
    private final HandBean hb;
    private GraphicalPlayer graphicalPlayer;
    private final ArrayBlockingQueue<Card> queue;
    
    private final static int QUEUE_SIZE = 1;
    
    /**
     * Construit l'adapteur en initialisant des beans vides et la queue de cartes de taille 1
     */
    public GraphicalPlayerAdapter() {
        sb = new ScoreBean();
        tb = new TrickBean();
        hb = new HandBean();
        queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
    }
    
    
    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#cardToPlay(ch.epfl.javass.jass.TurnState, ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        Platform.runLater(() -> { hb.setPlayableCards(state.trick().playableCards(hand)); });
        
        Card card;
        try {
            card = queue.take();
        } catch (InterruptedException e) {
            throw new Error(e);
        }
        Platform.runLater(() -> { hb.setPlayableCards(CardSet.EMPTY); });
        
        return card;
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setPlayers(ch.epfl.javass.jass.PlayerId, java.util.Map)
     */
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        graphicalPlayer = new GraphicalPlayer(ownId, playerNames, sb, tb, hb, queue);
        Platform.runLater(() -> { graphicalPlayer.createStage().show(); });
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateHand(ch.epfl.javass.jass.CardSet)
     */
    @Override
    public void updateHand(CardSet newHand) {
        Platform.runLater(() -> { hb.setHand(newHand); });
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setTrump(ch.epfl.javass.jass.Card.Color)
     */
    @Override
    public void setTrump(Color trump) {
        Platform.runLater(() -> { tb.setTrump(trump); });
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateTrick(ch.epfl.javass.jass.Trick)
     */
    @Override
    public void updateTrick(Trick newTrick) {
        Platform.runLater(() -> { tb.setTrick(newTrick); });
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateScore(ch.epfl.javass.jass.Score)
     */
    @Override
    public void updateScore(Score score) {
       Platform.runLater(() -> {
           for(TeamId team : TeamId.ALL) {
               sb.setTurnPoints(team, score.turnPoints(team));
               sb.setGamePoints(team, score.gamePoints(team));
               sb.setTotalPoints(team, score.totalPoints(team));
           }
       });
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setWinningTeam(ch.epfl.javass.jass.TeamId)
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        Platform.runLater(() -> { sb.setWinningTeam(winningTeam); });

    }
    
    

}
