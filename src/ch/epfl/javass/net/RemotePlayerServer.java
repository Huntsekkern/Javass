package ch.epfl.javass.net;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

/**
* représente le serveur d'un joueur, qui attend une connexion sur le port 5108 et pilote un joueur local en fonction des messages reçus
* @author Raoul Gerber (304502)
*/
public final class RemotePlayerServer {
    private Player player;
    
    private final static int COMMAND_LENGTH = 4;
    
    
    /**
     * port de connexion
     */
    public static final int JASS_PORT = 5108;
    
    
    /**
     * Construit un serveur sur l'ordinateur d'un joueur distant et pilote une simulation d'un joueur local en fonction des messages reçus du client 
     * @param player joueur distant 
     */
    public RemotePlayerServer(Player player) {
        this.player = player;
    }
        
    
    
    /**
     * lance une boucle infinie qui 
     * 1) attend un message du client,
     * 2) appelle la méthode correspondante du joueur local, et
     * 3) dans le cas de cardToPlay, renvoie la valeur de retour au client.
     */
    public void run() {
        try (
            ServerSocket s0 = new ServerSocket(JASS_PORT);
            Socket s = s0.accept();
            BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream(), US_ASCII));
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), US_ASCII))) {
            
            String str;
            while((str = r.readLine()) != null) {
                JassCommand type = JassCommand.valueOf(str.substring(0, COMMAND_LENGTH));
                str = str.substring(COMMAND_LENGTH + 1);
                
                switch(type) {
                case PLRS:
                    PlayerId ownId = PlayerId.ALL.get(StringSerializer.deserializeInt(str.substring(0, 1)));
                    
                    String[] names = StringSerializer.split(",", str.substring(2));
                    Map<PlayerId, String> playerNames = new HashMap<>();
                    for (PlayerId pId: PlayerId.ALL) {
                        playerNames.put(pId, StringSerializer.deserializeString(names[pId.ordinal()]));
                      }
                    
                    player.setPlayers(ownId, playerNames);
                    break;
                case TRMP:
                    player.setTrump(Color.ALL.get(StringSerializer.deserializeInt(str)));
                    break;
                case HAND:
                    player.updateHand(CardSet.ofPacked(StringSerializer.deserializeLong(str)));
                    break;
                case TRCK:
                    player.updateTrick(Trick.ofPacked(StringSerializer.deserializeInt(str)));
                    break;
                case CARD:
                    int spaceIndex = str.indexOf(" ");
                    
                    String stateData = str.substring(0, spaceIndex);
                    String[] data = StringSerializer.split(",", stateData);
                    TurnState state = TurnState.ofPackedComponents(StringSerializer.deserializeLong(data[0]), StringSerializer.deserializeLong(data[1]), StringSerializer.deserializeInt(data[2]));
                    
                    CardSet hand = CardSet.ofPacked(StringSerializer.deserializeLong(str.substring(spaceIndex+1)));
                    Card card = player.cardToPlay(state, hand);
                    
                    w.write(StringSerializer.serializeInt(card.packed()));
                    w.write('\n');
                    w.flush();
                    break;
                case SCOR:
                    player.updateScore(Score.ofPacked(StringSerializer.deserializeLong(str)));
                    break;
                case WINR:
                    player.setWinningTeam(TeamId.ALL.get(StringSerializer.deserializeInt(str)));
                    break;   
                default: break;
                }
            }
            s0.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
