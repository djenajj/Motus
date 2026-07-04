package fr.dauphine.miage.motus.dico;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controleur REST du Service Dico.
 *
 * @RestController : indique que cette classe traite des requetes HTTP
 * et que chaque methode renvoie directement le corps de la reponse
 * (serialise en JSON automatiquement).
 *
 * @RequestMapping("/api/mots") : toutes les URI de ce controleur
 * commencent par /api/mots (notion de "ressource" du modele REST).
 *
 * API exposees :
 *   GET  /api/mots/aleatoire?longueur=7  -> tire un mot mystere
 *   GET  /api/mots/existe?valeur=MAISON  -> verifie l'existence
 *   POST /api/mots                       -> ajoute un mot
 */
@RestController
@RequestMapping("/api/mots")
@CrossOrigin(origins = "*")
public class DicoController {

    private final DicoService dicoService;

    public DicoController(DicoService dicoService) {
        this.dicoService = dicoService;
    }

    /**
     * GET /api/mots/aleatoire?longueur=7
     * Tire et renvoie un mot mystere aleatoire de la longueur demandee.
     * C'est cette API que le Service Partie appellera pour demarrer
     * une nouvelle partie.
     *
     * @param longueur nombre de lettres (valeur par defaut : 7)
     */
    @GetMapping("/aleatoire")
    public Mot motAleatoire(
            @RequestParam(defaultValue = "7") int longueur) {
        return dicoService.tirerMotAleatoire(longueur);
    }

    /**
     * GET /api/mots/existe?valeur=MAISON
     * Indique si un mot existe dans le dictionnaire.
     * Le Service Partie appelle cette API pour valider chaque
     * proposition du joueur.
     *
     * @param valeur le mot a verifier
     * @return un objet JSON { "valeur": "...", "existe": true/false }
     */
    @GetMapping("/existe")
    public Map<String, Object> verifierExistence(
            @RequestParam String valeur) {
        boolean existe = dicoService.motExiste(valeur);
        return Map.of(
                "valeur", valeur.toUpperCase(),
                "existe", existe);
    }

    /**
     * POST /api/mots
     * Ajoute un nouveau mot au dictionnaire.
     * Le corps de la requete est un JSON, par exemple :
     *   { "valeur": "ORANGE", "langue": "FR" }
     *
     * Renvoie le code 201 CREATED, conformement aux principes REST.
     */
    @PostMapping
    public ResponseEntity<Mot> ajouterMot(@RequestBody Mot mot) {
        Mot motCree = dicoService.ajouterMot(mot);
        return ResponseEntity.status(HttpStatus.CREATED).body(motCree);
    }
}
