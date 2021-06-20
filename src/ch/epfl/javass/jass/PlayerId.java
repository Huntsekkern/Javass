package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * permet d'identifier chacun des quatre joueurs
 * @author Raoul Gerber (304502)
 */
public enum PlayerId {
    PLAYER_1(),
    PLAYER_2(),
    PLAYER_3(),
    PLAYER_4();

    /**
     * une liste immuable contenant toutes les valeurs du type énuméré, dans leur ordre de déclaration
     */
    public final static List<PlayerId> ALL =
            Collections.unmodifiableList(Arrays.asList(values()));

    /**
     * le nombre de valeurs du type énuméré 
     */
    public final static int COUNT = 4;

    /**
     * retourne (l'identité de) l'équipe à laquelle appartient le joueur auquel on l'applique, à savoir l'équipe 1 pour les joueurs 1 et 3, et l'équipe 2 pour les joueurs 2 et 4.
     * @return (l'identité de) l'équipe à laquelle appartient le joueur auquel on l'applique, à savoir l'équipe 1 pour les joueurs 1 et 3, et l'équipe 2 pour les joueurs 2 et 4.
     */
    public TeamId team() {
        switch(this) {
        case PLAYER_1 : 
        case PLAYER_3 : return TeamId.TEAM_1;
        case PLAYER_2 : 
        case PLAYER_4 : return TeamId.TEAM_2;

        default : return null;
        }
    }


}
