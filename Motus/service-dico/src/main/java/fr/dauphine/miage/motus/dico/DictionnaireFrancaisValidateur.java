package fr.dauphine.miage.motus.dico;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Verifie qu'un mot est un mot francais reellement existant, pour
 * empecher d'ajouter n'importe quelle suite de lettres au
 * dictionnaire (ex : "ZZZZZ").
 *
 * La reference utilisee est un fichier texte embarque dans les
 * ressources de l'application (src/main/resources/dictionnaire-fr.txt,
 * ~319 000 mots francais, un mot par ligne, sans accents, en
 * majuscules). Il est charge une seule fois en memoire au demarrage
 * dans un Set (recherche en O(1)), ce qui evite toute dependance a
 * un service externe : le microservice reste autonome et fonctionne
 * meme sans acces reseau (important pour une demo/soutenance).
 *
 * Source : projet open-source "an-array-of-french-words" (licence
 * MIT) - https://github.com/words/an-array-of-french-words
 */
@Component
public class DictionnaireFrancaisValidateur {

    private static final String FICHIER = "dictionnaire-fr.txt";
    private static final Pattern FORMAT_AUTORISE = Pattern.compile("^[A-Z]{2,20}$");

    private final Set<String> motsFrancais;

    public DictionnaireFrancaisValidateur() {
        this.motsFrancais = chargerDictionnaire();
        System.out.println(">>> Dictionnaire francais de reference charge : "
                + motsFrancais.size() + " mots.");
    }

    /**
     * Indique si un mot respecte le format attendu (uniquement des
     * lettres A-Z, 2 a 20 caracteres) ET figure bien dans le
     * dictionnaire francais de reference.
     */
    public boolean estMotFrancaisValide(String mot) {
        if (mot == null) {
            return false;
        }
        String normalise = mot.trim().toUpperCase();
        return FORMAT_AUTORISE.matcher(normalise).matches()
                && motsFrancais.contains(normalise);
    }

    private Set<String> chargerDictionnaire() {
        Set<String> mots = new HashSet<>(330_000);
        try (InputStream flux = new ClassPathResource(FICHIER).getInputStream();
             BufferedReader lecteur = new BufferedReader(
                     new InputStreamReader(flux, StandardCharsets.UTF_8))) {

            String ligne;
            while ((ligne = lecteur.readLine()) != null) {
                if (!ligne.isBlank()) {
                    mots.add(ligne.trim());
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Impossible de charger le dictionnaire francais de reference ("
                            + FICHIER + ")", e);
        }
        return mots;
    }
}
