package ch.epfl.javass.jass;

import java.util.StringJoiner;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Color;

/**
 * possède des méthodes permettant de manipuler des plis empaquetés dans des valeurs de type int
 * @author Raoul Gerber (304502)
 */
public final class PackedTrick {
    private PackedTrick() {}

    
    private static final int INDEX_LAST_TRICK = 8;
    
    private static final int CARD_LENGTH = 6;
    private static final int CARD1_START = 0;
    private static final int CARD1_END = CARD1_START + CARD_LENGTH;
    
    private static final int CARD2_START = CARD1_END;
    private static final int CARD2_END = CARD2_START + CARD_LENGTH;
    
    private static final int CARD3_START = CARD2_END;
    private static final int CARD3_END = CARD3_START + CARD_LENGTH;
    
    private static final int CARD4_START = CARD3_END;
    private static final int CARD4_END = CARD4_START + CARD_LENGTH;
    
    private static final int INDEX_START = CARD4_END;
    private static final int INDEX_LENGTH = 4;
    private static final int INDEX_END = INDEX_START + INDEX_LENGTH;
    
    private static final int PLAYER_START = INDEX_END;
    private static final int PLAYER_LENGTH = 2;
    private static final int PLAYER_END = PLAYER_START + PLAYER_LENGTH;
    
    private static final int TRUMP_START = PLAYER_END;
    private static final int TRUMP_LENGTH = 2;
//    private static final int TRUMP_END = TRUMP_START + TRUMP_LENGTH;

    /**
     * représente un pli empaqueté invalide, dont tous les 32 bits sont à 1
     */
    public static final int INVALID = Bits32.mask(CARD1_START, Integer.SIZE);



    /**
     * retourne vrai ssi l'entier donné représente un pli empaqueté valide, 
     * c-à-d si l'index est compris entre 0 et 8 (inclus) et si les éventuelles cartes invalides sont groupées dans les index supérieurs 
     * — c-à-d que le pli ne possède soit aucune carte invalide, soit une seule à l'index 3, soit deux aux index 3 et 2, soit trois aux index 3, 2 et 1, soit quatre aux index 3, 2, 1 et 0,
     * @param pkTrick int dont on teste la validité en tant que PackedTrick
     * @return vrai ssi l'entier donné représente un pli empaqueté valide, c-à-d si l'index est compris entre 0 et 8 (inclus) et si les éventuelles cartes invalides sont groupées dans les index supérieurs — c-à-d que le pli ne possède soit aucune carte invalide, soit une seule à l'index 3, soit deux aux index 3 et 2, soit trois aux index 3, 2 et 1, soit quatre aux index 3, 2, 1 et 0,
     */
    public static boolean isValid(int pkTrick) {
        if(Bits32.extract(pkTrick, INDEX_START, INDEX_LENGTH) > INDEX_LAST_TRICK) {
            return false;
        } else {
            boolean unplayedCard = false;
            for(int i=0; i<PlayerId.COUNT;++i) {
                if(!PackedCard.isValid(Bits32.extract(pkTrick, CARD_LENGTH*i, CARD_LENGTH))) {
                    unplayedCard = true;
                } else if(unplayedCard == true) {
                    return false;
                }
            }
            return true;
        }
    }


    /**
     * retourne le pli empaqueté vide — c-à-d sans aucune carte — d'index 0 avec l'atout et le premier joueur donnés
     * @param trump atout donné
     * @param firstPlayer premier joueur donné
     * @return retourne le pli empaqueté vide — c-à-d sans aucune carte — d'index 0 avec l'atout et le premier joueur donnés
     */
    public static int firstEmpty(Color trump, PlayerId firstPlayer) {
        return empty(0, firstPlayer, trump);
    }


    /**
     * retourne le pli empaqueté vide suivant celui donné (supposé plein), 
     * c-à-d le pli vide dont l'atout est identique à celui du pli donné, 
     * l'index est le successeur de celui du pli donné et le premier joueur est le vainqueur du pli donné ; 
     * si le pli donné est le dernier du tour, alors le pli invalide (INVALID) est retourné
     * @param pkTrick pli donné
     * @return le pli empaqueté vide suivant celui donné (supposé plein)
     */
    public static int nextEmpty(int pkTrick) {
        assert(isValid(pkTrick) && isFull(pkTrick));
        if(isLast(pkTrick)) {
            return INVALID;
        } else {
            return empty(index(pkTrick)+1, winningPlayer(pkTrick), trump(pkTrick));
        }       
    }
    
    private static int empty(int index, PlayerId player, Color trump) {
        return Bits32.pack(
                PackedCard.INVALID, CARD_LENGTH, 
                PackedCard.INVALID, CARD_LENGTH, 
                PackedCard.INVALID, CARD_LENGTH, 
                PackedCard.INVALID, CARD_LENGTH, 
                index, INDEX_LENGTH, 
                player.ordinal(), PLAYER_LENGTH, 
                trump.ordinal(), TRUMP_LENGTH);
    }


