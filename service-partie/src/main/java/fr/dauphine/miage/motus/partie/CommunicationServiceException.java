package fr.dauphine.miage.motus.partie;

/**
 * Exception levee lorsqu'un autre microservice (Dico, Joueur) est
 * injoignable ou renvoie une reponse inattendue.
 */
public class CommunicationServiceException extends RuntimeException {

    public CommunicationServiceException(String message) {
        super(message);
    }
}
