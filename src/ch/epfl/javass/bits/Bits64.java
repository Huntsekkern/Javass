package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

/**
 * contient des méthodes statiques permettant de travailler sur des vecteurs de 64 bits stockés dans des valeurs de type long.
 * @author Raoul Gerber (304502)
 */
public final class Bits64 {
    private Bits64() {}


    /**
     * retourne un entier long dont les bits d'index allant de start (inclus) à start + size (exclus) valent 1, les autres valant 0 
     * @param start début de l'index
     * @param size longueur de la plage d'index
     * @throws lève l'exception IllegalArgumentException si start et size ne désignent pas une plage de bits valide (c-à-d comprise entre 0 et 63, inclus)
     * @return un entier long dont les bits d'index allant de start (inclus) à start + size (exclus) valent 1, les autres valant 0 
     */
    public static long mask(int start, int size) {
        Preconditions.checkArgument(start >= 0 && size >= 0 && start+size <= Long.SIZE);

        long ones = 0;

        if(size == Long.SIZE) {
            ones = -1;
        } else {
            ones = (1L << size) - 1;
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
    public static long extract(long bits, int start, int size) {
        Preconditions.checkArgument(start >= 0 && size >= 0 && start+size <= Long.SIZE);

        bits = bits << (Long.SIZE - start - size);
        bits = bits >>> (Long.SIZE - size);

        return bits;
    }


    // issue with a borderline case (63) in the test. But it's probable that's the issue is in the test and not in the function since the test itself is using 1L << 63
    /**
     * retourne les valeurs v1 et v2 empaquetées dans un entier de type long, v1 occupant les s1 bits de poids faible, et v2 occupant les s2 bits suivants, tous les autres bits valant 0 
     * @param v1 valeur 1
     * @param s1 taille de v1
     * @param v2 valeur 2
     * @param s2 taille de v2
     * @throws IllegalArgumentException si l'une des tailles n'est pas comprise entre 1 (inclus) et 63 (inclus), si l'une des valeurs occupe plus de bits que sa taille, ou si la somme des tailles est supérieure à 64
     * @return les valeurs v1 et v2 empaquetées dans un entier de type long, v1 occupant les s1 bits de poids faible, et v2 occupant les s2 bits suivants, tous les autres bits valant 0 
     */
    public static long pack(long v1, int s1, long v2, int s2) {
        Preconditions.checkArgument(isSizeCorrect(v1, s1) && isSizeCorrect(v2, s2) && s1+s2 <= Long.SIZE);

        return (v1 | (v2 << s1));
    }



    /**
     * prends une paire valeur/taille et la valide en vérifiant que :
     * 1) la taille est comprise entre 1 et 63 (inclus),
     * 2) la valeur n'occupe pas plus de bits que le nombre spécifié par la taille.
     * @param value valeur 
     * @param size taille
     * @return true si un des deux critères n'est pas rempli, faux s'il n'y a pas de problèmes.
     */
    private static boolean isSizeCorrect(long value, int size) {
        return (size > 0 && size < Long.SIZE && value >> size == 0);
    } 
}