    /**
     * retourne vrai ssi le pli est le dernier du tour, c-à-d si son index vaut 8
     * @param pkTrick pli dont on veut savoir s'il est le dernier
     * @return vrai ssi le pli est le dernier du tour, c-à-d si son index vaut 8
     */
    public static boolean isLast(int pkTrick) {
        assert(isValid(pkTrick));
        return (index(pkTrick) == INDEX_LAST_TRICK);
    }


    /**
     * retourne vrai ssi le pli est vide, c-à-d s'il ne contient aucune carte
     * @param pkTrick pli dont on veut savoir s'il est vide
     * @return vrai ssi le pli est vide, c-à-d s'il ne contient aucune carte
     */
    public static boolean isEmpty(int pkTrick) {
        assert(isValid(pkTrick));
        return (size(pkTrick) == 0);
    }


    /**
     * retourne vrai ssi le pli est plein, c-à-d s'il contient 4 cartes
     * @param pkTrick pli dont on veut savoir s'il est plein
     * @return vrai ssi le pli est plein, c-à-d s'il contient 4 cartes
     */
    public static boolean isFull(int pkTrick) {
        assert(isValid(pkTrick));
        return (size(pkTrick) == PlayerId.COUNT);
    }


    /**
     * retourne la taille du pli, c-à-d le nombre de cartes qu'il contient
     * @param pkTrick pli dont on cherche la taille
     * @return la taille du pli, c-à-d le nombre de cartes qu'il contient
     */
    public static int size(int pkTrick) {
        assert(isValid(pkTrick));
        for(int i=0; i<PlayerId.COUNT; ++i) {
            if(Bits32.extract(pkTrick, CARD_LENGTH*i, CARD_LENGTH) == PackedCard.INVALID) {
                return i;
            }
        }
        return PlayerId.COUNT;
    }


    /**
     * retourne l'atout du pli
     * @param pkTrick pli dont on cherche l'atout
     * @return l'atout du pli
     */
    public static Color trump(int pkTrick) {
        assert(isValid(pkTrick));
        return Card.Color.ALL.get(Bits32.extract(pkTrick, TRUMP_START, TRUMP_LENGTH));
    }


    /**
     * retourne le joueur d'index donné dans le pli, le joueur d'index 0 étant le premier du pli
     * @param pkTrick pli donné
     * @param index du joueur cherché, le joueur d'index 0 étant le premier du pli
     * @return le joueur d'index donné dans le pli, le joueur d'index 0 étant le premier du pli
     */
    public static PlayerId player(int pkTrick, int index) {
        assert(isValid(pkTrick) && index >= 0 && index < PlayerId.COUNT);
        return PlayerId.ALL.get((Bits32.extract(pkTrick, PLAYER_START, PLAYER_LENGTH) + index) % PlayerId.COUNT);
    }


    /**
     * retourne l'index du pli
     * @param pkTrick pli dont on cherche l'index
     * @return l'index du pli
     */
    public static int index(int pkTrick) {
        assert(isValid(pkTrick));
        return Bits32.extract(pkTrick, INDEX_START, INDEX_LENGTH);
    }


    /**
     * retourne la version empaquetée de la carte du pli à l'index donné (supposée avoir été posée)
     * @param pkTrick pli donné
     * @param index index donné
     * @return la version empaquetée de la carte du pli à l'index donné (supposée avoir été posée)
     */
    public static int card(int pkTrick, int index) {
        assert(isValid(pkTrick) && index >= 0 && index < PlayerId.COUNT && size(pkTrick) > index);
        return Bits32.extract(pkTrick, index*CARD_LENGTH, CARD_LENGTH);
    }


    /**
     * retourne un pli identique à celui donné (supposé non plein), mais à laquelle la carte donnée a été ajoutée
     * @param pkTrick pli donné
     * @param pkCard carte à ajouter
     * @return un pli identique à celui donné (supposé non plein), mais à laquelle la carte donnée a été ajoutée
     */
    public static int withAddedCard(int pkTrick, int pkCard) {
        assert(isValid(pkTrick) && PackedCard.isValid(pkCard) && !isFull(pkTrick));

        int size = size(pkTrick);

        return ( ((~ Bits32.mask(CARD_LENGTH*size, CARD_LENGTH)) & pkTrick) | pkCard << CARD_LENGTH*size);
    }


    /**
     * retourne la couleur de base du pli, c-à-d la couleur de sa première carte (supposée avoir été jouée)
     * @param pkTrick pli donné
     * @return la couleur de base du pli, c-à-d la couleur de sa première carte (supposée avoir été jouée)
     */
    public static Color baseColor(int pkTrick) {
        assert(isValid(pkTrick) && size(pkTrick) > 0);
        return PackedCard.color(Bits32.extract(pkTrick, CARD1_START, CARD_LENGTH));
    }


