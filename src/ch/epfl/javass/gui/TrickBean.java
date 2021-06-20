package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Trick;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
* bean JavaFX contenant le pli courant
* @author Raoul Gerber (304502)
*/
public final class TrickBean {
    
    
    private final SimpleObjectProperty<Card.Color> trump;
    private final ObservableMap<PlayerId, Card> trick;
    private final SimpleObjectProperty<PlayerId> winningPlayer;
    
    /**
     * Construit un nouveau TrickBean sans valeur par défaut
     */
    public TrickBean() {
        trump = new SimpleObjectProperty<Card.Color>();
        trick = FXCollections.observableHashMap();
        winningPlayer = new SimpleObjectProperty<PlayerId>();
    }
    
    
    /**
     * Retourne la couleur d'atout
     * @return la couleur d'atout
     */
    public final ReadOnlyObjectProperty<Card.Color> trump() {
        return trump;
    }
    
    
    /**
     * Définit la couleur d'atout
     * @param trump couleur d'atout donnée
     */
    public final void setTrump(Card.Color trump) {
        this.trump.set(trump);
    }
    
    
    /**
     * Retourne une ObservableMap assignant à chaque PlayerId la carte qu'il a joué ou null s'il n'a pas encore joué
     * @return une ObservableMap assignant à chaque PlayerId la carte qu'il a joué ou null s'il n'a pas encore joué
     */
    public final ObservableMap<PlayerId, Card> trick() {
        return FXCollections.unmodifiableObservableMap(trick);
    }
    
    
    /**
     * Définit le pli en cours (sous forme de Map) ainsi que l'actuel gagnant à partir d'un pli donné
     * @param newTrick pli donné
     */
    public final void setTrick(Trick newTrick) {
        for(int i=0; i<newTrick.size(); ++i) {
          trick.put(newTrick.player(i), newTrick.card(i));  
        }
        
        for(int i=newTrick.size(); i<4; ++i) {
            trick.put(newTrick.player(i), null);
        }
            
        
        if(newTrick.isEmpty()) {
            winningPlayer.set(null);
        } else {
            winningPlayer.set(newTrick.winningPlayer());
        }
    }
    
    
    /**
     * Retourne l'actuel gagnant du pli en cours
     * @return l'actuel gagnant du pli en cours
     */
    public final ReadOnlyObjectProperty<PlayerId> winningPlayer() {
        return winningPlayer;
    }
}
