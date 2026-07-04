package fr.dauphine.miage.motus.partie;

/**
 * Statut d'une lettre dans une proposition Motus.
 *
 * C'est le coeur de la regle du jeu. Pour chaque lettre proposee :
 *
 * BIEN_PLACEE : la lettre est correcte ET a la bonne position
 *               (souvent affichee en rouge dans Motus).
 * MAL_PLACEE  : la lettre existe dans le mot mystere mais a une
 *               autre position (souvent affichee en jaune / cercle).
 * ABSENTE     : la lettre n'est pas dans le mot mystere
 *               (souvent affichee en bleu / gris).
 */
public enum StatutLettre {
    BIEN_PLACEE,
    MAL_PLACEE,
    ABSENTE
}
