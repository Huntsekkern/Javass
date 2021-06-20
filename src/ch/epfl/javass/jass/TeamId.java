package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * permet d'identifier chacune des deux équipes
 * @author Raoul Gerber (304502)
 */
public enum TeamId {
    TEAM_1(),
    TEAM_2();

    /**
     * une liste immuable contenant toutes les valeurs du type énuméré, dans leur ordre de déclaration
     */
    public final static List<TeamId> ALL =
            Collections.unmodifiableList(Arrays.asList(values()));

    /**
     * le nombre de valeurs du type énuméré 
     */
    public final static int COUNT = 2;

    /**
     * retourne l'autre équipe que celle à laquelle on l'applique (TEAM_2 pour TEAM_1, et inversément)
     * @return l'autre équipe que celle à laquelle on l'applique (TEAM_2 pour TEAM_1, et inversément)
     */
    public TeamId other() {
        switch(this) {
        case TEAM_1 : return TEAM_2;
        case TEAM_2 : return TEAM_1;

        // TODO default pourrait lancer une erreur, mais pas nécessaire
        default : return null;
        }
    }


}
