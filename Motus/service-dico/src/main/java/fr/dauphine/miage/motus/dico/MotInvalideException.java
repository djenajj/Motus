package fr.dauphine.miage.motus.dico;

/**
 * Exception metier levee lorsqu'on tente d'ajouter au dictionnaire
 * un mot qui n'est pas un mot francais valide (mauvais format, ou
 * absent du dictionnaire francais de reference).
 */
public class MotInvalideException extends RuntimeException {

    public MotInvalideException(String message) {
        super(message);
    }
}
