/**
 *
 * @author Raoul Gerber (304502)
 */

package ch.epfl.javass.net;


import ch.epfl.javass.jass.PrintingPlayerSelf;
import ch.epfl.javass.jass.RandomPlayerSelf;


public final class MessageTest {

    public static void main(String[] args) {
    RemotePlayerServer server = new RemotePlayerServer(new PrintingPlayerSelf(new RandomPlayerSelf(2019)));
    server.run();
    }
    
}
    
    
    //    private RemotePlayerServer server = new RemotePlayerServer(new PrintingPlayerSelf(new RandomPlayerSelf(2019)));
//    @Before
//    public void startServerTest(){
//    RemotePlayerServer server = new RemotePlayerServer(new PrintingPlayerSelf(new RandomPlayerSelf(2019)));
//            Thread serverThread = new Thread(() -> {
//                server.run();
//            });
////            serverThread.start();
////    while (!server.isRunning() {
////        Thread.sleep(1000);
////    }
//}
//
//    @Test
//    void updateHandWorks() throws Exception {
//        // Problème trouvé : readline est bloquante. Deux méthodes main ? des threads ?
//        RemotePlayerClient player = new RemotePlayerClient("localhost");
//        CardSet hand = CardSet.EMPTY.add(Card.of(Card.Color.CLUB, Card.Rank.EIGHT));
//        player.updateHand(hand);
//        player.close();
//    }
    
