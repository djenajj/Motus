package fr.dauphine.miage.motus.partie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests unitaires de l'algorithme Motus.
 *
 * Ces tests ne demarrent PAS Spring : ils testent directement la
 * classe AlgorithmeMotus. Ils sont rapides et verifient la regle
 * du jeu, notamment le piege des lettres en double.
 */
class AlgorithmeMotusTest {

    private final AlgorithmeMotus algo = new AlgorithmeMotus();

    /**
     * Cas simple : toutes les lettres bien placees -> mot trouve.
     */
    @Test
    void comparer_motIdentique_toutesBienPlacees() {
        List<StatutLettre> r = algo.comparer("MAISON", "MAISON");
        for (StatutLettre s : r) {
            assertEquals(StatutLettre.BIEN_PLACEE, s);
        }
    }

    /**
     * Cas avec lettres bien placees et absentes.
     * Mot mystere : MAISON
     * Proposition : MARRON
     *  M -> BIEN_PLACEE (position 0 identique)
     *  A -> BIEN_PLACEE (MAISON et MARRON ont toutes les deux un A en position 1)
     *  R -> ABSENTE     (pas de R dans MAISON)
     *  R -> ABSENTE
     *  O -> BIEN_PLACEE (position 4 identique)
     *  N -> BIEN_PLACEE (position 5 identique)
     *
     * (Le cas MAL_PLACEE, avec le piege des lettres en double, est
     * couvert par le test comparer_lettresEnDouble ci-dessous.)
     */
    @Test
    void comparer_casMixte() {
        List<StatutLettre> r = algo.comparer("MAISON", "MARRON");
        assertEquals(StatutLettre.BIEN_PLACEE, r.get(0)); // M
        assertEquals(StatutLettre.BIEN_PLACEE, r.get(1)); // A
        assertEquals(StatutLettre.ABSENTE, r.get(2));     // R
        assertEquals(StatutLettre.ABSENTE, r.get(3));     // R
        assertEquals(StatutLettre.BIEN_PLACEE, r.get(4)); // O
        assertEquals(StatutLettre.BIEN_PLACEE, r.get(5)); // N
    }

    /**
     * LE PIEGE DES LETTRES EN DOUBLE (plafonnement).
     * Mot mystere : TABLE (un seul L, un seul E, aucun doublon)
     * Proposition : ALLEE (deux L, deux E)
     *  A -> MAL_PLACEE  (A est dans TABLE mais pas en position 0)
     *  L -> MAL_PLACEE  (le premier L : TABLE a bien un L disponible)
     *  L -> ABSENTE     (le 2e L : TABLE n'a qu'UN seul L, deja compte)
     *  E -> ABSENTE     (le seul E de TABLE est deja bien place en pos 4)
     *  E -> BIEN_PLACEE (position 4 identique dans les deux mots)
     *
     * Ce test montre que l'algorithme ne marque pas une lettre
     * "presente" plus de fois qu'elle n'apparait reellement dans
     * le mot mystere. C'est tout l'interet des deux passes.
     */
    @Test
    void comparer_lettresEnDouble() {
        List<StatutLettre> r = algo.comparer("TABLE", "ALLEE");
        assertEquals(StatutLettre.MAL_PLACEE, r.get(0));  // A
        assertEquals(StatutLettre.MAL_PLACEE, r.get(1));  // L (1er)
        assertEquals(StatutLettre.ABSENTE, r.get(2));     // L (2e)
        assertEquals(StatutLettre.ABSENTE, r.get(3));     // E (1er)
        assertEquals(StatutLettre.BIEN_PLACEE, r.get(4)); // E (2e, bien place)
    }

    /**
     * Verifie la detection du mot gagnant (insensible a la casse).
     */
    @Test
    void estGagnant() {
        assertTrue(algo.estGagnant("MAISON", "maison"));
        assertFalse(algo.estGagnant("MAISON", "MARRON"));
    }
}
