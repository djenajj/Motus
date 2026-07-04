package fr.dauphine.miage.motus.dico;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entite JPA representant un mot du dictionnaire.
 *
 * L'annotation @Entity indique a JPA que cette classe est persistee
 * en base de donnees. Chaque instance correspond a une ligne de la
 * table "mot".
 *
 * C'est le modele a reproduire pour les autres entites du projet
 * (Joueur, Partie, Proposition, Resultat).
 */
@Entity
@Table(name = "mot")
public class Mot {

    /**
     * Identifiant unique, genere automatiquement par la base.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Le mot lui-meme, stocke en majuscules pour faciliter
     * les comparaisons (le jeu Motus est insensible a la casse).
     */
    @Column(nullable = false, unique = true, length = 30)
    private String valeur;

    /**
     * Nombre de lettres du mot. Stocke pour pouvoir filtrer
     * rapidement les mots par longueur.
     */
    @Column(nullable = false)
    private int longueur;

    /**
     * Langue du mot (ex : "FR"). Permet d'envisager plusieurs
     * dictionnaires.
     */
    @Column(nullable = false, length = 5)
    private String langue;

    /**
     * Constructeur sans argument exige par JPA.
     */
    public Mot() {
    }

    /**
     * Constructeur pratique pour creer un mot.
     * La longueur est calculee automatiquement a partir de la valeur.
     */
    public Mot(String valeur, String langue) {
        this.valeur = valeur.toUpperCase();
        this.longueur = this.valeur.length();
        this.langue = langue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur.toUpperCase();
        this.longueur = this.valeur.length();
    }

    public int getLongueur() {
        return longueur;
    }

    public void setLongueur(int longueur) {
        this.longueur = longueur;
    }

    public String getLangue() {
        return langue;
    }

    public void setLangue(String langue) {
        this.langue = langue;
    }

    @Override
    public String toString() {
        return "Mot{id=" + id + ", valeur='" + valeur + "', longueur=" + longueur
                + ", langue='" + langue + "'}";
    }
}
