package fr.dauphine.miage.motus.dico;

/**
 * Exception metier levee lorsqu'aucun mot ne correspond a la
 * recherche (par exemple : aucun mot de la longueur demandee).
 *
 * Elle herite de RuntimeException pour ne pas obliger a la
 * declarer partout (exception "non checked").
 */
public class MotIntrouvableException extends RuntimeException {

    public MotIntrouvableException(String message) {
        super(message);
    }
}
