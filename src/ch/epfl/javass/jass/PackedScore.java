package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;

/**
 * contient des méthodes statiques permettant de manipuler les scores d'une partie de Jass empaquetés dans un entier de type long
 * @author Raoul Gerber (304502)
 */
public final class PackedScore {
    private PackedScore() {}
    
    private static final int MAX_SCORE = 2000;
    private static final int MAX_POINTS_PER_TURN = 257;

    private static final int TEAM1_START = 0;
    private static final int TEAM1_LENGTH = 32;
    private static final int TEAM1_END = TEAM1_START + TEAM1_LENGTH;
    private static final int TEAM2_START = TEAM1_END;
    private static final int TEAM2_LENGTH = 32;
//    private static final int TEAM2_END = TEAM2_START + TEAM2_LENGTH;
    
    private static final int TRICK_START = 0;
    private static final int TRICK_LENGTH = 4;
    private static final int TRICK_END = TRICK_START + TRICK_LENGTH;
    private static final int TURNP_START = TRICK_END;
    private static final int TURNP_LENGTH = 9;
    private static final int TURNP_END = TURNP_START + TURNP_LENGTH;
    private static final int GAMEP_START = TURNP_END;
    private static final int GAMEP_LENGTH = 11;
    private static final int GAMEP_END = GAMEP_START + GAMEP_LENGTH;
    
    
    
    
    /**
     * contient le score initial d'une partie, dont les six composantes valent 0
     */
    public static final long INITIAL = 0L;

    /* 
     * Méthode cruciale à PackedScore qui permet d'extraire d'un pkScore les 32 bits concernant l'équipe donnée
     * @param pkScore pkScore donné
     * @param t équipe donnée
     * @return long dont les 32 bits de poids faible correspondent au score de l'équipe donnée
     */
    private static long extractByTeam(long pkScore, TeamId t) {
        switch(t) {
        case TEAM_1 : return Bits64.extract(pkScore, TEAM1_START, TEAM1_LENGTH);
        case TEAM_2 : return Bits64.extract(pkScore, TEAM2_START, TEAM2_LENGTH);
        default : return 0;
        }
    }




    /**
     * retourne vrai ssi la valeur donnée est un score empaqueté valide
     * c-à-d si les six composantes contiennent des valeurs comprises dans les bornes, et les bits inutilisés valent tous 0
     * @param pkScore valeur dont la validité est testée
     * @return vrai ssi la valeur donnée est un score empaqueté valide, c-à-d si les six composantes contiennent des valeurs comprises dans les bornes, et les bits inutilisés valent tous 0
     */
    public static boolean isValid(long pkScore) {
        return (Bits64.extract(pkScore, TRICK_START+TEAM1_START, TRICK_LENGTH) <= Jass.TRICKS_PER_TURN 
                && Bits64.extract(pkScore, TURNP_START+TEAM1_START, TURNP_LENGTH) <= MAX_POINTS_PER_TURN 
                && Bits64.extract(pkScore, GAMEP_START+TEAM1_START, GAMEP_LENGTH) <= MAX_SCORE
                && Bits64.extract(pkScore, TRICK_START+TEAM2_START, TRICK_LENGTH) <= Jass.TRICKS_PER_TURN 
                && Bits64.extract(pkScore, TURNP_START+TEAM2_START, TURNP_LENGTH) <= MAX_POINTS_PER_TURN 
                && Bits64.extract(pkScore, GAMEP_START+TEAM2_START, GAMEP_LENGTH) <= MAX_SCORE 
                && Bits64.extract(pkScore, GAMEP_END+TEAM1_START, TEAM1_LENGTH-GAMEP_END) == 0
                && Bits64.extract(pkScore, GAMEP_END+TEAM2_START, TEAM2_LENGTH-GAMEP_END) == 0
                && (Bits64.extract(pkScore, TRICK_START+TEAM1_START, TRICK_LENGTH) + Bits64.extract(pkScore, TRICK_START+TEAM2_START, TRICK_LENGTH)) <= Jass.TRICKS_PER_TURN
                && (Bits64.extract(pkScore, TURNP_START+TEAM1_START, TURNP_LENGTH) + Bits64.extract(pkScore, TURNP_START+TEAM2_START, TURNP_LENGTH)) <= MAX_POINTS_PER_TURN
                );
    }

    private static long packOneTeam(int turnTricks, int turnPoints, int gamePoints) {
        return Bits32.pack(turnTricks, TRICK_LENGTH, turnPoints, TURNP_LENGTH, gamePoints, GAMEP_LENGTH);
    }

