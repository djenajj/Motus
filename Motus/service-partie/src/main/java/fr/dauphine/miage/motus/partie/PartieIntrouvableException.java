package fr.dauphine.miage.motus.partie;

/**
 * Exception metier levee lorsqu'aucune partie ne correspond
 * a l'identifiant recherche.
 */
public class PartieIntrouvableException extends RuntimeException {

    public PartieIntrouvableException(String message) {
        super(message);
    }
}
