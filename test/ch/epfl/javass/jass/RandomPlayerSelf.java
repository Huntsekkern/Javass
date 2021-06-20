/**
 *
 * @author Raoul Gerber (304502)
 */

package ch.epfl.javass.jass;

import java.util.Random;

public final class RandomPlayerSelf implements Player {
    private final Random rng;

    public RandomPlayerSelf(long rngSeed) {
      this.rng = new Random(rngSeed);
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
      CardSet playable = state.trick().playableCards(hand);
      return playable.get(rng.nextInt(playable.size()));
    }
  }