    /**
     * empaquète les six composantes des scores dans un entier de type long
     * @param turnTricks1 le nombre de plis remportés par l'équipe 1 dans le tour courant
     * @param turnPoints1 le nombre de points remportés par l'équipe 1 dans le tour courant
     * @param gamePoints1 le nombre de points remportés par l'équipe 1 les tours précédents
     * @param turnTricks2 le nombre de plis remportés par l'équipe 2 dans le tour courant
     * @param turnPoints2 le nombre de points remportés par l'équipe 2 dans le tour courant
     * @param gamePoints2 le nombre de points remportés par l'équipe 2 les tours précédents
     * @return un entier de type long contenant les six composantes des scores
     */
    public static long pack(int turnTricks1, int turnPoints1, int gamePoints1, int turnTricks2, int turnPoints2, int gamePoints2) {
        long team1 = packOneTeam(turnTricks1, turnPoints1, gamePoints1);
        long team2 = packOneTeam(turnTricks2, turnPoints2, gamePoints2);
        return Bits64.pack(team1, TEAM1_LENGTH, team2, TEAM2_LENGTH);
    }


    /*
     * extrait le nombre de plis remportés dans le tour courant d'un score empaqueté donné duquel on a déjà extrait le scores d'une seule équipe
     * @param pkScore score empaqueté dans ses 32 bits de poids faible
     * @return le nombre de plis remportés dans le tour courant
     */
    private static int turnTricksOn32(long pkScore) {
        assert isValid(pkScore);
        return (int) Bits64.extract(pkScore, TRICK_START, TRICK_LENGTH);
    }

    /**
     * retourne le nombre de plis remportés par l'équipe donnée dans le tour courant des scores empaquetés donnés
     * @param pkScore valeur empaquetée contenant le score
     * @param t équipe dont le nombre de plis remportés est cherché
     * @return le nombre de plis remportés par l'équipe donnée dans le tour courant des scores empaquetés donnés
     */
    public static int turnTricks(long pkScore, TeamId t) {
        assert isValid(pkScore);
        pkScore = extractByTeam(pkScore, t);
        return turnTricksOn32(pkScore);
    }

    /*
     * extrait le nombre de points remportés dans le tour courant d'un score empaqueté donné duquel on a déjà extrait le scores d'une seule équipe
     * @param pkScore score empaqueté dans ses 32 bits de poids faible
     * @return le nombre de points remportés dans le tour courant
     */
    private static int turnPointsOn32(long pkScore) {
        assert isValid(pkScore);
        return (int) Bits64.extract(pkScore, TURNP_START, TURNP_LENGTH);
    }

    /**
     * retourne le nombre de points remportés par l'équipe donnée dans le tour courant des scores empaquetés donnés
     * @param pkScore valeur empaquetée contenant le score
     * @param t équipe dont le nombre de points remportés dans le tour courant est cherché
     * @return le nombre de points remportés par l'équipe donnée dans le tour courant des scores empaquetés donnés
     */
    public static int turnPoints(long pkScore, TeamId t) {
        assert isValid(pkScore);
        pkScore = extractByTeam(pkScore, t);
        return turnPointsOn32(pkScore);
    }

    /*
     * extrait le nombre de points remportés dans les tours précédents (sans inclure le tour courant) d'un score empaqueté donné duquel on a déjà extrait le scores d'une seule équipe
     * @param pkScore score empaqueté dans ses 32 bits de poids faible
     * @return le nombre de points remportés dans les tours précédents (sans inclure le tour courant)
     */
    private static int gamePointsOn32(long pkScore) {
        assert isValid(pkScore);
        return (int) Bits64.extract(pkScore, GAMEP_START, GAMEP_LENGTH);
    }

    /**
     * retourne le nombre de points reportés par l'équipe donnée dans les tours précédents (sans inclure le tour courant) des scores empaquetés donnés
     * c'est la valeur empaquetée telle quelle dans l'entier long.
     * @param pkScore valeur empaquetée contenant le score
     * @param t équipe dont le nombre de points remportés dans les tours précédents est cherché
     * @return le nombre de points reportés par l'équipe donnée dans les tours précédents (sans inclure le tour courant) des scores empaquetés donnés
     */
    public static int gamePoints(long pkScore, TeamId t) {
        assert isValid(pkScore);
        pkScore = extractByTeam(pkScore, t);
        return gamePointsOn32(pkScore);
    } 

