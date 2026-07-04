package fr.dauphine.miage.motus.partie;

/**
 * Exception metier levee lorsqu'un joueur sans le role ADMIN tente
 * d'acceder a une fonctionnalite d'administration (ex : lister
 * toutes les parties).
 */
public class AccesRefuseException extends RuntimeException {

    public AccesRefuseException(String message) {
        super(message);
    }
}
