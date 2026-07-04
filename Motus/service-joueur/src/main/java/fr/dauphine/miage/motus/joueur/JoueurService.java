package fr.dauphine.miage.motus.joueur;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Couche "service" : logique metier de la gestion des joueurs.
 *
 * Comme dans le Service Dico, on separe la logique metier du
 * controleur REST. Le controleur fait du HTTP, le service applique
 * les regles du jeu.
 */
@Service
public class JoueurService {

    private final JoueurRepository joueurRepository;

    /**
     * Injection par constructeur du repository des joueurs.
     */
    public JoueurService(JoueurRepository joueurRepository) {
        this.joueurRepository = joueurRepository;
    }

    /**
     * Enregistre un nouveau joueur.
     *
     * @throws PseudoDejaPrisException si le pseudo est deja utilise
     */
    public Joueur enregistrerJoueur(Joueur joueur) {
        if (joueurRepository.existsByPseudo(joueur.getPseudo())) {
            throw new PseudoDejaPrisException(
                    "Le pseudo '" + joueur.getPseudo() + "' est deja pris.");
        }
        // Jackson utilise le constructeur vide : on initialise les champs manquants.
        if (joueur.getDateInscription() == null) {
            joueur.setDateInscription(java.time.LocalDate.now());
        }
        if (joueur.getScoreTotal() < 0) {
            joueur.setScoreTotal(0);
        }
        return joueurRepository.save(joueur);
    }

    /**
     * Retrouve un joueur par son identifiant.
     *
     * @throws JoueurIntrouvableException si l'id n'existe pas
     */
    public Joueur trouverJoueur(Long id) {
        return joueurRepository.findById(id)
                .orElseThrow(() -> new JoueurIntrouvableException(
                        "Aucun joueur avec l'id " + id));
    }

    /**
     * Renvoie le classement : les joueurs (hors comptes ADMIN, qui
     * ne sont pas de vrais joueurs) tries par score decroissant.
     */
    public List<Joueur> classement() {
        return joueurRepository.findAllByOrderByScoreTotalDesc().stream()
                .filter(j -> !"ADMIN".equals(j.getRole()))
                .toList();
    }

    /**
     * Verifie les identifiants et renvoie le joueur correspondant.
     *
     * @throws CredentialsInvalidesException si pseudo ou mot de passe incorrect
     */
    public Joueur connecter(String pseudo, String motDePasse) {
        Joueur joueur = joueurRepository.findByPseudo(pseudo)
                .orElseThrow(() -> new CredentialsInvalidesException(
                        "Pseudo ou mot de passe incorrect."));
        if (!joueur.getMotDePasse().equals(motDePasse)) {
            throw new CredentialsInvalidesException("Pseudo ou mot de passe incorrect.");
        }
        return joueur;
    }

    /**
     * Enregistre le resultat d'une partie terminee et met a jour
     * le score total du joueur.
     *
     * Cette methode sera appelee par le Service Partie via une
     * requete REST quand une partie se termine.
     *
     * @param joueurId identifiant du joueur concerne
     * @param resultat le resultat de la partie
     * @return le resultat enregistre
     */
    @Transactional
    public Resultat enregistrerResultat(Long joueurId, Resultat resultat) {
        Joueur joueur = trouverJoueur(joueurId);

        if (resultat.getDatePartie() == null) {
            resultat.setDatePartie(java.time.LocalDateTime.now());
        }
        int score = calculerScore(resultat);
        resultat.setScore(score);
        resultat.setJoueur(joueur);
        joueur.getResultats().add(resultat);
        joueur.setScoreTotal(joueur.getScoreTotal() + score);

        joueurRepository.save(joueur);

        return resultat;
    }

    /**
     * Indique si le joueur a le role ADMIN.
     *
     * Utilisee par le Service Partie (via un appel REST) pour
     * proteger les fonctionnalites d'administration : c'est le
     * Service Joueur, proprietaire de la donnee "role", qui reste
     * seul responsable de cette verification.
     */
    public boolean estAdmin(Long joueurId) {
        return "ADMIN".equals(trouverJoueur(joueurId).getRole());
    }

    /**
     * Calcule le score d'une partie.
     *
     * Regle choisie pour le projet :
     *  - une partie gagnee rapporte 100 points ;
     *  - chaque essai utilise enleve 10 points ;
     *  - une partie perdue rapporte 0 point.
     *
     * Ainsi, gagner vite rapporte plus que gagner lentement,
     * ce qui rend le classement interessant.
     */
    private int calculerScore(Resultat resultat) {
        if (!resultat.isGagnee()) {
            return 0;
        }
        int score = 100 - (resultat.getNbEssais() * 10);
        return Math.max(score, 10); // au minimum 10 points si gagne
    }
}
