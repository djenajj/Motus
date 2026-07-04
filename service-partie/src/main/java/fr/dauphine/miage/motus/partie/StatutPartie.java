package fr.dauphine.miage.motus.partie;

/**
 * Statut d'une partie de Motus.
 *
 * EN_COURS : la partie est en cours, le joueur peut encore proposer.
 * GAGNEE   : le joueur a trouve le mot mystere.
 * PERDUE   : le joueur a epuise ses essais sans trouver.
 */
public enum StatutPartie {
    EN_COURS,
    GAGNEE,
    PERDUE
}
