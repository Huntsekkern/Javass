package ch.epfl.javass.net;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * contient des méthodes statiques permettant de sérialiser et désérialiser, sous forme de chaînes de caractères, des valeurs de types int, long et String
 * @author Raoul Gerber (304502)
 */
public final class StringSerializer {
    private StringSerializer() {}
    
    private final static int BASE = 16;
    
    /**
     * sérialise un entier sous la forme de sa représentation textuelle en base 16
     * @param i entier donné int
     * @return la représentation textuelle en base 16 de l'entier donné
     */
    public static String serializeInt(int i) {
        return Integer.toUnsignedString(i, BASE);
    }
    
    
    /**
     * désérialise la représentation textuelle en base 16 d'un entier
     * @param s représentation textuelle en base 16 d'un entier
     * @return l'entier désérialisé
     */
    public static int deserializeInt(String s) {
        return Integer.parseUnsignedInt(s, BASE);
    }
    
    /**
     * sérialise un long sous la forme de sa représentation textuelle en base 16
     * @param l long donné 
     * @return la représentation textuelle en base 16 du long donné
     */
    public static String serializeLong(long l) {
        return Long.toUnsignedString(l, BASE);
    }
    
    /**
     * désérialise la représentation textuelle en base 16 d'un long
     * @param s représentation textuelle en base 16 d'un long
     * @return le long désérialisé
     */
    public static long deserializeLong(String s) {
        return Long.parseUnsignedLong(s, BASE);
    }
    
    /**
     * sérialise une String sous la forme encodée en base64
     * @param s string donnée
     * @return la forme encodée en base64 de la string donnée
     */
    public static String serializeString(String s) {
        byte[] octets = s.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(octets);
    }
    
    /**
     * désérialise la forme encodée en base64 d'une string
     * @param s représentation textuelle en base64 d'une string
     * @return la string désérialisée
     */
    public static String deserializeString(String s) {
        byte[] octets = Base64.getDecoder().decode(s);
        return new String(octets, StandardCharsets.UTF_8);
    }
    
    
    /**
     * retourne la chaîne composée des chaînes données, séparées par le séparateur
     * @param separator caractère de séparation
     * @param strings nombre variable de chaînes
     * @return la chaîne composée des chaînes données, séparées par le séparateur
     */
    public static String combine(String separator, String...strings) {
        for(String s : strings) {
            assert(!s.contains(separator));
        }
        return String.join(separator, strings);
    }
    
    
    /**
     * retourne un tableau contenant les chaînes individuelles d'une string donnée
     * @param separator caractère de séparation
     * @param s chaîne unique
     * @return un tableau contenant les chaînes individuelles 
     */
    public static String[] split(String separator, String s) {
        return s.split(separator);
    }
    
    
}

