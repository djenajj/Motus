package fr.dauphine.miage.motus.dico;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Gestionnaire global des exceptions.
 *
 * Au lieu de laisser Spring renvoyer une erreur 500 generique,
 * on transforme nos exceptions metier en reponses HTTP claires,
 * conformes aux principes REST vus en cours (bon code de statut,
 * corps de reponse explicite).
 *
 * @RestControllerAdvice intercepte les exceptions de TOUS les
 * controleurs de l'application.
 */
@RestControllerAdvice
public class GestionnaireExceptions {

    /**
     * Quand un mot est introuvable, on renvoie un code 404 NOT FOUND.
     */
    @ExceptionHandler(MotIntrouvableException.class)
    public ResponseEntity<Map<String, Object>> gererMotIntrouvable(
            MotIntrouvableException ex) {

        Map<String, Object> corps = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "statut", 404,
                "erreur", "Mot introuvable",
                "message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corps);
    }

    /**
     * Quand un mot propose au dictionnaire n'est pas valide (mauvais
     * format, pas un mot francais reconnu, deja present...), on
     * renvoie un code 400 BAD REQUEST : c'est une erreur du client
     * (cf. tableau des codes HTTP du cours REST).
     */
    @ExceptionHandler(MotInvalideException.class)
    public ResponseEntity<Map<String, Object>> gererMotInvalide(
            MotInvalideException ex) {

        Map<String, Object> corps = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "statut", 400,
                "erreur", "Mot invalide",
                "message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corps);
    }
}
