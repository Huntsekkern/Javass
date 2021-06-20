package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * possède des méthodes permettant de manipuler des plis empaquetés dans des valeurs de type int
 * @author Raoul Gerber (304502)
 */
public final class Trick {
    private final int packed;

    private Trick(int packed) {
        this.packed = packed;
    }

    /**
     * représente un pli invalide
     */
    public static final Trick INVALID = new Trick(PackedTrick.INVALID);


    /**
     * retourne le pli vide — c-à-d sans aucune carte — d'index 0 avec l'atout et le premier joueur donnés
     * @param trump atout donné
     * @param firstPlayer premier joueur donné
     * @return retourne le pli vide — c-à-d sans aucune carte — d'index 0 avec l'atout et le premier joueur donnés
     */
    public static Trick firstEmpty(Color trump, PlayerId firstPlayer) {
        return new Trick(PackedTrick.firstEmpty(trump, firstPlayer));
    }

    /**
     * retourne le pli dont la version empaquetée est celle donnée
     * @param packed pli empaqueté donné
     * @throws IllegalArgumentException si celui-ci n'est pas valide (selon PackedTrick.isValid)
     * @return le pli dont la version empaquetée est celle donnée
     */
    public static Trick ofPacked(int packed) {
        Preconditions.checkArgument(PackedTrick.isValid(packed));
        return new Trick(packed);
    }

    /**
     * retourne la version empaquetée du pli
     * @return la version empaquetée du pli
     */
    public int packed() {
        return packed;
    }


    /**
     * retourne le pli vide suivant l'actuel (supposé plein), 
     * c-à-d le pli vide dont l'atout est identique à celui du pli donné, 
     * l'index est le successeur de celui du pli actuel et le premier joueur est le vainqueur du pli actuel; 
     * si le pli est le dernier du tour, alors le pli invalide (INVALID) est retourné
     * @throws IllegalStateException si le pli n'est pas plein
     * @return le pli vide suivant l'actuel (supposé plein)
     */
    public Trick nextEmpty() {
        // TODO déclarer ou traiter ? soit je doit rajouter Type method(..) throws Exception1, Exception2 dans le titre de la méthode
        // soit je dois la gérer avec un bloc try catch ???
        // Dans les deux cas, repasser dans Card, CardSet et Score pour adapter ça !!! et même içi : ctrl+F sur tous les mots clés throw pour les adapter.
        // Selon le forum, pas besoin de s'occuper de ça pour le moment   
        if(!isFull()) {
            throw new IllegalStateException();
        }

        return new Trick(PackedTrick.nextEmpty(packed));
    }


    /**
     * retourne vrai ssi le pli est le dernier du tour, c-à-d si son index vaut 8
     * @return vrai ssi le pli est le dernier du tour, c-à-d si son index vaut 8
     */
    public boolean isLast() {
        return PackedTrick.isLast(packed);
    }


    /**
     * retourne vrai ssi le pli est vide, c-à-d s'il ne contient aucune carte
     * @return vrai ssi le pli est vide, c-à-d s'il ne contient aucune carte
     */
    public boolean isEmpty() {
        return PackedTrick.isEmpty(packed);
    }


    /**
     * retourne vrai ssi le pli est plein, c-à-d s'il contient 4 cartes
     * @return vrai ssi le pli est plein, c-à-d s'il contient 4 cartes
     */
    public boolean isFull() {
        return PackedTrick.isFull(packed);
    }


    /**
     * retourne la taille du pli, c-à-d le nombre de cartes qu'il contient
     * @return la taille du pli, c-à-d le nombre de cartes qu'il contient
     */
    public int size() {
        return PackedTrick.size(packed);
    }


    /**
     * retourne l'atout du pli
     * @return l'atout du pli
     */
    public Color trump() {
        return PackedTrick.trump(packed);
    }


