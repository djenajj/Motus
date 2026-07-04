package fr.dauphine.miage.motus.partie;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Couche "service" : orchestre toute la logique du jeu Motus.
 *
 * C'est ici que tout se rejoint :
 *  - l'algorithme Motus (via AlgorithmeMotus) ;
 *  - la communication avec le Service Dico (via DicoClient) ;
 *  - la communication avec le Service Joueur (via JoueurClient) ;
 *  - la persistance des parties et propositions (via le repository).
 */
@Service
public class PartieService {

    private final PartieRepository partieRepository;
    private final AlgorithmeMotus algorithme;
    private final DicoClient dicoClient;
    private final JoueurClient joueurClient;

    /** Nombre d'essais autorises par defaut dans une partie. */
    private static final int NB_ESSAIS_PAR_DEFAUT = 6;

    public PartieService(PartieRepository partieRepository,
                         AlgorithmeMotus algorithme,
                         DicoClient dicoClient,
                         JoueurClient joueurClient) {
        this.partieRepository = partieRepository;
        this.algorithme = algorithme;
        this.dicoClient = dicoClient;
        this.joueurClient = joueurClient;
    }

    /**
     * Cree une nouvelle partie pour un joueur.
     *
     * Etapes :
     *  1. verifier qu'un compte joueur est fourni et que ce n'est pas
     *     un compte administrateur (appel REST au Service Joueur) ;
     *  2. demander un mot mystere au Service Dico (appel REST) ;
     *  3. creer la partie EN_COURS et l'enregistrer en base.
     *
     * @param joueurId identifiant du joueur (obligatoire : il faut un
     *                 compte pour jouer)
     * @param longueur longueur du mot souhaitee
     * @return la partie creee
     * @throws CoupInvalideException si aucun joueurId n'est fourni
     * @throws AccesRefuseException  si le joueur a le role ADMIN
     */
    @Transactional
    public Partie creerPartie(Long joueurId, int longueur) {
        if (joueurId == null) {
            throw new CoupInvalideException(
                    "Un compte est necessaire pour jouer une partie.");
        }
        if (joueurClient.estAdmin(joueurId)) {
            throw new AccesRefuseException(
                    "Un compte administrateur ne peut pas jouer de partie.");
        }

        // 2. Appel REST au Service Dico pour tirer le mot mystere.
        String motMystere = dicoClient.tirerMotMystere(longueur);

        // 3. Creation et persistance de la partie.
        Partie partie = new Partie(joueurId, motMystere, NB_ESSAIS_PAR_DEFAUT);
        return partieRepository.save(partie);
    }

    /**
     * Abandonne une partie en cours.
     *
     * Regle choisie pour le projet : abandonner equivaut a perdre (le
     * statut passe a PERDUE, jamais laisse EN_COURS), et le resultat
     * est envoye au Service Joueur comme une defaite.
     *
     * @throws CoupInvalideException si la partie est deja terminee
     */
    @Transactional
    public Partie abandonnerPartie(Long partieId) {
        Partie partie = trouverPartie(partieId);
        if (partie.getStatut() != StatutPartie.EN_COURS) {
            throw new CoupInvalideException(
                    "La partie est deja terminee (statut : " + partie.getStatut() + ").");
        }

        partie.setStatut(StatutPartie.PERDUE);
        partie.setDateFin(LocalDateTime.now());
        partieRepository.save(partie);

        joueurClient.envoyerResultat(
                partie.getJoueurId(), partie.getId(), false, partie.getNbEssaisUtilises());
        return partie;
    }

    /**
     * Retrouve une partie par son identifiant.
     */
    @Transactional(readOnly = true)
    public Partie trouverPartie(Long id) {
        return partieRepository.findById(id)
                .orElseThrow(() -> new PartieIntrouvableException(
                        "Aucune partie avec l'id " + id));
    }

