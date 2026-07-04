package fr.dauphine.miage.motus.joueur;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entite JPA representant un joueur du jeu Motus.
 *
 * Un joueur possede une liste de resultats (les parties qu'il a
 * terminees). C'est la relation @OneToMany ci-dessous : un joueur,
 * plusieurs resultats.
 */
@Entity
@Table(name = "joueur")
public class Joueur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Pseudonyme du joueur, unique dans l'application.
     */
    @NotBlank
    @Column(nullable = false, unique = true, length = 50)
    private String pseudo;

    /**
     * Mot de passe du joueur.
     * NB : dans une vraie application il faudrait le chiffrer.
     * Ici on le garde simple pour le projet pedagogique.
     */
    @Column(nullable = false)
    private String motDePasse;

    /**
     * Date d'inscription, fixee automatiquement a la creation.
     */
    @Column(nullable = false)
    private LocalDate dateInscription;

    /**
     * Score total du joueur, recalcule a chaque nouveau resultat.
     * Sert a etablir le classement.
     */
    @Column(nullable = false)
    private int scoreTotal;

    /**
     * Role du joueur : "JOUEUR" (par defaut) ou "ADMIN".
     *
     * Les administrateurs peuvent acceder aux fonctionnalites
     * d'administration (lister/rechercher toutes les parties, cf.
     * Service Partie). Un simple champ suffit ici : le projet ne
     * met pas en oeuvre un vrai mecanisme d'authentification a base
     * de jetons (type Spring Security/JWT), non couvert par les
     * supports de cours ; le controle de role reste donc une
     * verification pragmatique cote serveur, pas une securite de
     * niveau production.
     */
    @Column(nullable = false, length = 10)
    private String role = "JOUEUR";

    /**
     * Liste des resultats de ce joueur.
     *
     * @OneToMany : un joueur a plusieurs resultats.
     * mappedBy = "joueur" : la relation est "possedee" par le champ
     *   'joueur' de la classe Resultat (c'est lui qui porte la cle
     *   etrangere en base).
     * cascade = ALL : si on enregistre/supprime un joueur, ses
     *   resultats suivent automatiquement.
     * @JsonManagedReference : evite une boucle infinie quand Spring
     *   convertit l'objet en JSON (Joueur -> Resultat -> Joueur ...).
     */
    @OneToMany(mappedBy = "joueur", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Resultat> resultats = new ArrayList<>();

    /**
     * Constructeur sans argument exige par JPA.
     */
    public Joueur() {
    }

    /**
     * Constructeur pratique pour creer un joueur.
     * La date d'inscription et le score sont initialises ici.
     */
    public Joueur(String pseudo, String motDePasse) {
        this.pseudo = pseudo;
        this.motDePasse = motDePasse;
        this.dateInscription = LocalDate.now();
        this.scoreTotal = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

    public int getScoreTotal() {
        return scoreTotal;
    }

    public void setScoreTotal(int scoreTotal) {
        this.scoreTotal = scoreTotal;
    }

    public List<Resultat> getResultats() {
        return resultats;
    }

    public void setResultats(List<Resultat> resultats) {
        this.resultats = resultats;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Joueur{id=" + id + ", pseudo='" + pseudo + "', scoreTotal="
                + scoreTotal + "}";
    }
}
