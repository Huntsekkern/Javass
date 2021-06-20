/**
 *
 * @author Raoul Gerber (304502)
 */

package ch.epfl.javass.jass;

import java.util.HashMap;
import java.util.Map;

public final class FourMctsJassGameSelf {
    public static void main(String[] args) {
      Map<PlayerId, Player> players = new HashMap<>();
      Map<PlayerId, String> playerNames = new HashMap<>();

      for (PlayerId pId: PlayerId.ALL) {
          Player player;
          if(pId == PlayerId.PLAYER_1 || pId == PlayerId.PLAYER_3) {
              player = new MctsPlayer(pId, 0, 100000);
          } else {
              player = new RandomPlayerSelf(2019);
          }
        if (pId == PlayerId.PLAYER_1)
      player = new PrintingPlayerSelf(player);
        players.put(pId, player);
        playerNames.put(pId, pId.name());
      }

      JassGame g = new JassGame(2019, players, playerNames);
      while (! g.isGameOver()) {
          // fail after 4 iterations...
        g.advanceToEndOfNextTrick();
        System.out.println("----");
      }
      System.out.println("gameOver");
    }
  }