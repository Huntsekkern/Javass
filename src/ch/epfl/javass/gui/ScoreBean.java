package ch.epfl.javass.gui;

import ch.epfl.javass.jass.TeamId;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;


/**
* bean JavaFX contenant (principalement) les scores
* @author Raoul Gerber (304502)
*/
public final class ScoreBean {
    
    private final SimpleIntegerProperty turnPointsTeam1;
    private final SimpleIntegerProperty gamePointsTeam1;
    private final SimpleIntegerProperty totalPointsTeam1;
    private final SimpleIntegerProperty turnPointsTeam2;
    private final SimpleIntegerProperty gamePointsTeam2;
    private final SimpleIntegerProperty totalPointsTeam2;
    private final SimpleObjectProperty<TeamId> winningTeam;
    
    /**
     * Construit un nouveau ScoreBean sans valeurs par défaut
     */
    public ScoreBean() {
        turnPointsTeam1 = new SimpleIntegerProperty();
        gamePointsTeam1 = new SimpleIntegerProperty();
        totalPointsTeam1 = new SimpleIntegerProperty();
        turnPointsTeam2 = new SimpleIntegerProperty();
        gamePointsTeam2 = new SimpleIntegerProperty();
        totalPointsTeam2 = new SimpleIntegerProperty();
        winningTeam = new SimpleObjectProperty<TeamId>();
    }
       

    /**
     * Retourne les points du tour courant d'une équipe donnée
     * @param team équipe donnée
     * @return les points du tour courant d'une équipe donnée
     */
    public final ReadOnlyIntegerProperty turnPointsProperty(TeamId team) {
        return (team == TeamId.TEAM_1) ? turnPointsTeam1 : turnPointsTeam2;
    }
    
    /**
     * Définit les points du tour courant d'une équipe donnée
     * @param team équipe donnée 
     * @param newTurnPoints nouveaux points du tour courant
     */
    public final void setTurnPoints(TeamId team, int newTurnPoints) { 
        if(team == TeamId.TEAM_1) {
            turnPointsTeam1.set(newTurnPoints);
        } else if(team == TeamId.TEAM_2) {
            turnPointsTeam2.set(newTurnPoints);
        }
    }
    
    
    /**
     * Retourne les points du jeu sans le tour courant d'une équipe donnée
     * @param team équipe donnée
     * @return les points du jeu sans le tour courant d'une équipe donnée
     */
    public final ReadOnlyIntegerProperty gamePointsProperty(TeamId team) {
        return (team == TeamId.TEAM_1) ? gamePointsTeam1 : gamePointsTeam2;
    }
    
    /**
     * Définit les points du jeu sans le tour courant d'une équipe donnée
     * @param team équipe donnée 
     * @param newGamePoints nouveaux points du jeu sans le tour courant
     */
    public final void setGamePoints(TeamId team, int newGamePoints) {
        if(team == TeamId.TEAM_1) {
            assert(newGamePoints >= gamePointsTeam1.get());
            gamePointsTeam1.set(newGamePoints);
        } else if(team == TeamId.TEAM_2) {
            assert(newGamePoints >= gamePointsTeam2.get());
            gamePointsTeam2.set(newGamePoints);
        }
    }
    
    /**
     * Retourne les points du jeu y compris le tour courant d'une équipe donnée
     * @param team équipe donnée
     * @return les points du jeu y compris le tour courant d'une équipe donnée
     */
    public final ReadOnlyIntegerProperty totalPointsProperty(TeamId team) { 
        return (team == TeamId.TEAM_1) ? totalPointsTeam1 : totalPointsTeam2;
    }
    
    /**
     * Définit les points du jeu y compris le tour courant d'une équipe donnée
     * @param team équipe donnée 
     * @param newTotalPoints nouveaux points du jeu y compris le tour courant
     */
    public final void setTotalPoints(TeamId team, int newTotalPoints) {
        if(team == TeamId.TEAM_1) {
            assert(newTotalPoints >= totalPointsTeam1.get());
            totalPointsTeam1.set(newTotalPoints);
        } else if(team == TeamId.TEAM_2) {
            assert(newTotalPoints >= totalPointsTeam2.get());
            totalPointsTeam2.set(newTotalPoints);
        }
    }
    
    
    /**
     * Retourne l'équipe qui a gagné la partie
     * @return l'équipe qui a gagné la partie
     */
    public final ReadOnlyObjectProperty<TeamId> winningTeamProperty() {
        return winningTeam;
    }
    
    /**
     * Définit l'équipe qui a gagné la partie
     * @param winningTeam l'équipe qui a gagné la partie
     */
    public final void setWinningTeam(TeamId winningTeam) {
        this.winningTeam.set(winningTeam);
    }
    
}
