package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;


/**
 * représente l'état d'un tour de jeu
 * @author Raoul Gerber (304502)
 */
public final class TurnState {

    private final long pkScore;
    private final long pkUnplayedCards;
    private final int pkTrick;

    private TurnState(long pkScore, long pkUnplayedCards, int pkTrick) {
        this.pkScore = pkScore;
        this.pkUnplayedCards = pkUnplayedCards;
        this.pkTrick = pkTrick;
    }

    /**
     * retourne l'état initial correspondant à un tour de jeu dont l'atout, le score initial et le joueur initial sont ceux donnés
     * @param trump atout donné
     * @param score score initial donné
     * @param firstPlayer joueur initial donné
     * @return l'état initial correspondant à un tour de jeu dont l'atout, le score initial et le joueur initial sont ceux donnés
     */
    public static TurnState initial(Color trump, Score score, PlayerId firstPlayer) {
        return new TurnState(
                score.packed(), 
                PackedCardSet.ALL_CARDS, 
                PackedTrick.firstEmpty(trump, firstPlayer)
                );
    }


    /**
     * retourne l'état dont les composantes (empaquetées) sont celles données
     * @param pkScore score empaqueté donné
     * @param pkUnplayedCards cardset empaqueté donné
     * @param pkTrick pli empaqueté donné
     * @throws IllegalArgumentException si l'une des composantes est invalide selon la méthode isValid correspondante
     * @return l'état dont les composantes (empaquetées) sont celles données
     */
    public static TurnState ofPackedComponents(long pkScore, long pkUnplayedCards, int pkTrick) {
        Preconditions.checkArgument(PackedScore.isValid(pkScore));
        Preconditions.checkArgument(PackedCardSet.isValid(pkUnplayedCards));
        Preconditions.checkArgument(PackedTrick.isValid(pkTrick));

        return new TurnState(pkScore, pkUnplayedCards, pkTrick);
    }


    /**
     * retourne la version empaquetée du score courant
     * @return la version empaquetée du score courant
     */
    public long packedScore() {
        return pkScore;
    }

    /**
     * retourne la version empaquetée de l'ensemble des cartes pas encore jouées
     * @return la version empaquetée de l'ensemble des cartes pas encore jouées
     */
    public long packedUnplayedCards() {
        return pkUnplayedCards;
    }

    /**
     * retourne la version empaquetée du pli courant
     * @return la version empaquetée du pli courant
     */
    public int packedTrick() {
        return pkTrick;
    }

    /**
     * retourne le score courant
     * @return le score courant
     */
    public Score score() {
        return Score.ofPacked(pkScore);
    }


    /**
     * retourne l'ensemble des cartes pas encore jouées
     * @return l'ensemble des cartes pas encore jouées
     */
    public CardSet unplayedCards() {
        return CardSet.ofPacked(pkUnplayedCards);
    }


    /**
     * retourne le pli courant
     * @return le pli courant
     */
    public Trick trick() {
        return Trick.ofPacked(pkTrick);
    }


    /**
     * retourne vrai ssi l'état est terminal, c-à-d si le dernier pli du tour a été joué
     * @return vrai ssi l'état est terminal, c-à-d si le dernier pli du tour a été joué
     */
    public boolean isTerminal() { 
        return (pkTrick == PackedTrick.INVALID);
    }


    /**
     * retourne l'identité du joueur devant jouer la prochaine carte
     * @throws IllegalStateException si le pli courant est plein
     * @return l'identité du joueur devant jouer la prochaine carte
     */
    public PlayerId nextPlayer() {
        if(PackedTrick.isFull(pkTrick)) {
            throw new IllegalStateException();
        }

        return PackedTrick.player(pkTrick, PackedTrick.size(pkTrick));

    }


    /**
     * retourne l'état correspondant à celui auquel on l'applique après que le prochain joueur ait joué la carte donnée
     * @param card carte donnée
     * @throws IllegalStateException si le pli courant est plein
     * @return l'état correspondant à celui auquel on l'applique après que le prochain joueur ait joué la carte donnée
     */
    public TurnState withNewCardPlayed(Card card) {
        if(PackedTrick.isFull(pkTrick)) {
            throw new IllegalStateException();
        }

        return new TurnState(pkScore, PackedCardSet.remove(pkUnplayedCards, card.packed()), PackedTrick.withAddedCard(pkTrick, card.packed()));        

    }


    /**
     * retourne l'état correspondant à celui auquel on l'applique après que le pli courant ait été ramassé
     * @throws IllegalStateException si le pli courant n'est pas terminé (c-à-d plein)
     * @return l'état correspondant à celui auquel on l'applique après que le pli courant ait été ramassé
     */
    public TurnState withTrickCollected() {
        if(!PackedTrick.isFull(pkTrick)) {
            throw new IllegalStateException();
        }

        // la situation ou le pli courant est le dernier du tour :
        // si isLast, pkUnplayedCard => All_CARDS, mais pas besoin ici car on repart avec initial()
        return new TurnState(PackedScore.withAdditionalTrick(pkScore, PackedTrick.winningPlayer(pkTrick).team(), PackedTrick.points(pkTrick)), pkUnplayedCards, PackedTrick.nextEmpty(pkTrick));
    }


    /**
     * retourne l'état correspondant à celui auquel on l'applique après que le prochain joueur ait joué la carte donnée, et que le pli courant ait été ramassé s'il est alors plein
     * @param card carte donnée
     * @throws IllegalStateException si le pli courant est plein
     * @return l'état correspondant à celui auquel on l'applique après que le prochain joueur ait joué la carte donnée, et que le pli courant ait été ramassé s'il est alors plein
     */
    public TurnState withNewCardPlayedAndTrickCollected(Card card) {
        if(PackedTrick.isFull(pkTrick)) {
            throw new IllegalStateException();
        }

        TurnState state = withNewCardPlayed(card);

        return ((PackedTrick.isFull(state.packedTrick())) ? state.withTrickCollected() : state);
    }

}

