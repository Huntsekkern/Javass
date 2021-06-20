package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

/**
 * représente un joueur. Elle est destinée à être implémentée par les différents types de joueurs que nous réaliserons par la suite — joueur simulé, joueur humain, etc
 * @author Raoul Gerber (304502)
 */
public interface Player {


    /**
     * retourne la carte que le joueur désire jouer, 
     * sachant que l'état actuel du tour est celui décrit par state et que le joueur a les cartes hand en main.
     * @param state état actuel du tour
     * @param hand cartes en main du jouer
     * @return la carte que le joueur désire jouer
     */
    public abstract Card cardToPlay(TurnState state, CardSet hand);


    /**
     * est appelée une seule fois en début de partie pour informer le joueur qu'il a l'identité ownId et que les différents joueurs (lui inclus) sont nommés selon le contenu de la table associative playerNames
     * @param ownId identité du joueur
     * @param playerNames différents joueurs sous forme de table associative
     */
    public default void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {}


    /**
     * est appelée chaque fois que la main du joueur change — soit en début de tour lorsque les cartes sont distribuées, soit après qu'il ait joué une carte — pour l'informer de sa nouvelle main
     * @param newHand nouvelle main du joueur
     */
    public default void updateHand(CardSet newHand) {} 


    /**
     * est appelée chaque fois que l'atout change — c-à-d au début de chaque tour — pour informer le joueur de l'atout
     * @param trump nouveau atout
     */
    public default void setTrump(Color trump) {} 


    /**
     * est appelée chaque fois que le pli change, c-à-d chaque fois qu'une carte est posée ou lorsqu'un pli terminé est ramassé et que le prochain pli (vide) le remplace
     * @param newTrick nouveau pli vide
     */
    public default void updateTrick(Trick newTrick) {} 


    /**
     * est appelée chaque fois que le score change, c-à-d chaque fois qu'un pli est ramassé
     * @param score score mis-à-jour
     */
    public default void updateScore(Score score) {} 


    /**
     * est appelée une seule fois dès qu'une équipe à gagné en obtenant 1000 points ou plus
     * @param winningTeam équipe gagnante
     */
    public default void setWinningTeam(TeamId winningTeam) {}

}
