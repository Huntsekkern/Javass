package ch.epfl.javass.jass;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * représente une partie de Jass
 * @author Raoul Gerber (304502)
 */
public final class JassGame {

    private final Random shuffleRng;
    private final Random trumpRng;
    private final Map<PlayerId, Player> playersFixed;
    private final Map<PlayerId, String> playerNamesFixed;

    private boolean gameOver;

    private List<Card> deck;
    private Map<PlayerId, Long> hands;

    private PlayerId startingPlayer;

    private TurnState turnState;

    /**
     * construit une partie de Jass avec la graine aléatoire et les joueurs donnés, dont l'identité est donnée par la table associative players et le nom par la table associative playerNames
     * @param rngSeed grain aléatoire
     * @param players identité des joueurs donnés
     * @param playerNames noms des joueurs donnés
     */
    public JassGame(long rngSeed, Map<PlayerId, Player> players, Map<PlayerId, String> playerNames) {
        gameOver = false;
        Random rng = new Random(rngSeed);
        this.shuffleRng = new Random(rng.nextLong());
        this.trumpRng = new Random(rng.nextLong());
        playersFixed = Collections.unmodifiableMap(new EnumMap<>(players));
        playerNamesFixed = Collections.unmodifiableMap(new EnumMap<>(playerNames));
        hands = new EnumMap<PlayerId, Long>(PlayerId.class);


        for(Entry<PlayerId, Player> player : playersFixed.entrySet()) {
            player.getValue().setPlayers(player.getKey(), playerNamesFixed);
        }

        deck = createDeck();
        distributeHands();      

        // Détermine le premier joueur
        for(int i=0; i<PlayerId.COUNT; ++i) {
            if(PackedCardSet.contains(hands.get(PlayerId.ALL.get(i)), PackedCard.pack(Color.DIAMOND, Rank.SEVEN))) {
                startingPlayer = PlayerId.ALL.get(i);
            }
        }


        Color trump = chooseRandomTrump();
        turnState = TurnState.initial(trump, Score.INITIAL, startingPlayer);

        for(Entry<PlayerId, Player> player : playersFixed.entrySet()) {
            player.getValue().setTrump(trump);
            player.getValue().updateScore(turnState.score());
        }

    }


    /**
     * retourne vrai ssi la partie est terminée
     * @return vrai ssi la partie est terminée
     */
    public boolean isGameOver() {
        return gameOver;
    }


    /**
     * fait avancer l'état du jeu jusqu'à la fin du prochain pli, ou ne fait rien si la partie est terminée
     */
    public void advanceToEndOfNextTrick() {
        if(gameOver) {
            return;
        }

        // Ramasse le pli précédent
        if(PackedTrick.isFull(turnState.packedTrick())) {
            turnState = turnState.withTrickCollected();

            for(Entry<PlayerId, Player> player : playersFixed.entrySet()) {
                player.getValue().updateScore(turnState.score());
            }
        }

        // Vérifie si une équipe a gagné
        if(checkWinners(TeamId.TEAM_1)) {
            return;
        }
        if(checkWinners(TeamId.TEAM_2)) {
            return;
        }


        // Si le tour est fini, prépare le prochain tour
        if(turnState.isTerminal()) {
            prepareNewTurn();
        }  

        for(Entry<PlayerId, Player> player : playersFixed.entrySet()) {
            player.getValue().updateTrick(turnState.trick());
        }

        // play a card 4 times
        for(int i=0; i<PlayerId.COUNT; ++i) {
            playACard(i);
        }

    }


    /*
     * Prépare le prochain tour : 
     * Nouveau premier joueur, nouvel atout, nouveau turnState, nouveau mélange de cartes et nouvelle main
     */
    private void prepareNewTurn() {
        startingPlayer = PlayerId.ALL.get((startingPlayer.ordinal() + 1) % PlayerId.COUNT);

        Color newTrump = chooseRandomTrump();

        turnState = TurnState.initial(newTrump, turnState.score().nextTurn(), startingPlayer);

        for(Entry<PlayerId, Player> player : playersFixed.entrySet()) {
            player.getValue().setTrump(newTrump);
            player.getValue().updateScore(turnState.score());
        }

        createDeck();
        distributeHands();
    }



    /*
     * Fait poser la prochaine carte et update le jeu de manière adéquate.
     */
    private void playACard(int nbrOfCardsPlayedInTrick) {
        assert(!PackedTrick.isFull(turnState.packedTrick()));
        PlayerId player = PackedTrick.player(turnState.packedTrick(), nbrOfCardsPlayedInTrick);
        Card card = playersFixed.get(player).cardToPlay(turnState, CardSet.ofPacked(hands.get(player)));
        long newHand = PackedCardSet.remove(hands.get(player), card.packed());
        
        hands.put(player, newHand);
        playersFixed.get(player).updateHand(CardSet.ofPacked(newHand));
        turnState = turnState.withNewCardPlayed(card);
        
        for(Entry<PlayerId, Player> eachPlayer : playersFixed.entrySet()) {
            eachPlayer.getValue().updateTrick(turnState.trick());
        }
    }



    /*
     * Recrée le deck de card ordonné
     * @return le deck de card ordonné
     */
    private List<Card> createDeck() {
        deck = new LinkedList<>();
        for (Color c: Color.ALL) {
            for (Rank r: Rank.ALL) {
                deck.add(Card.of(c, r));
            }
        }

        return deck;
    }


    /*
     * mélange le deck de cartes puis distribue les mains aux joueurs 
     */
    private void distributeHands() {
        Collections.shuffle(deck, shuffleRng);

        for(int i=0; i<PlayerId.COUNT; i++) {
            long hand = PackedCardSet.EMPTY;
            for(int j=0; j<Jass.HAND_SIZE; j++) {
                hand = PackedCardSet.add(hand, deck.remove(0).packed());
            }
            hands.put(PlayerId.ALL.get(i), hand);
            playersFixed.get(PlayerId.ALL.get(i)).updateHand(CardSet.ofPacked(hand));
        }
    }


    private Color chooseRandomTrump() {
        return Color.ALL.get(trumpRng.nextInt(Color.COUNT));
    }


    /*
     * Vérifie si une équipe donnée a gagné, et place gameOver à vrai si c'est le cas
     * @param checkingTeam équipe donnée
     * @return vrai si une équipe donnée a gagné
     */
    private boolean checkWinners(TeamId checkingTeam) {
        if(PackedScore.totalPoints(turnState.packedScore(), checkingTeam) >= Jass.WINNING_POINTS) {
            gameOver = true;

            for(Entry<PlayerId, Player> player : playersFixed.entrySet()) {
                player.getValue().setWinningTeam(checkingTeam);
            }
            return true;
        }
        return false;
    }
}
