package fr.dauphine.miage.motus.joueur;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controleur REST du Service Joueur.
 *
 * Toutes les URI commencent par /api/joueurs.
 *
 * API exposees :
 *   POST /api/joueurs                    -> enregistrer un joueur
 *   POST /api/joueurs/connexion          -> connexion (pseudo + mot de passe)
 *   GET  /api/joueurs/classement         -> classement par score
 *   GET  /api/joueurs/{id}               -> consulter un joueur
 *   POST /api/joueurs/{id}/resultats     -> enregistrer un resultat
 *   GET  /api/joueurs/{id}/est-admin     -> role ADMIN ? (appel interne)
 */
@RestController
@RequestMapping("/api/joueurs")
@CrossOrigin(origins = "*")
public class JoueurController {

    private final JoueurService joueurService;

    public JoueurController(JoueurService joueurService) {
        this.joueurService = joueurService;
    }

    /**
     * POST /api/joueurs/connexion
     * Verifie les identifiants et renvoie le joueur connecte.
     * Corps attendu : { "pseudo": "...", "motDePasse": "..." }
     * Renvoie 401 si les identifiants sont incorrects.
     */
    @PostMapping("/connexion")
    public Joueur connecter(@RequestBody Map<String, String> credentials) {
        return joueurService.connecter(
                credentials.getOrDefault("pseudo", ""),
                credentials.getOrDefault("motDePasse", ""));
    }

    /**
     * POST /api/joueurs
     * Enregistre un nouveau joueur.
     * Corps attendu : { "pseudo": "...", "motDePasse": "..." }
     * Renvoie le code 201 CREATED.
     */
    @PostMapping
    public ResponseEntity<Joueur> enregistrer(@Valid @RequestBody Joueur joueur) {
        Joueur cree = joueurService.enregistrerJoueur(joueur);
        return ResponseEntity.status(HttpStatus.CREATED).body(cree);
    }

    /**
     * GET /api/joueurs/classement
     * Renvoie les joueurs tries par score decroissant.
     *
     * IMPORTANT : cette methode doit etre declaree AVANT
     * getJoueur(...) car "classement" pourrait sinon etre
     * interprete comme un {id}. Spring teste les routes les
     * plus specifiques d'abord, mais on garde l'ordre clair.
     */
    @GetMapping("/classement")
    public List<Joueur> classement() {
        return joueurService.classement();
    }

    /**
     * GET /api/joueurs/{id}
     * Renvoie un joueur a partir de son identifiant.
     */
    @GetMapping("/{id}")
    public Joueur getJoueur(@PathVariable Long id) {
        return joueurService.trouverJoueur(id);
    }

    /**
     * POST /api/joueurs/{id}/resultats
     * Enregistre le resultat d'une partie pour ce joueur et met
     * a jour son score total.
     * Corps attendu : { "partieId": 1, "gagnee": true,
     *                    "nbEssais": 3, "score": 0 }
     * (le champ score est recalcule cote serveur)
     */
    @PostMapping("/{id}/resultats")
    public ResponseEntity<Resultat> ajouterResultat(
            @PathVariable Long id,
            @RequestBody Resultat resultat) {
        Resultat enregistre = joueurService.enregistrerResultat(id, resultat);
        return ResponseEntity.status(HttpStatus.CREATED).body(enregistre);
    }

    /**
     * GET /api/joueurs/{id}/est-admin
     * Indique si le joueur a le role ADMIN.
     * Appelee par le Service Partie pour proteger ses fonctionnalites
     * d'administration (voir JoueurClient cote Service Partie).
     */
    @GetMapping("/{id}/est-admin")
    public Map<String, Boolean> estAdmin(@PathVariable Long id) {
        return Map.of("estAdmin", joueurService.estAdmin(id));
    }
}
