package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;

/**
 * représente les scores d'une partie de Jass
 * @author Raoul Gerber (304502)
 */
public final class Score { 

    private final long packed;


    private Score(long packed) {
        this.packed = packed;
    }

    /**
     * contient le score initial d'une partie, dont les six composantes valent 0
     */
    public static final Score INITIAL = new Score(0L);


    /**
     * retourne les scores dont packed est la version empaquetée
     * @param packed 
     * @throws IllegalArgumentException si cet argument ne représente pas des scores empaquetés valides
     * @return les scores dont packed est la version empaquetée
     */
    public static Score ofPacked(long packed) {
        Preconditions.checkArgument(PackedScore.isValid(packed));
        return new Score(packed);
    }



    /**
     * retourne la version empaquetée des scores
     * @return retourne la version empaquetée des scores
     */
    public long packed() {
        return packed;
    }



    /**
     * retourne le nombre de plis remportés par l'équipe donnée dans le tour courant du récepteur
     * @param t équipe dont le nombre de plis remportés est cherché
     * @return le nombre de plis remportés par l'équipe donnée dans le tour courant du récepteur
     */
    public int turnTricks(TeamId t) {
        return PackedScore.turnTricks(packed, t);
    }




    /**
     * retourne le nombre de points remportés par l'équipe donnée dans le tour courant du récepteur
     * @param t équipe dont le nombre de points remportés dans le tour courant est cherché
     * @return le nombre de points remportés par l'équipe donnée dans le tour courant du récepteur
     */
    public int turnPoints(TeamId t) {
        return PackedScore.turnPoints(packed, t);
    }




    /**
     * retourne le nombre de points reportés par l'équipe donnée dans les tours précédents (sans inclure le tour courant) du récepteur
     * @param t équipe dont le nombre de points remportés dans les tours précédents est cherché
     * @return le nombre de points reportés par l'équipe donnée dans les tours précédents (sans inclure le tour courant) du récepteur
     */
    public int gamePoints(TeamId t) {
        return PackedScore.gamePoints(packed, t);
    } 


    /**
     * retourne le nombre total de points remportés par l'équipe donnée dans la partie courante du récepteur
     * c-à-d la somme des points remportés dans les tours précédents et ceux remportés dans le tour courant
     * c'est le total de turnPoints et gamePoints
     * @param t équipe dont le nombre de points total est cherché
     * @return le nombre total de points remportés par l'équipe donnée dans la partie courante du récepteur, c-à-d la somme des points remportés dans les tours précédents et ceux remportés dans le tour courant,
     */
    public int totalPoints(TeamId t) {
        return PackedScore.totalPoints(packed, t);
    } 


    /**
     * retourne les scores mis-à-jour en tenant compte du fait que l'équipe winningTeam a remporté un pli valant trickPoints points
     * seuls le nombre de plis et le nombre de points du tour courant sont mis à jour
     * et si cette mise à jour fait que l'équipe gagnante a remporté tous les plis du tour, alors son score est augmenté de 100 points additionels étant donné qu'elle a fait match
     * par contre, les 5 points attribués au dernier pli ne sont pas être gérés par cette méthode, ils sont gérés ailleurs
     * @param winningTeam l'équipe ayant marqué le pli
     * @param trickPoints le nombre de points du pli remporté
     * @throws IllegalArgumentException si le nombre de points donné est inférieur à 0
     * @return les scores mis-à-jour en tenant compte du fait que l'équipe winningTeam a remporté un pli valant trickPoints points
     */
    public Score withAdditionalTrick(TeamId winningTeam, int trickPoints) {
        Preconditions.checkArgument(trickPoints >= 0);

        return new Score(PackedScore.withAdditionalTrick(packed, winningTeam, trickPoints));
    }


    /**
     * retourne les scores mis à jour pour le tour prochain
     * c-à-d avec les points obtenus par chaque équipe dans le tour courant ajoutés à leur nombre de points remportés lors de la partie
     * et les deux autres composantes remises à 0
     * @return les scores mis à jour pour le tour prochain, c-à-d avec les points obtenus par chaque équipe dans le tour courant ajoutés à leur nombre de points remportés lors de la partie, et les deux autres composantes remises à 0
     */
    public Score nextTurn() {
        return new Score(PackedScore.nextTurn(packed));
    }



    /** 
     * retourne vrai ssi le récepteur est égal à l'objet passé en argument, c-à-d s'il représente le même Score
     * @param thatO score auquel on compare
     * @return vrai ssi le récepteur est égal à l'objet passé en argument, c-à-d s'il représente le même Score
     */
    @Override
    public boolean equals(Object thatO) {
        if(thatO instanceof Score) {
            return packed == ((Score) thatO).packed();  
        } else {
            return false;
        }
    }


    /**
     * retourne la valeur produite par la méthode hashCode de la classe Long, appliquée au score empaqueté.
     * @return la valeur produite par la méthode hashCode de la classe Long, appliquée au score empaqueté.
     */
    @Override
    public int hashCode() {
        return Long.hashCode(packed);
    }    

    /**
     * retourne une représentation textuelle des scores
     * @return une représentation textuelle des scores
     */
    @Override
    public String toString() {
        return PackedScore.toString(packed);
    }

}
