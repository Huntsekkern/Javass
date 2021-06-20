package ch.epfl.javass.jass;

import java.util.StringJoiner;

import ch.epfl.javass.bits.Bits64;

/**
 * possède des méthodes permettant de manipuler des ensembles de cartes empaquetés dans des valeurs de type long (64 bits).
 * @author Raoul Gerber (304502)
 */
public final class PackedCardSet {
    private PackedCardSet() {}

    private static final int START_BIT_SPADE = 0;
    private static final int START_BIT_HEART = 16;
    private static final int START_BIT_DIAMOND = 32;
    private static final int START_BIT_CLUB = 48;
    private static final int COLOR_LENGTH = 16;
    private static final int INVALID_BITS_LENGTH = 7;

    /**
     * l'ensemble de cartes vide
     */
    public static final long EMPTY = 0L;

    /**
     * l'ensemble des 36 cartes du jeu de Jass
     */
    public static final long ALL_CARDS = (Bits64.mask(START_BIT_SPADE, Card.Rank.COUNT) | Bits64.mask(START_BIT_HEART, Card.Rank.COUNT) | Bits64.mask(START_BIT_DIAMOND, Card.Rank.COUNT) | Bits64.mask(START_BIT_CLUB, Card.Rank.COUNT));

    private static final long[] BY_COLOR = {
            Bits64.mask(START_BIT_SPADE, Card.Rank.COUNT), Bits64.mask(START_BIT_HEART, Card.Rank.COUNT), Bits64.mask(START_BIT_DIAMOND, Card.Rank.COUNT), Bits64.mask(START_BIT_CLUB, Card.Rank.COUNT)
    };

    private static final long[] TRUMP_ABOVE_TAB = trumpAboveTab();

    private static long[] trumpAboveTab() {
        long[] tab = new long[Long.SIZE];
        // TODO je stocke trop de long dans le tableau apparemment
        for(int i=0; i<Card.Color.COUNT; ++i) {

            long currentSet = 0;
            for(int j=0; j<Card.Rank.COUNT;++j) {
                currentSet = 0;
                for(int k=0; k<Card.Rank.COUNT;++k) {
                    if(PackedCard.isBetter(Card.Color.SPADE,k,j)) {
                        currentSet = PackedCardSet.add(currentSet, k);
                    }
                }
                tab[i*COLOR_LENGTH + j] =  currentSet << COLOR_LENGTH*i;
            }
        }

        return tab;
    }




    /**
     * retourne vrai ssi la valeur donnée représente un ensemble de cartes empaqueté valide, 
     * c-à-d si aucun des 28 bits inutilisés ne vaut 1
     * @param pkCardSet ensemble de cartes testé
     * @return vrai ssi la valeur donnée représente un ensemble de cartes empaqueté valide
     */
    public static boolean isValid(long pkCardSet) {
        return (Bits64.extract(pkCardSet, START_BIT_SPADE+Card.Rank.COUNT, INVALID_BITS_LENGTH) == 0 
                && Bits64.extract(pkCardSet, START_BIT_HEART+Card.Rank.COUNT, INVALID_BITS_LENGTH) == 0
                && Bits64.extract(pkCardSet, START_BIT_DIAMOND+Card.Rank.COUNT, INVALID_BITS_LENGTH) == 0
                && Bits64.extract(pkCardSet, START_BIT_CLUB+Card.Rank.COUNT, INVALID_BITS_LENGTH) == 0
                );
    }


    /**
     * retourne l'ensemble des cartes strictement plus fortes que la carte empaquetée donnée, sachant qu'il s'agit d'une carte d'atout
     * par exemple, appliquée à l'as de cœur, elle doit retourner un ensemble contenant deux éléments, le neuf et le valet de cœur, car ces deux cartes sont les seules à être strictement plus fortes que l'as de cœur lorsque cœur est atout
     * @param pkCard carte dont on cherche les cartes plus fortes
     * @return l'ensemble des cartes strictement plus fortes que la carte empaquetée donnée, sachant qu'il s'agit d'une carte d'atout
     */
    public static long trumpAbove(int pkCard) {
        assert PackedCard.isValid(pkCard);
        return TRUMP_ABOVE_TAB[pkCard];
    } 


    /**
     * retourne l'ensemble de cartes empaqueté contenant uniquement la carte empaquetée donnée
     * @param pkCard carte dont on veut créer l'ensemble qui la contient
     * @return l'ensemble de cartes empaqueté contenant uniquement la carte empaquetée donnée
     */
    public static long singleton(int pkCard) {
        assert PackedCard.isValid(pkCard);
        return Bits64.mask(pkCard, 1);
    }


    /**
     * retourne vrai ssi l'ensemble de cartes empaqueté donné est vide
     * @param pkCardSet ensemble de carte testé
     * @return vrai ssi l'ensemble de cartes empaqueté donné est vide
     */
    public static boolean isEmpty(long pkCardSet) {
        assert isValid(pkCardSet);
        return (pkCardSet == EMPTY);
    }


    /**
     * retourne la taille de l'ensemble de cartes empaqueté donné, 
     * c-à-d le nombre de cartes qu'il contient
     * @param pkCardSet ensemble de cartes dont on cherche la taille
     * @return la taille de l'ensemble de cartes empaqueté donné
     */
    public static int size(long pkCardSet) {
        assert isValid(pkCardSet);
        return Long.bitCount(pkCardSet);
    }