    /**
     * Traite une proposition de mot du joueur. C'est le coeur du jeu.
     *
     * Etapes :
     *  1. verifier que la partie est encore EN_COURS ;
     *  2. verifier la longueur du mot propose ;
     *  3. demander au Service Dico si le mot existe (appel REST) ;
     *  4. calculer la reponse avec l'algorithme Motus ;
     *  5. enregistrer la proposition, incrementer les essais ;
     *  6. determiner si la partie est gagnee / perdue / continue ;
     *  7. si terminee, envoyer le resultat au Service Joueur (REST).
     *
     * @param partieId   identifiant de la partie
     * @param motPropose mot propose par le joueur
     * @return la partie mise a jour (avec la nouvelle proposition)
     */
    @Transactional
    public Partie proposerMot(Long partieId, String motPropose) {
        Partie partie = trouverPartie(partieId);
        motPropose = motPropose == null ? "" : motPropose.trim().toUpperCase();

        // 1. La partie doit etre en cours.
        if (partie.getStatut() != StatutPartie.EN_COURS) {
            throw new CoupInvalideException(
                    "La partie est terminee (statut : " + partie.getStatut() + ").");
        }

        // 2. Bonne longueur ?
        if (motPropose.length() != partie.getLongueur()) {
            throw new CoupInvalideException(
                    "Le mot doit comporter " + partie.getLongueur() + " lettres.");
        }

        // 3. Le mot existe-t-il dans le dictionnaire (appel REST au Service Dico) ?
        if (!dicoClient.motExiste(motPropose)) {
            throw new CoupInvalideException(
                    "\"" + motPropose + "\" n'existe pas dans le dictionnaire.");
        }

        // 4. Calcul de la reponse Motus.
        List<StatutLettre> statuts =
                algorithme.comparer(partie.getMotMystere(), motPropose);
        String resultatEncode = encoder(statuts);

        // 5. Enregistrement de la proposition.
        int numeroEssai = partie.getNbEssaisUtilises() + 1;
        Proposition proposition =
                new Proposition(motPropose, numeroEssai, resultatEncode);
        proposition.setPartie(partie);
        partie.getPropositions().add(proposition);
        partie.setNbEssaisUtilises(numeroEssai);

        // 6. Partie gagnee, perdue ou toujours en cours ?
        boolean gagnee = algorithme.estGagnant(partie.getMotMystere(), motPropose);
        if (gagnee) {
            partie.setStatut(StatutPartie.GAGNEE);
            partie.setDateFin(LocalDateTime.now());
        } else if (partie.getNbEssaisUtilises() >= partie.getNbEssaisMax()) {
            partie.setStatut(StatutPartie.PERDUE);
            partie.setDateFin(LocalDateTime.now());
        }

        // Persistance (cascade ALL enregistre aussi la proposition).
        partieRepository.save(partie);

        // 7. Si la partie est terminee, on previent le Service Joueur pour
        //    qu'il historise le resultat et mette a jour le score.
        if (partie.getStatut() != StatutPartie.EN_COURS) {
            joueurClient.envoyerResultat(
                    partie.getJoueurId(),
                    partie.getId(),
                    partie.getStatut() == StatutPartie.GAGNEE,
                    partie.getNbEssaisUtilises());
        }

        return partie;
    }

    /**
     * Renvoie les parties filtrees par joueur, statut et/ou plage de dates.
     * Chaque parametre est optionnel (null = pas de filtre).
     *
     * Fonctionnalite d'administration (cf. enonce du projet, section
     * "Administrer le jeu") : reservee aux joueurs ayant le role
     * ADMIN. Le role est verifie aupres du Service Joueur, seul
     * proprietaire de cette donnee (appel REST via JoueurClient).
     *
     * @param adminId identifiant du joueur qui effectue la recherche
     * @throws AccesRefuseException si ce joueur n'a pas le role ADMIN
     */
    @Transactional(readOnly = true)
    public List<Partie> listerParties(Long adminId, Long joueurId, String statut,
                                      LocalDate dateDebut, LocalDate dateFin) {
        if (!joueurClient.estAdmin(adminId)) {
            throw new AccesRefuseException(
                    "Seul un administrateur peut consulter la liste des parties.");
        }
        return partieRepository.findAll().stream()
                // joueurId (parametre) == null -> pas de filtre par joueur.
                .filter(p -> joueurId == null
                        || joueurId.equals(p.getJoueurId()))
                .filter(p -> statut == null || statut.isBlank()
                        || p.getStatut().name().equals(statut))
                .filter(p -> dateDebut == null
                        || !p.getDateDebut().toLocalDate().isBefore(dateDebut))
                .filter(p -> dateFin == null
                        || !p.getDateDebut().toLocalDate().isAfter(dateFin))
                .collect(Collectors.toList());
    }

    /**
     * Encode la liste des statuts en une chaine stockable.
     * Exemple : [BIEN_PLACEE, ABSENTE] -> "BIEN_PLACEE,ABSENTE"
     */
    private String encoder(List<StatutLettre> statuts) {
        return statuts.stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }
}
