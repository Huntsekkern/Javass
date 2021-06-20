/**
 *
 * @author Raoul Gerber (304502)
 */

package ch.epfl.javass.gui;

import ch.epfl.javass.jass.TeamId;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ScoreBeanTest {

    public static void main(String[] args) {

        ScoreBean sb = new ScoreBean();
        final ChangeListener listener = new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue,
                Object newValue) {
              System.out.println("oldValue:"+ oldValue + ", newValue = " + newValue);
            }
        };
        sb.totalPointsProperty(TeamId.TEAM_2).addListener(listener);
        
        sb.setTotalPoints(TeamId.TEAM_2, 150);
        
        sb.setTotalPoints(TeamId.TEAM_2, 250);
        
        
        //TODO couldn't test the below, but all the above works fine for the 6 properties
//        sb.winningTeamProperty().addListener(listener);
//        
//        sb.setWinningTeam(TeamId.TEAM_1);
        
    }
    

}
