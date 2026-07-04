package fr.dauphine.miage.motus.dico;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Peuple le dictionnaire du JEU (le pool de mots-mysteres possibles,
 * pas le dictionnaire de validation des propositions) au demarrage.
 *
 * La liste vient du fichier resources/mots-jouables.txt (~2900 mots
 * de 5 a 8 lettres, un par ligne) plutot que d'etre codee en dur ici :
 * a cette taille, une liste Java aurait ete illisible. Ces mots sont
 * une selection de mots francais courants (issus d'une liste de
 * frequence lexicale, filtree sur la longueur), volontairement plus
 * restreinte que le dictionnaire de validation (~319 000 mots) pour
 * que le mot mystere reste facile a deviner.
 */
@Component
public class InitialisationDonnees implements CommandLineRunner {

    private static final String FICHIER = "mots-jouables.txt";

    private final MotRepository motRepository;

    public InitialisationDonnees(MotRepository motRepository) {
        this.motRepository = motRepository;
    }

    @Override
    public void run(String... args) throws IOException {
        int ajoutes = 0;
        try (InputStream flux = new ClassPathResource(FICHIER).getInputStream();
             BufferedReader lecteur = new BufferedReader(
                     new InputStreamReader(flux, StandardCharsets.UTF_8))) {

            String ligne;
            while ((ligne = lecteur.readLine()) != null) {
                String valeur = ligne.trim().toUpperCase();
                if (!valeur.isEmpty() && !motRepository.existsByValeur(valeur)) {
                    motRepository.save(new Mot(valeur, "FR"));
                    ajoutes++;
                }
            }
        }

        long total = motRepository.count();
        System.out.println(">>> Dictionnaire du jeu initialise : " + total
                + " mots charges (" + ajoutes + " nouveaux).");
    }
}
