package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.epfl.javass.Preconditions;

/**
 * Représentation d'une carte
 * @author Raoul Gerber (304502)
 *
 */
public final class Card {

    private final int packed;
    
    private Card(int packed) {
        this.packed = packed;
    }
    
    /**
     * retourne la carte de couleur et de rang donnés
     * @param c couleur donnée
     * @param r rang donné
     * @return la carte de couleur et de rang donnés
     */
    public static Card of(Color c, Rank r) {
        return new Card(PackedCard.pack(c, r));
    }
    
    /**
     * retourne la carte dont packed est la valeur empaquetée
     * @param packed carte empaquetée
     * @throws IllegalArgumentException si packed ne représente pas une carte empaquetée valide
     * @return la carte dont packed est la valeur empaquetée
     */
    public static Card ofPacked(int packed) {
        Preconditions.checkArgument(PackedCard.isValid(packed));
        return new Card(packed);
    }
    
    /**
     * retourne la version empaquetée de la carte
     * @return la version empaquetée de la carte
     */
    public int packed() {
        return packed;
    }
    
    
    /**
     * retourne la couleur de la carte
     * @return la couleur de la carte
     */
    public Color color() {
        return PackedCard.color(packed);
    }
    
    /**
     * retourne le rang de la carte
     * @return le rang de la carte
     */
    public Rank rank() {
        return PackedCard.rank(packed);
    }
    
    
    /**
     * retourne vrai ssi le récepteur (c-à-d la carte à laquelle on applique la méthode) est supérieur à la carte passée en argument (that), sachant que l'atout est trump,
     * @param trump atout en cours
     * @param that carte à laquelle on se compare 
     * @return vrai ssi le récepteur (c-à-d la carte à laquelle on applique la méthode) est supérieur à la carte passée en argument (that), sachant que l'atout est trump,
     */
    public boolean isBetter(Color trump, Card that) {
        return PackedCard.isBetter(trump, this.packed(), that.packed());
    }
    
    /**
     * retourne la valeur de la carte, sachant que l'atout est trump
     * @param trump atout en cours
     * @return  l valeur de la carte, sachant que l'atout est trump
     */
    public int points(Color trump) {
        return PackedCard.points(trump, packed);
    }
    

    /** 
     * retourne vrai ssi le récepteur est égal à l'objet passé en argument, c-à-d s'il représente la même carte
     * @param thatO carte à laquelle on compare
     * @return vrai ssi le récepteur est égal à l'objet passé en argument, c-à-d s'il représente la même carte
     */
    @Override
    public boolean equals(Object thatO) {
        if(thatO instanceof Card) {
            return packed == ((Card) thatO).packed();  
        } else {
            return false;
        }
    }
    
    /**
     * retourne la même valeur que la méthode packed
     * @return la même valeur que la méthode packed
     */
    @Override
    public int hashCode() {
        return packed;
    }    
    
    /**
     * retourne une représentation textuelle de la carte, donnée sous forme de chaîne de caractères composée du symbole de la couleur et du nom abrégé du rang
     * @return une représentation textuelle de la carte, donnée sous forme de chaîne de caractères composée du symbole de la couleur et du nom abrégé du rang
     */
    @Override
    public String toString() {
        return PackedCard.toString(packed);
    }
    
    
    
    
    /**
     * Représente la couleur d'une carte
     * @author Raoul Gerber (304502)
     *
     */
    public enum Color {
        SPADE("\u2660"),
        HEART("\u2661"),
        DIAMOND("\u2662"),
        CLUB("\u2663");
        
        
        private final String icon;
        private Color(String icon) {
          this.icon = icon;
        }
        
        /**
         * une liste immuable contenant toutes les valeurs du type énuméré, dans leur ordre de déclaration
         */
        public final static List<Color> ALL =
                Collections.unmodifiableList(Arrays.asList(values()));
                
        /**
         * le nombre de valeurs du type énuméré 
         */
        public final static int COUNT = ALL.size();
        
        /**
         * @see java.lang.Enum#toString()
         * retourne le symbole correspondant à la couleur
         */
        public String toString() {
            return icon;
        }
        
    }
    
    /**
     * Représente le rang d'une carte
     * @author Raoul Gerber (304502)
     *
     */
    public enum Rank {
        SIX("6"),
        SEVEN("7"),
        EIGHT("8"),
        NINE("9"),
        TEN("10"),
        JACK("J"),
        QUEEN("Q"),
        KING("K"),
        ACE("A");
        
        private final String id;
        private Rank(String id) {
            this.id = id;
        }
        
        
        /**
         * une liste immuable contenant toutes les valeurs du type énuméré, dans leur ordre de déclaration
         */
        public final static List<Rank> ALL =
                Collections.unmodifiableList(Arrays.asList(values()));
                
        /**
         * le nombre de valeurs du type énuméré
         */
        public final static int COUNT = ALL.size();
        
        
        /**
         * retourne la position, comprise entre 0 et 8, de la carte d'atout ayant ce rang dans l'ordre des cartes d'atout
         * @return la position, comprise entre 0 et 8, de la carte d'atout ayant ce rang dans l'ordre des cartes d'atout. En cas de "problème", retourne -1
         */
        public int trumpOrdinal() {
            switch(this) {
                case SIX : return 0;
                case SEVEN : return 1;
                case EIGHT : return 2;
                case NINE : return 7;
                case TEN : return 3;
                case JACK : return 8;
                case QUEEN : return 4;
                case KING : return 5;
                case ACE : return 6;
                
                default : return -1;
            }
        }
        
        /**
         * retourne la représentation compacte du rang
         * @return la représentation compacte du rang
         */
        public String toString() {
            return id;
        }
    }
    

}
