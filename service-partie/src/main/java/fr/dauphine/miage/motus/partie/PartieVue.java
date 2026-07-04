package fr.dauphine.miage.motus.partie;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de reponse pour une partie : c'est ce que l'API renvoie
 * reellement au client, par opposition a l'entite Partie qui est
 * le modele de persistance interne.
 *
 * Le but : ne JAMAIS renvoyer le mot mystere en clair tant que la
 * partie est EN_COURS (sinon un joueur curieux peut le lire dans la
 * reponse JSON avec l'outil "reseau" du navigateur et tricher). On
 * expose uniquement sa premiere lettre, conformement a la regle du
 * jeu ("vous commencez chaque partie avec la premiere lettre du
 * mot"). Le mot complet n'est revele que lorsque la partie est
 * GAGNEE ou PERDUE.
 */
public class PartieVue {

    private final Long id;
    private final Long joueurId;
    private final String motMystere;
    private final String premiereLettreMotMystere;
    private final int longueur;
    private final StatutPartie statut;
    private final int nbEssaisMax;
    private final int nbEssaisUtilises;
    private final LocalDateTime dateDebut;
    private final LocalDateTime dateFin;
    private final List<Proposition> propositions;

    public PartieVue(Partie partie) {
        boolean terminee = partie.getStatut() != StatutPartie.EN_COURS;

        this.id = partie.getId();
        this.joueurId = partie.getJoueurId();
        this.motMystere = terminee ? partie.getMotMystere() : null;
        this.premiereLettreMotMystere = partie.getMotMystere().substring(0, 1);
        this.longueur = partie.getLongueur();
        this.statut = partie.getStatut();
        this.nbEssaisMax = partie.getNbEssaisMax();
        this.nbEssaisUtilises = partie.getNbEssaisUtilises();
        this.dateDebut = partie.getDateDebut();
        this.dateFin = partie.getDateFin();
        this.propositions = partie.getPropositions();
    }

    public Long getId() {
        return id;
    }

    public Long getJoueurId() {
        return joueurId;
    }

    public String getMotMystere() {
        return motMystere;
    }

    public String getPremiereLettreMotMystere() {
        return premiereLettreMotMystere;
    }

    public int getLongueur() {
        return longueur;
    }

    public StatutPartie getStatut() {
        return statut;
    }

    public int getNbEssaisMax() {
        return nbEssaisMax;
    }

    public int getNbEssaisUtilises() {
        return nbEssaisUtilises;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public List<Proposition> getPropositions() {
        return propositions;
    }
}
