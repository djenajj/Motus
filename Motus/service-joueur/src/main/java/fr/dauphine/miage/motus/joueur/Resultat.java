package fr.dauphine.miage.motus.joueur;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Entite JPA representant le resultat d'une partie terminee.
 *
 * Chaque resultat appartient a un joueur (relation @ManyToOne :
 * plusieurs resultats pour un meme joueur).
 *
 * Le Service Partie enverra un resultat a ce microservice chaque
 * fois qu'une partie se termine.
 */
@Entity
@Table(name = "resultat")
public class Resultat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Le joueur a qui appartient ce resultat.
     *
     * @ManyToOne : plusieurs resultats pour un joueur.
     * @JoinColumn : nom de la colonne cle etrangere en base.
     * @JsonBackReference : cote "retour" de la relation. Empeche
     *   la boucle infinie lors de la conversion en JSON.
     */
    @ManyToOne
    @JoinColumn(name = "joueur_id", nullable = false)
    @JsonBackReference
    private Joueur joueur;

    /**
     * Identifiant de la partie concernee (dans le Service Partie).
     * On stocke juste l'id : les deux microservices ont des bases
     * separees, il n'y a pas de cle etrangere entre eux.
     */
    @Column(nullable = false)
    private Long partieId;

    /**
     * Vrai si la partie a ete gagnee, faux si perdue.
     */
    @Column(nullable = false)
    private boolean gagnee;

    /**
     * Nombre d'essais utilises pendant la partie.
     */
    @Column(nullable = false)
    private int nbEssais;

    /**
     * Score obtenu pour cette partie.
     */
    @Column(nullable = false)
    private int score;

    /**
     * Date et heure de fin de la partie.
     */
    @Column(nullable = false)
    private LocalDateTime datePartie;

    /**
     * Constructeur sans argument exige par JPA.
     */
    public Resultat() {
    }

    /**
     * Constructeur pratique. La date est fixee automatiquement.
     */
    public Resultat(Long partieId, boolean gagnee, int nbEssais, int score) {
        this.partieId = partieId;
        this.gagnee = gagnee;
        this.nbEssais = nbEssais;
        this.score = score;
        this.datePartie = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Joueur getJoueur() {
        return joueur;
    }

    public void setJoueur(Joueur joueur) {
        this.joueur = joueur;
    }

    public Long getPartieId() {
        return partieId;
    }

    public void setPartieId(Long partieId) {
        this.partieId = partieId;
    }

    public boolean isGagnee() {
        return gagnee;
    }

    public void setGagnee(boolean gagnee) {
        this.gagnee = gagnee;
    }

    public int getNbEssais() {
        return nbEssais;
    }

    public void setNbEssais(int nbEssais) {
        this.nbEssais = nbEssais;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public LocalDateTime getDatePartie() {
        return datePartie;
    }

    public void setDatePartie(LocalDateTime datePartie) {
        this.datePartie = datePartie;
    }

    @Override
    public String toString() {
        return "Resultat{id=" + id + ", partieId=" + partieId + ", gagnee="
                + gagnee + ", score=" + score + "}";
    }
}
