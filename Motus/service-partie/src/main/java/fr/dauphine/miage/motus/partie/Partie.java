package fr.dauphine.miage.motus.partie;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entite JPA representant une partie de Motus.
 *
 * Une partie appartient a un joueur (on stocke juste son id, car
 * le joueur vit dans un autre microservice), porte sur un mot
 * mystere, et contient la liste des propositions deja faites.
 */
@Entity
@Table(name = "partie")
public class Partie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identifiant du joueur (gere par le Service Joueur).
     * Pas de cle etrangere : les bases sont separees.
     *
     * Obligatoire : un compte est necessaire pour jouer. La creation
     * d'une partie sans joueur est refusee (voir
     * PartieService.creerPartie), et le resultat de chaque partie
     * terminee est envoye au Service Joueur (historique + classement).
     */
    @Column(nullable = false)
    private Long joueurId;

    /**
     * Le mot mystere a deviner. Stocke en majuscules.
     * NB : on ne le renvoie volontairement pas au client tant que
     * la partie est en cours (voir le DTO de reponse).
     */
    @Column(nullable = false)
    private String motMystere;

    /**
     * Longueur du mot (nombre de lettres).
     */
    @Column(nullable = false)
    private int longueur;

    /**
     * Statut courant : EN_COURS, GAGNEE ou PERDUE.
     * @Enumerated(STRING) stocke le nom ("EN_COURS") plutot qu'un
     * numero, plus lisible en base.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPartie statut;

    /**
     * Nombre maximal d'essais autorises.
     */
    @Column(nullable = false)
    private int nbEssaisMax;

    /**
     * Nombre d'essais deja utilises.
     */
    @Column(nullable = false)
    private int nbEssaisUtilises;

    @Column(nullable = false)
    private LocalDateTime dateDebut;

    private LocalDateTime dateFin;

    /**
     * Liste des propositions faites pendant cette partie.
     * Meme principe que Joueur/Resultat : @OneToMany cote "un",
     * @JsonManagedReference pour eviter la boucle JSON.
     */
    @OneToMany(mappedBy = "partie", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Proposition> propositions = new ArrayList<>();

    public Partie() {
    }

    /**
     * Cree une nouvelle partie EN_COURS pour un joueur et un mot.
     */
    public Partie(Long joueurId, String motMystere, int nbEssaisMax) {
        this.joueurId = joueurId;
        this.motMystere = motMystere.toUpperCase();
        this.longueur = this.motMystere.length();
        this.nbEssaisMax = nbEssaisMax;
        this.nbEssaisUtilises = 0;
        this.statut = StatutPartie.EN_COURS;
        this.dateDebut = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJoueurId() {
        return joueurId;
    }

    public void setJoueurId(Long joueurId) {
        this.joueurId = joueurId;
    }

    public String getMotMystere() {
        return motMystere;
    }

    public void setMotMystere(String motMystere) {
        this.motMystere = motMystere;
    }

    public int getLongueur() {
        return longueur;
    }

    public void setLongueur(int longueur) {
        this.longueur = longueur;
    }

    public StatutPartie getStatut() {
        return statut;
    }

    public void setStatut(StatutPartie statut) {
        this.statut = statut;
    }

    public int getNbEssaisMax() {
        return nbEssaisMax;
    }

    public void setNbEssaisMax(int nbEssaisMax) {
        this.nbEssaisMax = nbEssaisMax;
    }

    public int getNbEssaisUtilises() {
        return nbEssaisUtilises;
    }

    public void setNbEssaisUtilises(int nbEssaisUtilises) {
        this.nbEssaisUtilises = nbEssaisUtilises;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public List<Proposition> getPropositions() {
        return propositions;
    }

    public void setPropositions(List<Proposition> propositions) {
        this.propositions = propositions;
    }
}
