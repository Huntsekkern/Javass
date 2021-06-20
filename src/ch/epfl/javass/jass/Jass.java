package ch.epfl.javass.jass;

/**
 * Contient des constantes liées au jeu du Jass
 * @author Raoul Gerber (304502)
 *
 */
public interface Jass {

    /**
     * le nombre de cartes dans une main au début d'un tour 
     */
    public final int HAND_SIZE = 9;

    /**
     * le nombre de plis dans un tour de jeu
     */
    public final int TRICKS_PER_TURN = 9;

    /**
     * le nombre de points nécessaire à une victoire
     */
    public final int WINNING_POINTS = 1000;

    /**
     * le nombre de points additionnels obtenus par une équipe remportant la totalité des plis d'un tour
     */
    public final int MATCH_ADDITIONAL_POINTS = 100;

    /**
     * le nombre de points additionnels obtenu par l'équipe remportant le dernier pli
     */
    public final int LAST_TRICK_ADDITIONAL_POINTS = 5;
}
