package fr.dauphine.miage.motus.partie;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Gestionnaire global des exceptions du Service Partie.
 *
 * Transforme les exceptions metier en reponses HTTP claires :
 *  - PartieIntrouvable        -> 404 NOT FOUND
 *  - CoupInvalide             -> 400 BAD REQUEST
 *  - CommunicationService     -> 503 SERVICE UNAVAILABLE
 */
@RestControllerAdvice
public class GestionnaireExceptions {

    @ExceptionHandler(PartieIntrouvableException.class)
    public ResponseEntity<Map<String, Object>> gererPartieIntrouvable(
            PartieIntrouvableException ex) {
        return reponse(HttpStatus.NOT_FOUND, "Partie introuvable", ex.getMessage());
    }

    @ExceptionHandler(CoupInvalideException.class)
    public ResponseEntity<Map<String, Object>> gererCoupInvalide(
            CoupInvalideException ex) {
        return reponse(HttpStatus.BAD_REQUEST, "Coup invalide", ex.getMessage());
    }

    @ExceptionHandler(CommunicationServiceException.class)
    public ResponseEntity<Map<String, Object>> gererCommunication(
            CommunicationServiceException ex) {
        return reponse(HttpStatus.SERVICE_UNAVAILABLE,
                "Service indisponible", ex.getMessage());
    }

    @ExceptionHandler(AccesRefuseException.class)
    public ResponseEntity<Map<String, Object>> gererAccesRefuse(
            AccesRefuseException ex) {
        return reponse(HttpStatus.FORBIDDEN, "Acces refuse", ex.getMessage());
    }

    /**
     * Construit une reponse JSON uniforme pour les erreurs.
     */
    private ResponseEntity<Map<String, Object>> reponse(
            HttpStatus statut, String erreur, String message) {
        Map<String, Object> corps = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "statut", statut.value(),
                "erreur", erreur,
                "message", message);
        return ResponseEntity.status(statut).body(corps);
    }
}
