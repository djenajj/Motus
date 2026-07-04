package fr.dauphine.miage.motus.partie;

/**
 * Exception metier levee lorsqu'une proposition est invalide :
 *  - la partie est deja terminee ;
 *  - le mot propose n'a pas la bonne longueur ;
 *  - le mot propose n'existe pas dans le dictionnaire.
 */
public class CoupInvalideException extends RuntimeException {

    public CoupInvalideException(String message) {
        super(message);
    }
}
