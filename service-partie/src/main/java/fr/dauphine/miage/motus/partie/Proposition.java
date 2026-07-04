package fr.dauphine.miage.motus.partie;

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
 * Entite JPA representant une proposition (un essai) faite par le
 * joueur pendant une partie.
 *
 * Chaque proposition appartient a une partie (relation @ManyToOne).
 * Le champ "resultat" stocke la reponse du jeu sous forme de chaine,
 * par exemple "BIEN_PLACEE,MAL_PLACEE,ABSENTE,ABSENTE,BIEN_PLACEE".
 */
@Entity
@Table(name = "proposition")
public class Proposition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * La partie a laquelle appartient cette proposition.
     */
    @ManyToOne
    @JoinColumn(name = "partie_id", nullable = false)
    @JsonBackReference
    private Partie partie;

    /**
     * Le mot propose par le joueur (en majuscules).
     */
    @Column(nullable = false)
    private String motPropose;

    /**
     * Numero de l'essai (1 pour la premiere proposition, etc.).
     */
    @Column(nullable = false)
    private int numeroEssai;

    /**
     * La reponse Motus, encodee en chaine de caracteres.
     * Format : statuts separes par des virgules, un par lettre.
     * Exemple pour MAISON vs MARRON :
     *   "BIEN_PLACEE,MAL_PLACEE,ABSENTE,ABSENTE,BIEN_PLACEE,BIEN_PLACEE"
     */
    @Column(nullable = false, length = 500)
    private String resultat;

    @Column(nullable = false)
    private LocalDateTime dateProposition;

    public Proposition() {
    }

    public Proposition(String motPropose, int numeroEssai, String resultat) {
        this.motPropose = motPropose.toUpperCase();
        this.numeroEssai = numeroEssai;
        this.resultat = resultat;
        this.dateProposition = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Partie getPartie() {
        return partie;
    }

    public void setPartie(Partie partie) {
        this.partie = partie;
    }

    public String getMotPropose() {
        return motPropose;
    }

    public void setMotPropose(String motPropose) {
        this.motPropose = motPropose;
    }

    public int getNumeroEssai() {
        return numeroEssai;
    }

    public void setNumeroEssai(int numeroEssai) {
        this.numeroEssai = numeroEssai;
    }

    public String getResultat() {
        return resultat;
    }

    public void setResultat(String resultat) {
        this.resultat = resultat;
    }

    public LocalDateTime getDateProposition() {
        return dateProposition;
    }

    public void setDateProposition(LocalDateTime dateProposition) {
        this.dateProposition = dateProposition;
    }
}
