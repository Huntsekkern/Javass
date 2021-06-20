package ch.epfl.javass.jass;

import java.util.List;

import ch.epfl.javass.Preconditions;

/**
 * possède des méthodes permettant de manipuler des ensembles de cartes empaquetés dans des valeurs de type long (64 bits).
 * @author Raoul Gerber (304502)
 */
public final class CardSet {

    private final long packed;

    private CardSet(long packed) {
        this.packed = packed;
    }

    // les méthodes normales d'une classe immuable finale, mais dans notre cas "non-packed" n'ont pas besoin d'être final static


    /**
     * l'ensemble de cartes vide
     */
    public static final CardSet EMPTY = new CardSet(PackedCardSet.EMPTY);

    /**
     * l'ensemble des 36 cartes du jeu de Jass
     */
    public static final CardSet ALL_CARDS = new CardSet(PackedCardSet.ALL_CARDS);

    /**
     * retourne l'ensemble des cartes contenues dans la liste donnée
     * @param cards cartes à convertir en ensemble
     * @return l'ensemble des cartes contenues dans la liste donnée
     */
    public static CardSet of(List<Card> cards) {
        CardSet set = EMPTY;

        for (Card c: cards)
            set = set.add(c);

        return set;
    }

    /**
     * retourne l'ensemble de cartes dont packed est la version empaquetée
     * @param packed version empaquetée d'un ensemble de cartes
     * @throws IllegalArgumentException si cet argument ne représente pas un ensemble empaqueté valide
     * @return l'ensemble de cartes dont packed est la version empaquetée
     */
    public static CardSet ofPacked(long packed) {
        Preconditions.checkArgument(PackedCardSet.isValid(packed));
        return new CardSet(packed);
    } 


    /**
     * retourne la version empaquetée de l'ensemble de cartes
     * @return la version empaquetée de l'ensemble de cartes
     */
    public long packed() {
        return packed;
    } 


    /**
     * retourne vrai ssi l'ensemble de cartes est vide
     * @return vrai ssi l'ensemble de cartes est vide
     */
    public boolean isEmpty() {
        return PackedCardSet.isEmpty(packed);
    }


    /**
     * retourne la taille de l'ensemble de cartes 
     * c-à-d le nombre de cartes qu'il contient
     * @return la taille de l'ensemble de cartes
     */
    public int size() {
        return PackedCardSet.size(packed);
    }


    /**
     * retourne la carte d'index donné de l'ensemble de cartes, 
     * la carte d'index 0 étant celle correspondant au bit de poids le plus faible valant 1
     * @param index la carte d'index 0 étant celle correspondant au bit de poids le plus faible valant 1
     * @return retourne la carte d'index donné de l'ensemble de cartes
     */
    public Card get(int index) {
        Preconditions.checkIndex(index, size());
        return Card.ofPacked(PackedCardSet.get(packed, index));
    } 


    /**
     * retourne l'ensemble de cartes auquel la carte donnée a été ajoutée
     * @param card carte à ajouter
     * @return l'ensemble de cartes auquel la carte donnée a été ajoutée
     */
    public CardSet add(Card card) {
        return new CardSet(PackedCardSet.add(packed, card.packed()));
    }


    /**
     * retourne l'ensemble de cartes duquel la carte donnée a été supprimée
     * @param card carte à supprimer
     * @return l'ensemble de cartes duquel la carte donnée a été supprimée
     */
    public CardSet remove(Card card) {
        return new CardSet(PackedCardSet.remove(packed, card.packed()));
    } 


    /**
     * retourne vrai ssi l'ensemble de cartes contient la carte donnée
     * @param card carte dont on cherche à savoir si déjà incluse
     * @return vrai ssi l'ensemble de cartes contient la carte donnée
     */
    public boolean contains(Card card) {
        return PackedCardSet.contains(packed, card.packed());
    } 


    /**
     * retourne le complément de l'ensemble de cartes
     * @return le complément de l'ensemble de cartes
     */
    public CardSet complement() {
        return new CardSet(PackedCardSet.complement(packed));
    }


    /**
     * retourne l'union de l'ensemble de cartes avec l'ensemble de cartes donné
     * @param that ensemble de cartes à unir
     * @return l'union de l'ensemble de cartes avec l'ensemble de cartes donné
     */
    public CardSet union(CardSet that) {
        return new CardSet(PackedCardSet.union(packed, that.packed()));
    }  


    /**
     * retourne l'intersection de l'ensemble de cartes avec l'ensemble de cartes donné
     * @param that ensemble de carte à intersecter
     * @return l'intersection de l'ensemble de cartes avec l'ensemble de cartes donné
     */
    public CardSet intersection(CardSet that) {
        return new CardSet(PackedCardSet.intersection(packed, that.packed()));
    } 


    /** 
     * retourne la différence entre l'ensemble de cartes et un ensemble de cartes donné
     * c-à-d l'ensemble des cartes qui se trouvent dans le premier ensemble mais pas dans le second
     * @param that ensemble de carte qu'on soustrait au premier
     * @return la différence entre l'ensemble de cartes et un ensemble de cartes donné
     */
    public CardSet difference(CardSet that) {
        return new CardSet(PackedCardSet.difference(packed, that.packed()));
    } 


    /**
     * retourne le sous-ensemble de l'ensemble de cartes constitué uniquement des cartes de la couleur donnée
     * @param color la couleur donnée
     * @return le sous-ensemble de l'ensemble de cartes constitué uniquement des cartes de la couleur donnée
     */
    public CardSet subsetOfColor(Card.Color color) {
        return new CardSet(PackedCardSet.subsetOfColor(packed, color));
    }




    /** 
     * retourne vrai ssi le récepteur est égal à l'objet passé en argument, c-à-d s'il représente le même CardSet
     * @param thatO score auquel on compare
     * @return vrai ssi le récepteur est égal à l'objet passé en argument, c-à-d s'il représente le même CardSet
     */
    @Override
    public boolean equals(Object thatO) {
        if(thatO instanceof CardSet) {
            return packed == ((CardSet) thatO).packed();  
        } else {
            return false;
        }
    }

    /**
     * retourne la valeur produite par la méthode hashCode de la classe Long, appliquée au score empaqueté.
     * @return la valeur produite par la méthode hashCode de la classe Long, appliquée au score empaqueté.
     */
    @Override
    public int hashCode() {
        return Long.hashCode(packed);
    }    


    /**
     * retourne la représentation textuelle de l'ensemble de cartes 
     * @return la représentation textuelle de l'ensemble de cartes 
     */
    @Override
    public String toString() {
        return PackedCardSet.toString(packed);
    }    

}
