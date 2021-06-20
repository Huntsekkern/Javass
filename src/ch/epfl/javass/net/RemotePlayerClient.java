/**
 * représente le client d'un joueur, se trouve sur l'ordinateur sur lequel se déroule la partie
 * @author Raoul Gerber (304502)
 */

package ch.epfl.javass.net;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.net.UnknownHostException;
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

public final class RemotePlayerClient implements Player, AutoCloseable {
    
    private BufferedReader r;
    private BufferedWriter w;
    private Socket s;
    
    /**
     * Construit un client sur l'ordinateur sur lequel se déroule la partie qui transmet au joueur distant les évènements de la partie.
     * @param hostName nom de l'hôte auquel le client se connecte
     * @throws IOException 
     */
    public RemotePlayerClient(String hostName) throws UnknownHostException, IOException {
        s = new Socket(hostName, RemotePlayerServer.JASS_PORT);
        r = new BufferedReader(new InputStreamReader(s.getInputStream(), US_ASCII));
        w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), US_ASCII));
    }

    
    private void writeLineAndFlush(String s) {
        try {
            w.write(s);
            w.write('\n');
            w.flush();
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#cardToPlay(ch.epfl.javass.jass.TurnState, ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        String score = StringSerializer.serializeLong(state.packedScore());
        String unplayedCards = StringSerializer.serializeLong(state.packedUnplayedCards());
        String trick = StringSerializer.serializeInt(state.packedTrick());
        String handStr = StringSerializer.serializeLong(hand.packed());
        
        String s = StringSerializer.combine(",", score, unplayedCards, trick);
        writeLineAndFlush(JassCommand.CARD.name() + " " + s + " " + handStr);
        
        String card = "";
        try {
            card = r.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        
        return Card.ofPacked(StringSerializer.deserializeInt(card));
        
    }
    
    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setPlayers(ch.epfl.javass.jass.PlayerId, java.util.Map)
     */
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        String[] names = new String[4];
        for (PlayerId pId: PlayerId.ALL) {
            names[pId.ordinal()] = StringSerializer.serializeString(playerNames.get(pId));
          }
        writeLineAndFlush(JassCommand.PLRS.name() + " " + StringSerializer.serializeInt(ownId.ordinal()) + " " + StringSerializer.combine(",", names));
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateHand(ch.epfl.javass.jass.CardSet)
     */
    @Override
    public void updateHand(CardSet newHand) {
        writeLineAndFlush(JassCommand.HAND.name() + " " + StringSerializer.serializeLong(newHand.packed()));
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setTrump(ch.epfl.javass.jass.Card.Color)
     */
    @Override
    public void setTrump(Color trump) {
        writeLineAndFlush(JassCommand.TRMP.name() + " " + StringSerializer.serializeInt(trump.ordinal()));
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateTrick(ch.epfl.javass.jass.Trick)
     */
    @Override
    public void updateTrick(Trick newTrick) {
        writeLineAndFlush(JassCommand.TRCK.name() + " " + StringSerializer.serializeInt(newTrick.packed()));
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateScore(ch.epfl.javass.jass.Score)
     */
    @Override
    public void updateScore(Score score) {
        writeLineAndFlush(JassCommand.SCOR.name() + " " + StringSerializer.serializeLong(score.packed()));
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setWinningTeam(ch.epfl.javass.jass.TeamId)
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        writeLineAndFlush(JassCommand.WINR.name() + " " + StringSerializer.serializeInt(winningTeam.ordinal()));
    }

    /* (non-Javadoc)
     * appelle les méthodes close des flots d'entrée et de sortie de la prise utilisée pour se connecter au serveur, ainsi que celle de la prise elle-même.
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() throws Exception {
        r.close();
        w.close();
        s.close();
    }



}