    /**
     * retourne le sous-ensemble (empaqueté) des cartes de la main pkHand qui peuvent être jouées comme prochaine carte du pli pkTrick (supposé non plein)
     * @param pkTrick pli en cours
     * @param pkHand main dont on cherche les cartes jouables
     * @return le sous-ensemble (empaqueté) des cartes de la main pkHand qui peuvent être jouées comme prochaine carte du pli pkTrick (supposé non plein)
     */
    public static long playableCards(int pkTrick, long pkHand) {
        assert(isValid(pkTrick) && PackedCardSet.isValid(pkHand) && !isFull(pkTrick));

        // si firstPlayer, peut jouer n'importe quelle carte
        if(isEmpty(pkTrick)) {
            return pkHand;
        }


        long playableCards = pkHand;
        Color baseColor = baseColor(pkTrick);
        Color trump = trump(pkTrick);
        boolean trumpBase = baseColor.equals(trump);


        // si l'atout est la couleur de base et que le dernier atout en main est le bour, peut jouer n'importe quelle carte
        if(trumpBase) {
            if(PackedCardSet.subsetOfColor(playableCards, trump) == PackedCardSet.singleton(PackedCard.pack(trump, Card.Rank.JACK))) {
                return pkHand;
            }
        }

        // si le joueur a des cartes de la couleur de base, enlève de playableCards toutes les cartes qui ne sont ni atout, ni couleur de base 
        if(!PackedCardSet.isEmpty(PackedCardSet.subsetOfColor(playableCards, baseColor(pkTrick)))) {
            if(trumpBase) {
                return PackedCardSet.subsetOfColor(playableCards, trump);
            } else {
                playableCards = PackedCardSet.union(PackedCardSet.subsetOfColor(playableCards, baseColor), PackedCardSet.subsetOfColor(playableCards, trump));
            }
        } else if(trumpBase) {
            // si la couleur de base du pli est atout, et que le joueur n'a pas d'atout, peut jouer n'importe quelle carte
            return pkHand;
        }

        // maintenant, on sait que l'atout n'est pas la couleur de base, donc le reste de la méthode pourrait être conditionné par !trumpBase, mais ce n'est pas nécessaire

        // si un atout a été joué pour couper, crée un set en enlevant les atouts plus faibles de playableCards. Si ce set n'est pas vide, on l'assigne à playableCards
        boolean trumpPlayed = false;
        int indexOfBestTrump = 0;
        for(int i=0; i<size(pkTrick); ++i) {
            if(PackedCard.color(card(pkTrick, i)).equals(trump)) {
                trumpPlayed = true;
                if(PackedCard.isBetter(trump, card(pkTrick, i), card(pkTrick, indexOfBestTrump))) {
                    indexOfBestTrump = i; 
                }
            }
        }
        if(trumpPlayed) {
            long tempCards = PackedCardSet.union(PackedCardSet.subsetOfColor(playableCards, baseColor), PackedCardSet.intersection(PackedCardSet.subsetOfColor(playableCards, trump), PackedCardSet.trumpAbove(card(pkTrick, indexOfBestTrump))));
            if(!PackedCardSet.isEmpty(tempCards)) {
                playableCards = tempCards;
            }
        }
        return playableCards;       
    }


    /**
     * retourne la valeur du pli, en tenant compte des « 5 de der »
     * @param pkTrick pli dont on cherche la valeur
     * @return la valeur du pli, en tenant compte des « 5 de der »
     */
    public static int points(int pkTrick) {
        assert(isValid(pkTrick) & isFull(pkTrick));
        Color trump = trump(pkTrick);
        int points = 0;

        for(int i=0; i<PlayerId.COUNT; ++i) {
            points += PackedCard.points(trump, card(pkTrick, i));
        }

        if(index(pkTrick) == INDEX_LAST_TRICK) {
            points += Jass.LAST_TRICK_ADDITIONAL_POINTS;
        }

        return points;
    }


    /**
     * retourne l'identité du joueur menant le pli (supposé non vide)
     * @param pkTrick pli donné
     * @return l'identité du joueur menant le pli (supposé non vide)
     */
    public static PlayerId winningPlayer(int pkTrick) {
        assert(isValid(pkTrick) && !isEmpty(pkTrick));

        Color trump = trump(pkTrick);

        int winningIndex = 0;

        for(int i=1; i<size(pkTrick); ++i) {
            if(PackedCard.isBetter(trump, card(pkTrick,i), card(pkTrick, winningIndex))) {
                winningIndex = i;
            }
        }

        return player(pkTrick, winningIndex);
    }


    /**
     * retourne une représentation textuelle du pli, qui inclut une représentation des cartes jouées, dans l'ordre de jeu et séparées par une virgule
     * @param pkTrick pli donné
     * @return une représentation textuelle du pli
     */
    public static String toString(int pkTrick) {
        assert(isValid(pkTrick));

        StringJoiner j = new StringJoiner(",", "{", "}");
        for(int i = 0; i < size(pkTrick); ++i) {
            j.add(PackedCard.toString(card(pkTrick, i)));
        }

        j.add(" index:" + index(pkTrick));
        j.add(" trump:" + trump(pkTrick).toString());

        if(!isEmpty(pkTrick)) {
            j.add(" firstPlayer:" + player(pkTrick, 0));
            j.add(" winningPlayer:" + winningPlayer(pkTrick));
        }

        if(isFull(pkTrick)) {
            j.add(" points:" + points(pkTrick));
        }


        return j.toString();

    }

}
