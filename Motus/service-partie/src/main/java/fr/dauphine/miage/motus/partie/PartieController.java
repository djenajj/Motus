package fr.dauphine.miage.motus.partie;

import fr.dauphine.miage.motus.partie.DTOs.CreerPartieRequete;
import fr.dauphine.miage.motus.partie.DTOs.ProposerMotRequete;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controleur REST du Service Partie.
 *
 * @CrossOrigin autorise le client web (HTML/JS) a appeler ce service
 * depuis le navigateur, meme s'il est servi depuis une autre origine.
 *
 * API exposees :
 *   POST /api/parties                     -> creer une partie
 *   GET  /api/parties                     -> lister les parties (admin)
 *   GET  /api/parties/{id}                -> etat d'une partie
 *   POST /api/parties/{id}/propositions   -> proposer un mot
 *   GET  /api/parties/{id}/propositions   -> historique des essais
 */
@RestController
@RequestMapping("/api/parties")
@CrossOrigin(origins = "*")
public class PartieController {

    private final PartieService partieService;

    public PartieController(PartieService partieService) {
        this.partieService = partieService;
    }

    /**
     * POST /api/parties
     * Cree une nouvelle partie. joueurId est optionnel : sans lui
     * (mode "jouer sans compte"), la partie n'est ni historisee ni
     * comptee au classement.
     * Corps : { "joueurId": 1, "longueur": 7 } ou { "longueur": 7 }
     */
    @PostMapping
    public ResponseEntity<PartieVue> creerPartie(
            @RequestBody CreerPartieRequete requete) {
        int longueur = requete.longueur > 0 ? requete.longueur : 7;
        Partie partie = partieService.creerPartie(requete.joueurId, longueur);
        return ResponseEntity.status(HttpStatus.CREATED).body(new PartieVue(partie));
    }

    /**
     * GET /api/parties
     * Liste les parties avec filtres optionnels (administration).
     * Reserve aux administrateurs : parametre "adminId" obligatoire,
     * identifiant du joueur-administrateur qui fait la requete.
     * Renvoie 403 si ce joueur n'a pas le role ADMIN.
     * Params : adminId (obligatoire), joueurId, statut (EN_COURS|GAGNEE|PERDUE),
     *          dateDebut, dateFin (yyyy-MM-dd)
     */
    @GetMapping
    public List<PartieVue> listerParties(
            @RequestParam Long adminId,
            @RequestParam(required = false) Long joueurId,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        return partieService.listerParties(adminId, joueurId, statut, dateDebut, dateFin)
                .stream().map(PartieVue::new).toList();
    }

    /**
     * GET /api/parties/{id}
     * Renvoie l'etat courant d'une partie.
     */
    @GetMapping("/{id}")
    public PartieVue getPartie(@PathVariable Long id) {
        return new PartieVue(partieService.trouverPartie(id));
    }

    /**
     * POST /api/parties/{id}/propositions
     * Soumet une proposition de mot.
     * Corps : { "motPropose": "MAISON" }
     */
    @PostMapping("/{id}/propositions")
    public PartieVue proposer(
            @PathVariable Long id,
            @RequestBody ProposerMotRequete requete) {
        return new PartieVue(partieService.proposerMot(id, requete.motPropose));
    }

    /**
     * GET /api/parties/{id}/propositions
     * Renvoie l'historique des essais d'une partie.
     */
    @GetMapping("/{id}/propositions")
    public List<Proposition> listerPropositions(@PathVariable Long id) {
        return partieService.listerPropositions(id);
    }
}
