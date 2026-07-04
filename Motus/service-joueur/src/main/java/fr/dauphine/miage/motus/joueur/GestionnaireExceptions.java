package fr.dauphine.miage.motus.joueur;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Gestionnaire global des exceptions du Service Joueur.
 *
 * Transforme nos exceptions metier en reponses HTTP claires,
 * conformes aux principes REST (bon code de statut).
 *
 * @RestControllerAdvice intercepte les exceptions de TOUS les
 * controleurs de l'application.
 */
@RestControllerAdvice
public class GestionnaireExceptions {

    /**
     * Joueur introuvable -> code 404 NOT FOUND.
     */
    @ExceptionHandler(JoueurIntrouvableException.class)
    public ResponseEntity<Map<String, Object>> gererJoueurIntrouvable(
            JoueurIntrouvableException ex) {

        Map<String, Object> corps = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "statut", 404,
                "erreur", "Joueur introuvable",
                "message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corps);
    }

    /**
     * Validation @NotBlank echouee -> code 400 BAD REQUEST.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> gererValidation(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + " : " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        Map<String, Object> corps = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "statut", 400,
                "erreur", "Donnees invalides",
                "message", message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corps);
    }

    /**
     * Identifiants incorrects -> code 401 UNAUTHORIZED.
     */
    @ExceptionHandler(CredentialsInvalidesException.class)
    public ResponseEntity<Map<String, Object>> gererCredentialsInvalides(
            CredentialsInvalidesException ex) {

        Map<String, Object> corps = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "statut", 401,
                "erreur", "Authentification echouee",
                "message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(corps);
    }

    /**
     * Pseudo deja pris -> code 409 CONFLICT.
     * Le code 409 signifie "conflit avec l'etat actuel de la
     * ressource", ce qui correspond bien a un doublon.
     */
    @ExceptionHandler(PseudoDejaPrisException.class)
    public ResponseEntity<Map<String, Object>> gererPseudoDejaPris(
            PseudoDejaPrisException ex) {

        Map<String, Object> corps = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "statut", 409,
                "erreur", "Pseudo deja pris",
                "message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(corps);
    }
}
