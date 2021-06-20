package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

/**
 * contient des méthodes statiques permettant de travailler sur des vecteurs de 32 bits stockés dans des valeurs de type int.
 * 
 * @author Raoul Gerber (304502)
 *
 */
public final class Bits32 {
    private Bits32() {}


    /**
     * retourne un entier dont les bits d'index allant de start (inclus) à start + size (exclus) valent 1, les autres valant 0 
     * @param start début de l'index
     * @param size longueur de la plage d'index
     * @throws lève l'exception IllegalArgumentException si start et size ne désignent pas une plage de bits valide (c-à-d comprise entre 0 et 31, inclus)
     * @return un entier dont les bits d'index allant de start (inclus) à start + size (exclus) valent 1, les autres valant 0 
     */
    public static int mask(int start, int size) {
        Preconditions.checkArgument(start >= 0 && size >= 0 && start+size <= Integer.SIZE);
        
        int ones = 0;

        if(size == Integer.SIZE) {
            ones = -1;
        } else {
            ones = (1 << size) - 1;
            ones = ones << start;
        }

        return ones;
    }



    /**
     * retourne une valeur dont les size bits de poids faible sont égaux à ceux de bits allant de l'index start (inclus) à l'index start + size (exclus)
     * @param bits séquence sur laquelle l'extraction est effectuée
     * @param start début de l'index à extraire
     * @param size longueur de la plage d'index à extraire
     * @throws IllegalArgumentException si start et size ne désignent pas une plage de bits valide
     * @return une valeur dont les size bits de poids faible sont égaux à ceux de bits allant de l'index start (inclus) à l'index start + size (exclus)
     */
    public static int extract(int bits, int start, int size) {
        Preconditions.checkArgument(start >= 0 && size >= 0 && start+size <= Integer.SIZE);

        bits = bits << (Integer.SIZE - start - size);
        bits = bits >>> (Integer.SIZE - size);

        return bits;
    }

    /**
     * retourne les valeurs v1 et v2 empaquetées dans un entier de type int, v1 occupant les s1 bits de poids faible, et v2 occupant les s2 bits suivants, tous les autres bits valant 0 
     * @param v1 valeur 1
     * @param s1 taille de v1
     * @param v2 valeur 2
     * @param s2 taille de v2
     * @throws IllegalArgumentException si l'une des tailles n'est pas comprise entre 1 (inclus) et 31 (inclus), si l'une des valeurs occupe plus de bits que sa taille, ou si la somme des tailles est supérieure à 32
     * @return les valeurs v1 et v2 empaquetées dans un entier de type int, v1 occupant les s1 bits de poids faible, et v2 occupant les s2 bits suivants, tous les autres bits valant 0 
     */
    public static int pack(int v1, int s1, int v2, int s2) {
        Preconditions.checkArgument(isSizeCorrect(v1, s1) && isSizeCorrect(v2, s2) && s1+s2 <= Integer.SIZE);

        return (v1 | (v2 << s1));
    }


    /**
     * retourne les valeurs v1, v2 et v3 empaquetées dans un entier de type int, v1 occupant les s1 bits de poids faible, v2 occupant les s2 bits suivants, v3 occupant les 32 bits suivants, tous les autres bits valant 0 
     * @param v1 valeur 1
     * @param s1 taille de v1
     * @param v2 valeur 2
     * @param s2 taille de v2
     * @param v3 valeur 3
     * @param s3 taille de v3
     * @throws IllegalArgumentException si l'une des tailles n'est pas comprise entre 1 (inclus) et 31 (inclus), si l'une des valeurs occupe plus de bits que sa taille, ou si la somme des tailles est supérieure à 32
     * @return les valeurs v1, v2 et v3 empaquetées dans un entier de type int, v1 occupant les s1 bits de poids faible, v2 occupant les s2 bits suivants, v3 occupant les 32 bits suivants, tous les autres bits valant 0 
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3) {
        Preconditions.checkArgument(isSizeCorrect(v1, s1) && isSizeCorrect(v2, s2) && isSizeCorrect(v3, s3) && (s1+s2+s3) <= Integer.SIZE);

        return (v1 | (v2 << s1) | (v3 << (s1+s2)));
    }

    /**
     * retourne les valeurs v1, v2,..., v7 empaquetées dans un entier de type int, v1 occupant les s1 bits de poids faible, v2 occupant les s2 bits suivants, v3 occupant les 32 bits suivants, etc., tous les autres bits valant 0 
     * @param v1 valeur 1
     * @param s1 taille de v1
     * @param v2 valeur 2
     * @param s2 taille de v2
     * @param v3 valeur 3
     * @param s3 taille de v3
     * @param v4 valeur 4
     * @param s4 taille de v4
     * @param v5 valeur 5
     * @param s5 taille de v5
     * @param v6 valeur 6
     * @param s6 taille de v6     
     * @param v7 valeur 7
     * @param s7 taille de v7
     * @throws IllegalArgumentException si l'une des tailles n'est pas comprise entre 1 (inclus) et 31 (inclus), si l'une des valeurs occupe plus de bits que sa taille, ou si la somme des tailles est supérieure à 32
     * @return les valeurs v1, v2,..., v7 empaquetées dans un entier de type int, v1 occupant les s1 bits de poids faible, v2 occupant les s2 bits suivants, v3 occupant les 32 bits suivants, etc., tous les autres bits valant 0 
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3, int v4, int s4, int v5, int s5, int v6, int s6, int v7, int s7) {
        Preconditions.checkArgument(isSizeCorrect(v1, s1) && isSizeCorrect(v2, s2) && isSizeCorrect(v3, s3) && isSizeCorrect(v4, s4) && isSizeCorrect(v5, s5) && isSizeCorrect(v6, s6) && isSizeCorrect(v7, s7) && (s1+s2+s3+s4+s5+s6+s7) <= Integer.SIZE);

        return (v1 | (v2 << s1) | (v3 << (s1+s2)) | (v4 << (s1+s2+s3)) | (v5 << (s1+s2+s3+s4)) | (v6 << (s1+s2+s3+s4+s5)) | (v7 << (s1+s2+s3+s4+s5+s6)) );
    }


    /**
     * prends une paire valeur/taille et la valide en vérifiant que :
     * 1) la taille est comprise entre 1 et 31 (inclus),
     * 2) la valeur n'occupe pas plus de bits que le nombre spécifié par la taille.
     * @param value valeur 
     * @param size taille
     * @return true s'il n'y a pas de problèmes.
     */
    private static boolean isSizeCorrect(int value, int size) {
        return (size > 0 && size < Integer.SIZE && value >> size == 0);
    } 


}