    /**
     * retourne la version empaquetée de la carte d'index donné de l'ensemble de cartes empaqueté donné, 
     * la carte d'index 0 étant celle correspondant au bit de poids le plus faible valant 1
     * @param pkCardSet ensemble de carte dont on cherche à extraire une carte empaquetée
     * @param index la carte d'index 0 étant celle correspondant au bit de poids le plus faible valant 1
     * @return retourne la version empaquetée de la carte d'index donné de l'ensemble de cartes empaqueté donné
     */
    public static int get(long pkCardSet, int index) {
        assert isValid(pkCardSet);
        assert(index>=0 && index<size(pkCardSet));
        for(int i = 0; i < index; ++i) {
            pkCardSet = pkCardSet ^ Long.lowestOneBit(pkCardSet);
        }
        return Long.numberOfTrailingZeros(pkCardSet);   
    }


    /**
     * retourne l'ensemble de cartes empaqueté donné auquel la carte empaquetée donnée a été ajoutée
     * @param pkCardSet ensemble de cartes existant
     * @param pkCard carte à ajouter
     * @return l'ensemble de cartes empaqueté donné auquel la carte empaquetée donnée a été ajoutée
     */
    public static long add(long pkCardSet, int pkCard) {
        assert isValid(pkCardSet);
        assert PackedCard.isValid(pkCard);
        return (pkCardSet | singleton(pkCard));
    }


    /**
     * retourne l'ensemble de cartes empaqueté donné duquel la carte empaquetée donnée a été supprimée
     * @param pkCardSet ensemble de cartes existant
     * @param pkCard carte à supprimer
     * @return l'ensemble de cartes empaqueté donné duquel la carte empaquetée donnée a été supprimée
     */
    public static long remove(long pkCardSet, int pkCard) {
        assert isValid(pkCardSet);
        assert PackedCard.isValid(pkCard);
        return (pkCardSet & complement(singleton(pkCard)));
    } 



    /**
     * retourne vrai ssi l'ensemble de cartes empaqueté donné contient la carte empaquetée donnée
     * @param pkCardSet ensemble de cartes existant
     * @param pkCard carte dont on cherche à savoir si déjà incluse
     * @return vrai ssi l'ensemble de cartes empaqueté donné contient la carte empaquetée donnée
     */
    public static boolean contains(long pkCardSet, int pkCard) {
        assert isValid(pkCardSet);
        assert PackedCard.isValid(pkCard);
        return (pkCardSet == add(pkCardSet, pkCard));
    } 


    /**
     * retourne le complément de l'ensemble de cartes empaqueté donné
     * @param pkCardSet ensemble de cartes à inverser
     * @return le complément de l'ensemble de cartes empaqueté donné
     */
    public static long complement(long pkCardSet) {
        assert isValid(pkCardSet);
        return (pkCardSet ^ ALL_CARDS);
    }


    /**
     * retourne l'union des deux ensembles de cartes empaquetés donnés
     * @param pkCardSet1 premier ensemble de carte à unir
     * @param pkCardSet2 deuxième ensemble de carte à unir
     * @return l'union des deux ensembles de cartes empaquetés donnés
     */
    public static long union(long pkCardSet1, long pkCardSet2) {
        assert isValid(pkCardSet1);
        assert isValid(pkCardSet2);
        return (pkCardSet1 | pkCardSet2);
    }  


    /**
     * retourne l'intersection des deux ensembles de cartes empaquetés donnés
     * @param pkCardSet1 premier ensemble de carte à intersecter
     * @param pkCardSet2 deuxième ensemble de carte à intersecter
     * @return l'intersection des deux ensembles de cartes empaquetés donnés
     */
    public static long intersection(long pkCardSet1, long pkCardSet2) {
        assert isValid(pkCardSet1);
        assert isValid(pkCardSet2);
        return (pkCardSet1 & pkCardSet2);
    } 


    /** 
     * retourne la différence entre le premier ensemble de cartes empaqueté donné et le second,
     * c-à-d l'ensemble des cartes qui se trouvent dans le premier ensemble mais pas dans le second
     * @param pkCardSet1 premier ensemble de carte 
     * @param pkCardSet2 deuxième ensemble de carte qu'on soustrait au premier
     * @return la différence entre le premier ensemble de cartes empaqueté donné et le second
     */
    public static long difference(long pkCardSet1, long pkCardSet2) {
        assert isValid(pkCardSet1);
        assert isValid(pkCardSet2);
        return (pkCardSet1 & complement(pkCardSet2));
    } 


    /**
     * retourne le sous-ensemble de l'ensemble de cartes empaqueté donné constitué uniquement des cartes de la couleur donnée
     * @param pkCardSet l'ensemble de cartes empaqueté donné
     * @param color la couleur donnée
     * @return le sous-ensemble de l'ensemble de cartes empaqueté donné constitué uniquement des cartes de la couleur donnée
     */
    public static long subsetOfColor(long pkCardSet, Card.Color color) {
        assert isValid(pkCardSet);
        return pkCardSet & BY_COLOR[color.ordinal()];
    }


    /**
     * retourne la représentation textuelle de l'ensemble de cartes empaqueté donné
     * @param pkCardSet l'ensemble de cartes empaqueté donné
     * @return la représentation textuelle de l'ensemble de cartes empaqueté donné
     */
    public static String toString(long pkCardSet) {
        assert isValid(pkCardSet);
        StringJoiner j = new StringJoiner(",", "{", "}");
        for(int i = 0; i < size(pkCardSet); ++i) {
            j.add(PackedCard.toString(get(pkCardSet, i)));
        }
        return j.toString();
    }    

}
