/**
 *
 * @author Raoul Gerber (304502)
 */

package ch.epfl.javass.net;

import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PlayerId;

public final class DistantLocalJassGame {
    public static void main(String[] args) {
//      Map<PlayerId, Player> players = new HashMap<>();
//      Map<PlayerId, String> playerNames = new HashMap<>();
      
      RemotePlayerServer server = new RemotePlayerServer(new MctsPlayer(PlayerId.PLAYER_3, 0, 100000));
      server.run();

//      for (PlayerId pId: PlayerId.ALL) {
//          Player player;
//          if(pId == PlayerId.PLAYER_1) {
//              player = new MctsPlayer(pId, 0, 100000);
//          } else {
//              player = new RandomPlayerSelf(2019);
//          }
//          
//        if (pId == PlayerId.PLAYER_1)
//      player = new PrintingPlayerSelf(player);
//        
//        if (pId == PlayerId.PLAYER_3) {
////            try {
//                player = new RemotePlayerClient("localhost");
////            } catch (IOException e) {
////                // TODO Auto-generated catch block
////                e.printStackTrace();
////            }
//        }  
//        
//        players.put(pId, player);
//        playerNames.put(pId, pId.name());
//      }
//      
//
//      
//
//
//      JassGame g = new JassGame(2019, players, playerNames);
//
//      while (! g.isGameOver()) {
//        g.advanceToEndOfNextTrick();
//        System.out.println("----");
//      }
//      try {
//        ((RemotePlayerClient) players.get(PlayerId.PLAYER_3)).close();
//    } catch (Exception e) {
//        // TODO Auto-generated catch block
//        e.printStackTrace();
//    }
    }
  }