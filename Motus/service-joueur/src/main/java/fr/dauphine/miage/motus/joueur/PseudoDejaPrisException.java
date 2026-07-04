package fr.dauphine.miage.motus.joueur;

/**
 * Exception metier levee lorsqu'on tente d'enregistrer un joueur
 * avec un pseudo deja utilise.
 */
public class PseudoDejaPrisException extends RuntimeException {

    public PseudoDejaPrisException(String message) {
        super(message);
    }
}
