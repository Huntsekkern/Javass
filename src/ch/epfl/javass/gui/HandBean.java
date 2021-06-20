package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Jass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

/**
* bean JavaFX contenant la main du joueur et le sous-ensemble jouable de sa main
* @author Raoul Gerber (304502)
*/
public final class HandBean {
    
    private final ObservableList<Card> hand;
    private final ObservableSet<Card> playableCards;
    
    /**
     * Construit un HandBean dont les cartes de la main sont null
     */
    public HandBean() {
        hand = FXCollections.observableArrayList();
        for(int i=0; i<Jass.HAND_SIZE; ++i) {
            hand.add(null);
        }
        playableCards = FXCollections.observableSet();
    }
    
    
     /**
      * Retourne la liste observable de cartes dans la main 
      * @return la liste observable de cartes dans la main 
      */
    public final ObservableList<Card> hand() {
        return FXCollections.unmodifiableObservableList(hand);
    }
    
    
    /**
     * Redéfinit la main si appelée avec un ensemble de 9 cartes,
     * sinon enlève de la main courante les cartes qui ne figurent plus dans newHand
     * @param newHand nouvelle main du joueur
     */
    public final void setHand(CardSet newHand) {
        if(newHand.size() == 9) {
            for(int i=0; i<newHand.size(); ++i) {
                hand.set(i, newHand.get(i));
            }
        } else {
            for(int i=0; i<hand.size(); ++i) {
                if(hand.get(i) != null && !newHand.contains(hand.get(i))) {
                    hand.set(i, null);
                }

            }
        }

    }
    
    
    /**
     * Retourne le set observable de cartes jouables parmi la main
     * @return le set observable de cartes jouables parmi la main
     */
    public final ObservableSet<Card> playableCards() {
        return FXCollections.unmodifiableObservableSet(playableCards);
    }
    
    
    /**
     * Redéfinit le set de cartes jouables
     * @param newPlayableCards le nouveau set de cartes jouables
     */
    public final void setPlayableCards(CardSet newPlayableCards) {
        playableCards.clear();
        for(int i=0; i<newPlayableCards.size(); ++i) {
            playableCards.add(newPlayableCards.get(i));
        }
    }

}
