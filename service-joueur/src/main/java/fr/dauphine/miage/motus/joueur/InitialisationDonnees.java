package fr.dauphine.miage.motus.joueur;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InitialisationDonnees implements CommandLineRunner {

    /**
     * Identifiants du compte administrateur cree au demarrage.
     * A utiliser pour se connecter a l'espace d'administration
     * (voir le rapport de projet / README).
     */
    private static final String ADMIN_PSEUDO = "admin";
    private static final String ADMIN_MOT_DE_PASSE = "admin123";

    private final JoueurRepository joueurRepository;

    public InitialisationDonnees(JoueurRepository joueurRepository) {
        this.joueurRepository = joueurRepository;
    }

    @Override
    public void run(String... args) {
        // Le compte administrateur : donne acces a l'espace
        // d'administration (liste/recherche des parties). C'est le
        // seul compte cree automatiquement (aucun joueur de demo :
        // les vrais joueurs s'inscrivent eux-memes, ou jouent sans
        // compte via le mode "jouer sans compte" de l'accueil).
        // Exclu du classement (voir JoueurService.classement) : ce
        // n'est pas un joueur.
        if (!joueurRepository.existsByPseudo(ADMIN_PSEUDO)) {
            Joueur admin = new Joueur(ADMIN_PSEUDO, ADMIN_MOT_DE_PASSE);
            admin.setRole("ADMIN");
            joueurRepository.save(admin);
            System.out.println(">>> Compte administrateur cree (pseudo=\"" + ADMIN_PSEUDO
                    + "\", mot de passe=\"" + ADMIN_MOT_DE_PASSE + "\").");
        }
        System.out.println(">>> Service Joueur demarre. Les joueurs s'inscrivent eux-memes.");
    }
}
