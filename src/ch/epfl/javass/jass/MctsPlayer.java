package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

import ch.epfl.javass.Preconditions;

/**
 * représente un joueur simulé au moyen de l'algorithme MCTS
 * @author Raoul Gerber (304502)
 */
public final class MctsPlayer implements Player {


    private final PlayerId ownId;
    private final SplittableRandom rng;
    private final int iterations;
    private static final int MCTS_CONSTANT = 40;

    //TODO switch objects to PackedVersions later

    /**
     * construit un joueur simulé avec l'identité, la graine aléatoire et le nombre d'itérations donnés 
     * @param ownId identité donnée
     * @param rngSeed graine aléatoire donnée
     * @param iterations nombre d'itérations donné
     * @throws IllegalArgumentException si le nombre d'itérations est inférieur à 9
     */
    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
        Preconditions.checkArgument(iterations >= Jass.HAND_SIZE);
        rng = new SplittableRandom(rngSeed);
        this.ownId = ownId;
        this.iterations = iterations;

    }


    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#cardToPlay(ch.epfl.javass.jass.TurnState, ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {

        Node mainNode = new Node(state, hand, ownId);

        for(int i = 0; i<iterations; ++i) {
            update(mainNode);
        }

        return state.trick().playableCards(mainNode.hand).get(mainNode.indexOfBestChild(0));
    }



    /*
     * Rajoute un noeud au bon endroit de l'arbre, prends le score de ce noeud, 
     * puis update tous les noeuds parcourus pour créer ce nouveau noeud
     * @param updatedNode noeud de départ
     */
    private void update(Node updatedNode) {
        List<Node> affected = addNodeAndReturnIntermediates(updatedNode);
        Score score = scoreAfterRandomTurn((affected.get(affected.size()-1)));
        for(int i=0; i<affected.size(); ++i) {
            Node node = affected.get(i);
            node.turnSimulatedAmount += 1;
            node.pointsWorth += extractPointsFromScore(score);
        }
    }


    private int extractPointsFromScore(Score finalScore) {
        return finalScore.turnPoints(ownId.team());
    }



    /*
     * une méthode qui ajoute (si possible) un nouveau nœud au bon endroit de l'arbre et 
     * retourne, sous forme de liste, le chemin allant de la racine à ce nouveau nœud — 
     * les nœuds situés sur ce chemin étant ceux dont les scores doivent être mis à jour une fois le nouveau nœud évalué   
     * @param currentNode noeud actuel
     * @return sous forme de liste, le chemin allant de la racine au nouveau nœud
     */
    private List<Node> addNodeAndReturnIntermediates(Node currentNode) {
        List<Node> list = new ArrayList<>();

        list.add(currentNode);

        while(!currentNode.turnState.isTerminal()) {

            int nbrOfChildren = 0;
            for(int i=0; i<currentNode.children.length; ++i) {
                if(currentNode.children[i] == null) {
                    break;
                }
                nbrOfChildren += 1;
            }

            if(nbrOfChildren < currentNode.children.length) {
                // si le noeud n'a pas tous ses enfants, créer ce nouvel enfant et le rajouter à la liste.
                list.add(newChildren(currentNode, nbrOfChildren));
                return list;

            } else {
                // si le noeud a déjà tous ses enfants, sélectionner le bon enfant et descendre d'un cran dans l'arbre, tout en le rajoutant à la liste
                int index = currentNode.indexOfBestChild(MCTS_CONSTANT);
                currentNode = currentNode.children[index];
                list.add(currentNode);
            }

        }

        return list;
    }


    /*
     * @param currentNode noeud actuel
     * @param nbrOfChildren nombre d'enfants du noeud actuel
     * @return le nouvel enfant du noeud actuel
     */
    private Node newChildren(Node currentNode, int nbrOfChildren) {
        // joue une carte parmi celles possibles
        CardSet playableCards = currentNode.nodePlayableCards(ownId);
        Card cardPlayed = playableCards.get(nbrOfChildren);
        TurnState nextState = currentNode.turnState.withNewCardPlayedAndTrickCollected(cardPlayed);

        // rajoute un nouveau noeud parmi les enfants du noeud actuel, et le retourne
        currentNode.children[nbrOfChildren] = new Node(nextState, currentNode.hand, ownId);

        return currentNode.children[nbrOfChildren];
    }



    /*
     * retourne le score final d'un tour terminé aléatoirement à partir d'un état donné
     * @return le score final d'un tour terminé aléatoirement à partir d'un état donné
     */
    private Score scoreAfterRandomTurn(Node node) {

        TurnState evolvingState = node.turnState;
        while(!evolvingState.isTerminal()) {
            Card card = playAtRandom(node, evolvingState);

            evolvingState = evolvingState.withNewCardPlayedAndTrickCollected(card);   
        }
        return evolvingState.score();
    }



    private Card playAtRandom(Node node, TurnState evolvingState) {
        CardSet playable = node.nodePlayableCards(ownId, evolvingState);
        return playable.get(rng.nextInt(playable.size()));
    }





    /**
     * @author Raoul Gerber (304502)
     *
     */
    private final static class Node {
        private final TurnState turnState;
        private Node[] children;
        private final CardSet hand;
        // with an int for points, cannot simulate more than 8mio games
        // S(n) :
        private int pointsWorth;
        // N(n) :
        private int turnSimulatedAmount;
        //        private TeamId teamPlayed;


        private Node(TurnState turnState, CardSet hand, PlayerId ownId) {
            this.turnState = turnState;
            this.hand = hand;

            if(!turnState.isTerminal()) {
                children = new Node[nodePlayableCards(ownId).size()];
            } else {
                children = new Node[0];
            }

            pointsWorth = 0;
            turnSimulatedAmount = 0;
        }



        /*
         * retourne l'index du « meilleur » fils d'un nœud, c-à-d celui dont la valeur V est la plus élevée ;
         * peut aussi s'utiliser à la fin de l'algorithme avec c=0 pour déterminer la carte à jouer
         * @param c constante de l'algorithme MCTS
         * @return l'index du « meilleur » fils d'un nœud, c-à-d celui dont la valeur V est la plus élevée
         */
        private int indexOfBestChild(int c) {
            int index = 0;
            double value = 0;
            for(int i=0; i<children.length; ++i) {
                double valueOfChild = valueOfChild(children[i], c);
                if(valueOfChild > value) {
                    value = valueOfChild;
                    index = i;
                }
            }

            return index;
        }


        /*
         * retourne la valeur V(n) d'un enfant du noeud actuel
         * @param child noeud enfant donné
         * @param c constante c de l'algorithme MCTS
         * @return la valeur V(n) d'un enfant du noeud actuel
         */
        private double valueOfChild(Node child, int c) {
            if(child.turnSimulatedAmount <= 0) {
                return Double.MAX_VALUE;
            }
            double v = 
                    ((double)child.pointsWorth/(double)child.turnSimulatedAmount) 
                    + c*Math.sqrt(2*Math.log(this.turnSimulatedAmount)/child.turnSimulatedAmount);

            return v;
        }

        /*
         * Overload de simplification pour déterminer les cartes jouables d'un node uniquement à sa création
         * @param ownId MCTSPlayer
         * @return les cartes jouables pour un état donné
         */
        private CardSet nodePlayableCards(PlayerId ownId) {
            return (nodePlayableCards(ownId, turnState));
        }



        /*
         * une méthode qui détermine les cartes jouables pour un état donné, en tenant compte des cartes pas encore jouées et de la main du joueur
         * Cette méthode est capable de gérer les cas de simulation de fin d'un tour, à l'inverse de l'overload qui ne prend qu'un PlayerId en paramètre
         * @param ownId MCTSPlayer
         * @param evolvingState état du tour actuel, pendant que la simulation de la fin est en cours
         * @return les cartes jouables pour un état donné, en tenant compte des cartes pas encore jouées et de la main du joueur
         */
        private CardSet nodePlayableCards(PlayerId ownId, TurnState evolvingState) {
            if(evolvingState.nextPlayer() == ownId) {
                return evolvingState.trick().playableCards(hand.intersection(evolvingState.unplayedCards()));
            } else {
                return evolvingState.trick().playableCards(evolvingState.unplayedCards().difference(hand));
            }
        }   


    }

}





