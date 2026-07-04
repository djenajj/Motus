package fr.dauphine.miage.motus.partie;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Algorithme central du jeu Motus.
 *
 * Compare un mot propose avec le mot mystere et renvoie, pour
 * chaque lettre, son statut : BIEN_PLACEE, MAL_PLACEE ou ABSENTE.
 *
 * LE PIEGE DES LETTRES EN DOUBLE
 * ------------------------------
 * Une approche naive ("la lettre est-elle dans le mot ?") se trompe
 * sur les doublons. Exemple : mot mystere ARBRE, proposition SERRE.
 * Le mot mystere contient deux R. La proposition SERRE en a deux
 * aussi. Il faut compter correctement pour ne pas marquer une lettre
 * "mal placee" plus de fois qu'elle n'apparait reellement.
 *
 * On procede donc EN DEUX PASSES :
 *   1re passe : on repere les lettres BIEN placees et on les
 *               "consomme" dans un compteur d'occurrences restantes.
 *   2e passe  : pour les lettres restantes, on regarde s'il reste
 *               des occurrences disponibles dans le mot mystere ;
 *               si oui -> MAL_PLACEE, sinon -> ABSENTE.
 */
@Component
public class AlgorithmeMotus {

    /**
     * Compare la proposition au mot mystere.
     *
     * @param motMystere le mot a deviner (en majuscules)
     * @param motPropose le mot propose par le joueur (en majuscules)
     * @return la liste des statuts, un par lettre de la proposition
     */
    public List<StatutLettre> comparer(String motMystere, String motPropose) {
        motMystere = motMystere.toUpperCase();
        motPropose = motPropose.toUpperCase();

        int taille = motMystere.length();
        List<StatutLettre> resultat = new ArrayList<>();
        // On initialise tout a ABSENTE, on corrigera ensuite.
        for (int i = 0; i < taille; i++) {
            resultat.add(StatutLettre.ABSENTE);
        }

        // Compteur d'occurrences de chaque lettre dans le mot mystere.
        // Indice 0 = 'A', 1 = 'B', ..., 25 = 'Z'.
        int[] occurrences = new int[26];
        for (int i = 0; i < taille; i++) {
            char c = motMystere.charAt(i);
            occurrences[c - 'A']++;
        }

        // ---- 1re passe : les lettres BIEN placees ----
        for (int i = 0; i < taille; i++) {
            char lettreProposee = motPropose.charAt(i);
            if (lettreProposee == motMystere.charAt(i)) {
                resultat.set(i, StatutLettre.BIEN_PLACEE);
                // On consomme une occurrence de cette lettre.
                occurrences[lettreProposee - 'A']--;
            }
        }

        // ---- 2e passe : les lettres MAL placees ----
        for (int i = 0; i < taille; i++) {
            // On ignore celles deja marquees BIEN_PLACEE.
            if (resultat.get(i) == StatutLettre.BIEN_PLACEE) {
                continue;
            }
            char lettreProposee = motPropose.charAt(i);
            int indice = lettreProposee - 'A';
            // Reste-t-il une occurrence disponible de cette lettre ?
            if (indice >= 0 && indice < 26 && occurrences[indice] > 0) {
                resultat.set(i, StatutLettre.MAL_PLACEE);
                occurrences[indice]--;
            }
            // sinon : la lettre reste ABSENTE (valeur par defaut)
        }

        return resultat;
    }

    /**
     * Indique si la proposition est exactement le mot mystere
     * (toutes les lettres bien placees).
     */
    public boolean estGagnant(String motMystere, String motPropose) {
        return motMystere.equalsIgnoreCase(motPropose);
    }
}
