package fr.dauphine.miage.motus.joueur;

/**
 * Exception metier levee lorsqu'aucun joueur ne correspond
 * a l'identifiant recherche.
 *
 * Elle herite de RuntimeException (exception "non checked").
 */
public class JoueurIntrouvableException extends RuntimeException {

    public JoueurIntrouvableException(String message) {
        super(message);
    }
}