    /**
     * retourne le joueur d'index donné dans le pli, le joueur d'index 0 étant le premier du pli
     * @param index du joueur cherché, le joueur d'index 0 étant le premier du pli
     * @throws IndexOutOfBoundsException si l'index n'est pas compris entre 0 (inclus) et 4 (exclus)
     * @return le joueur d'index donné dans le pli, le joueur d'index 0 étant le premier du pli
     */
    public PlayerId player(int index) {
        Preconditions.checkIndex(index, PlayerId.COUNT);
        return PackedTrick.player(packed, index);
    }


    /**
     * retourne l'index du pli
     * @return l'index du pli
     */
    public int index() {
        return PackedTrick.index(packed);
    }


    /**
     * retourne la carte du pli à l'index donné (supposée avoir été posée)
     * @param index index donné
     * @throws IndexOutOfBoundsException si l'index n'est pas compris entre 0 (inclus) et la taille du pli (exclus)
     * @return la carte du pli à l'index donné (supposée avoir été posée)
     */
    public Card card(int index) {
        Preconditions.checkIndex(index, size());
        return Card.ofPacked(PackedTrick.card(packed, index));
    }


    /**
     * retourne un pli identique à l'actuel (supposé non plein), mais à laquelle la carte donnée a été ajoutée
     * @param c carte à ajouter
     * @throws IllegalStateException si le pli est plein
     * @return un pli identique à l'actuel (supposé non plein), mais à laquelle la carte donnée a été ajoutée
     */
    public Trick withAddedCard(Card c) {
        if(isFull()) {
            throw new IllegalStateException();
        }

        return new Trick(PackedTrick.withAddedCard(packed, c.packed()));
    }


    /**
     * retourne la couleur de base du pli, c-à-d la couleur de sa première carte (supposée avoir été jouée)
     * @throws IllegalStateException si le pli est vide
     * @return la couleur de base du pli, c-à-d la couleur de sa première carte (supposée avoir été jouée)
     */
    public Color baseColor() {
        if(isEmpty()) {
            throw new IllegalStateException();
        }

        return PackedTrick.baseColor(packed);
    }


    /**
     * retourne le sous-ensemble des cartes de la main Hand qui peuvent être jouées comme prochaine carte du pli actuel (supposé non plein)
     * @param hand main dont on cherche les cartes jouables
     * @throws IllegalStateException si le pli est plein
     * @return le sous-ensemble des cartes de la main Hand qui peuvent être jouées comme prochaine carte du pli actuel (supposé non plein)
     */
    public CardSet playableCards(CardSet hand) {
        if(isFull()) {
            throw new IllegalStateException();
        }

        return CardSet.ofPacked(PackedTrick.playableCards(packed, hand.packed()));
    }


    /**
     * retourne la valeur du pli, en tenant compte des « 5 de der »
     * @return la valeur du pli, en tenant compte des « 5 de der »
     */
    public int points() {
        return PackedTrick.points(packed);
    }


    /**
     * retourne l'identité du joueur menant le pli (supposé non vide)
     * @throws IllegalStateException si le pli est vide
     * @return l'identité du joueur menant le pli (supposé non vide)
     */
    public PlayerId winningPlayer() {
        if(isEmpty()) {
            throw new IllegalStateException();
        }

        return PackedTrick.winningPlayer(packed);
    }


    /** 
     * retourne vrai ssi le récepteur est égal à l'objet passé en argument, c-à-d s'il représente le même Trick
     * @param thatO score auquel on compare
     * @return vrai ssi le récepteur est égal à l'objet passé en argument, c-à-d s'il représente le même Trick
     */
    @Override
    public boolean equals(Object thatO) {
        if(thatO instanceof Trick) {
            return packed == ((Trick) thatO).packed();  
        } else {
            return false;
        }
    }

    /**
     * retourne la valeur produite par la méthode hashCode de la classe Long, appliquée au pli empaqueté.
     * @return la valeur produite par la méthode hashCode de la classe Long, appliquée au pli empaqueté.
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(packed);
    }    


    /**
     * retourne la représentation textuelle du pli 
     * @return la représentation textuelle du pli 
     */
    @Override
    public String toString() {
        return PackedTrick.toString(packed);
    }    

}
