package ch.epfl.javass;


/**
 * Préconditions devant être satisfaites avant l'appel d'une méthode 
 * 
 * @author Raoul Gerber (304502)
 *
 */
public final class Preconditions {
    private Preconditions() {}


    /**
     * lève l'exception IllegalArgumentException si son argument est faux, et ne fait rien sinon
     * @param b argument à tester
     * @throws IllegalArgumentException si b est faux.
     */
    public static void checkArgument(boolean b) {
        if(!b) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * lève l'exception IndexOutOfBoundsException si l'index donné est négatif, ou supérieur ou égal à size.
     * @param index à comparer à size
     * @param size auquel index est comparé
     * @throws IndexOutOfBoundsException si l'index donné est négatif, ou supérieur ou égal à size
     * @return index si l'exception n'a pas été levée
     */
    public static int checkIndex(int index, int size) {
        if((index < 0) || (index >= size)) {
            throw new IndexOutOfBoundsException();
        } else {
            return index;
        }
    }
}
