package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * Représentation empaquetée (light) d'une carte
 * @author Raoul Gerber (304502)
 *
 */
public final class PackedCard {
    private PackedCard() {}

    private static final int RANK_START = 0;
    private static final int RANK_LENGTH = 4;
    private static final int RANK_END = RANK_START + RANK_LENGTH;
    private static final int COLOR_START = RANK_END;
    private static final int COLOR_LENGTH = 2;
    private static final int COLOR_END = COLOR_START + COLOR_LENGTH;


    /**
     * contient la valeur binaire 111111, qui ne représente pas une carte empaquetée valide
     */
    public static final int INVALID = Bits32.mask(RANK_START, COLOR_END);

    /**
     * retourne vrai ssi la valeur donnée est une carte empaquetée valide, c-à-d si les bits contenant le rang contiennent une valeur comprise entre 0 et 8 (inclus) et si les bits inutilisés valent tous 0.
     * @param pkCard carte à tester
     * @return vrai ssi la valeur donnée est une carte empaquetée valide, c-à-d si les bits contenant le rang contiennent une valeur comprise entre 0 et 8 (inclus) et si les bits inutilisés valent tous 0.
     */
    public static boolean isValid(int pkCard) {
        return (Bits32.extract(pkCard, RANK_START, RANK_LENGTH) < Card.Rank.COUNT 
                && Bits32.extract(pkCard, COLOR_END, Integer.SIZE-COLOR_END) == 0);
    }

    /**
     * retourne la carte empaquetée de couleur et rang donnés
     * @param c couleur donnée
     * @param r rang donné
     * @return  la carte empaquetée de couleur et rang donnés
     */
    public static int pack(Card.Color c, Card.Rank r) {
        return Bits32.pack(r.ordinal(), RANK_LENGTH, c.ordinal(), COLOR_LENGTH);
    }

    /**
     * retourne la couleur de la carte empaquetée donnée
     * @param pkCard carte dont la couleur est cherchée
     * @return la couleur de la carte empaquetée donnée
     */
    public static Card.Color color(int pkCard) {
        assert isValid(pkCard);
        int color = Bits32.extract(pkCard, COLOR_START, COLOR_LENGTH);
        return Color.ALL.get(color);
    }

    /**
     * retourne le rang de la carte empaquetée donnée
     * @param pkCard carte dont le rang est cherché
     * @return le rang de la carte empaquetée donnée
     */
    public static Card.Rank rank(int pkCard) {
        assert isValid(pkCard);
        int rank = Bits32.extract(pkCard, RANK_START, RANK_LENGTH);
        return Rank.ALL.get(rank);
    }

    /**
     * retourne vrai ssi la première carte donnée est supérieure à la seconde, sachant que l'atout est trump
     * notez que cela implique que cette méthode retourne faux si les deux cartes ne sont pas comparables
     * @param trump l'atout en cours
     * @param pkCardL première carte dont on teste la supériorité
     * @param pkCardR deuxième carte dont on teste l'infériorité
     * @return vrai ssi la première carte donnée est supérieure à la seconde, sachant que l'atout est trump, retourne faux sinon ou si les deux cartes ne sont pas comparables
     */
    public static boolean isBetter(Card.Color trump, int pkCardL, int pkCardR) {
        assert isValid(pkCardL);
        assert isValid(pkCardR);
        if(color(pkCardL) == trump) {
            if(color(pkCardR) == trump) {
                if(rank(pkCardL).trumpOrdinal() > rank(pkCardR).trumpOrdinal()) {
                    // both trump, but L is better
                    return true;
                } else {
                    // both trump, but R is better
                    return false;
                }
            } else {
                // only L is trump
                return true;
            }
        } else if(color(pkCardL) != color(pkCardR)) {
            // L is a different color (not trump) than R
            return false;
        } else if(rank(pkCardL).ordinal() > rank(pkCardR).ordinal()) {
            // none trump, but L is better
            return true;
        } else {
            // none trump, but R is better
            return false;
        }        
    }

    /**
     * retourne la valeur de la carte empaquetée donnée, sachant que l'atout est trump
     * @param trump atout donné
     * @param pkCard carte empaquetée donnée
     * @return la valeur de la carte empaquetée donnée, sachant que l'atout est trump
     */
    public static int points(Card.Color trump, int pkCard) {
        assert isValid(pkCard);
        Card.Rank rank = rank(pkCard);
        
        switch(rank) {
        case SIX : 
        case SEVEN : 
        case EIGHT : return 0;
        case NINE : return (color(pkCard) != trump) ? 0 : 14;
        case TEN : return 10;
        case JACK : return (color(pkCard) != trump) ? 2 : 20;
        case QUEEN : return 3;
        case KING : return 4;
        case ACE : return 11;

        default : return -1;
        }

    }

    /**
     * retourne une représentation de la carte empaquetée donnée sous forme de chaîne de caractères composée du symbole de la couleur et du nom abrégé du rang
     * @param pkCard la carte dont la forme abrégée (string) est cherchée
     * @return une représentation de la carte empaquetée donnée sous forme de chaîne de caractères composée du symbole de la couleur et du nom abrégé du rang
     */
    public static String toString(int pkCard) {
        assert isValid(pkCard);
        String str = color(pkCard).toString() + rank(pkCard).toString();
        return str;
    }

}
