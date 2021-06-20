/**
 *
 * @author Raoul Gerber (304502)
 */

package ch.epfl.javass.jass;

import java.util.HashMap;
import java.util.Map;

public final class PacedPlayerTestSelf {
    public static void main(String[] args) {
      Map<PlayerId, Player> players = new HashMap<>();
      Map<PlayerId, String> playerNames = new HashMap<>();

      for (PlayerId pId: PlayerId.ALL) {
        Player player = new RandomPlayerSelf(2019);
        if (pId == PlayerId.PLAYER_1)
      player = new PacedPlayer(player, 3);
        // add sysout in PacedPlayer cardToPlay to test !!
        players.put(pId, player);
        playerNames.put(pId, pId.name());
      }

      JassGame g = new JassGame(2019, players, playerNames);
      while (! g.isGameOver()) {
        g.advanceToEndOfNextTrick();
        System.out.println("----");
      }
    }
  }