    /**
     * retourne le nombre total de points remportés par l'équipe donnée dans la partie courante des scores empaquetés donnés
     * c-à-d la somme des points remportés dans les tours précédents et ceux remportés dans le tour courant
     * c'est le total de turnPoints et gamePoints
     * @param pkScore valeur empaquetée contenant le score
     * @param t équipe dont le nombre de points total est cherché
     * @return le nombre total de points remportés par l'équipe donnée dans la partie courante des scores empaquetés donnés, c-à-d la somme des points remportés dans les tours précédents et ceux remportés dans le tour courant,
     */
    public static int totalPoints(long pkScore, TeamId t) {
        assert isValid(pkScore);
        pkScore = extractByTeam(pkScore, t);
        return (turnPointsOn32(pkScore) + gamePointsOn32(pkScore));
    } 

    /**
     * retourne les scores empaquetés donnés mis à jour pour tenir compte du fait que l'équipe winningTeam a remporté un pli valant trickPoints points
     * seuls le nombre de plis et le nombre de points du tour courant sont mis à jour
     * et si cette mise à jour fait que l'équipe gagnante a remporté tous les plis du tour, alors son score est augmenté de 100 points additionels étant donné qu'elle a fait match
     * par contre, les 5 points attribués au dernier pli ne sont pas être gérés par cette méthode, ils sont gérés ailleurs
     * @param pkScore valeur empaquetée contenant le score
     * @param winningTeam l'équipe ayant marqué le pli
     * @param trickPoints le nombre de points du pli remporté
     * @return les scores empaquetés donnés mis à jour pour tenir compte du fait que l'équipe winningTeam a remporté un pli valant trickPoints points
     */
    public static long withAdditionalTrick(long pkScore, TeamId winningTeam, int trickPoints) {
        assert isValid(pkScore);
        long winningTeamStat = extractByTeam(pkScore, winningTeam);
        long losingTeamStat = extractByTeam(pkScore, winningTeam.other());

        if(turnTricksOn32(winningTeamStat)+1 == Jass.TRICKS_PER_TURN) {
            winningTeamStat = packOneTeam(turnTricksOn32(winningTeamStat)+1, turnPointsOn32(winningTeamStat)+trickPoints+Jass.MATCH_ADDITIONAL_POINTS, gamePointsOn32(winningTeamStat));
        } else {
            winningTeamStat = packOneTeam(turnTricksOn32(winningTeamStat)+1, turnPointsOn32(winningTeamStat)+trickPoints, gamePointsOn32(winningTeamStat));
        }

        if(winningTeam == TeamId.TEAM_1) {
            return Bits64.pack(winningTeamStat, TEAM1_LENGTH, losingTeamStat, TEAM2_LENGTH);
        } else {
            return Bits64.pack(losingTeamStat, TEAM1_LENGTH, winningTeamStat, TEAM2_LENGTH);
        }

    }

    /**
     * retourne les scores empaquetés donnés mis à jour pour le tour prochain
     * c-à-d avec les points obtenus par chaque équipe dans le tour courant ajoutés à leur nombre de points remportés lors de la partie
     * et les deux autres composantes remises à 0
     * @param pkScore valeur empaquetée contenant le score
     * @return les scores empaquetés donnés mis à jour pour le tour prochain, c-à-d avec les points obtenus par chaque équipe dans le tour courant ajoutés à leur nombre de points remportés lors de la partie, et les deux autres composantes remises à 0
     */
    public static long nextTurn(long pkScore) {
        assert isValid(pkScore);
        return pack(0, 0, totalPoints(pkScore, TeamId.TEAM_1), 0, 0, totalPoints(pkScore, TeamId.TEAM_2));
    } 

    /**
     * retourne la représentation textuelle des scores, cette méthode sert uniquement au déboguage
     * @param pkScore valeur empaquetée contenant le score
     * @return la représentation textuelle des scores
     */
    public static String toString(long pkScore) {
        assert isValid(pkScore);
        String str = 
                "Team1. Tricks: " + turnTricks(pkScore, TeamId.TEAM_1) 
                + " Turn Points: " + turnPoints(pkScore, TeamId.TEAM_1) 
                + " Game Points: " + gamePoints(pkScore, TeamId.TEAM_1) 
                + "\nTeam2. Tricks: " + turnTricks(pkScore, TeamId.TEAM_2) 
                + " Turn Points: " + turnPoints(pkScore, TeamId.TEAM_2) 
                + " Game Points: " + gamePoints(pkScore, TeamId.TEAM_2) 
                + "\n---";
        return str;
    } 

}