//Back-up de ma merveilleuse version récursive et foireuse x)
//// une méthode qui ajoute (si possible) un nouveau nœud au bon endroit de l'arbre et retourne, sous forme de liste, le chemin allant de la racine à ce nouveau nœud — les nœuds situés sur ce chemin étant ceux dont les scores doivent être mis à jour une fois le nouveau nœud évalué   
//private List<Node> addNodeAndReturnIntermediates(PlayerId ownId, Node recursive) {
//  CardSet playableCards = nodePlayableCards(ownId);
//  List<Node> list = new ArrayList<>();
//  
//  list.add(this);
//  
//  // gérer le cas du dernier tour avec un if ?
//  if(remainingCards.equals(CardSet.EMPTY)) {
//      return list;
//  }
//  System.out.println(turnSimulatedAmount);
//  // in case all potential children don't have a node yet
//  if(recursive.turnSimulatedAmount-1 < recursive.nodePlayableCards.size()) {
//      Card cardPlayed = recursive.replayableCards.get(recursive.turnSimulatedAmount-1);
//      System.out.println(cardPlayed);
//      TurnState nextState = recursive.turnState.withNewCardPlayedAndTrickCollected(cardPlayed);
////  trouble here, attributing the hand to the wrong one. And digging in the case of 9, which is the worst play...
//      
//      CardSet cards = CardSet.EMPTY;
//      if(nextState.nextPlayer() == ownId) {
//          cards = recursive.remainingCards.intersection(hand);
//          recursive.children[recursive.turnSimulatedAmount-1] = new Node(nextState, cards);
//      } else {
//          cards = recursive.remainingCards.difference(recursive.hand);
//          if(recursive.turnState.nextPlayer() == ownId) {
//              children[recursive.turnSimulatedAmount-1] = new Node(nextState, cards, cardPlayed);
//          } else {
//              children[recursive.turnSimulatedAmount-1] = new Node(nextState, cards);
//          }
//      }
//      
//      
//      list.add(recursive.children[recursive.turnSimulatedAmount-1]);
//      return list;
//  }
//  System.out.println("INDEX: " +indexOfBestChild(MctsPlayer.MCTS_CONSTANT));
//  // otherwise, we need to go deeper recursively
//  list.addAll(recursive.children[recursive.indexOfBestChild(MctsPlayer.MCTS_CONSTANT)].addNodeAndReturnIntermediates(ownId));
//  return list;
//}
//----------------------------------------------