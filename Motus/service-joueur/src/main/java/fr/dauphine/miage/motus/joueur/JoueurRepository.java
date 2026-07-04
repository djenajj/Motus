package fr.dauphine.miage.motus.joueur;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository Spring Data JPA pour l'entite Joueur.
 *
 * Comme pour le Service Dico, l'heritage de JpaRepository fournit
 * gratuitement toutes les operations CRUD (save, findById, findAll,
 * delete...).
 *
 * Parametres generiques : <Joueur, Long> = entite geree + type de
 * la cle primaire.
 */
@Repository
public interface JoueurRepository extends JpaRepository<Joueur, Long> {

    /**
     * Retrouve un joueur par son pseudo.
     * Renvoie un Optional vide si aucun joueur ne correspond.
     */
    Optional<Joueur> findByPseudo(String pseudo);

    /**
     * Indique si un pseudo est deja pris.
     * Utile pour refuser les doublons a l'inscription.
     */
    boolean existsByPseudo(String pseudo);

    /**
     * Renvoie tous les joueurs tries par score decroissant.
     * C'est exactement le classement du jeu : Spring Data genere
     * SELECT * FROM joueur ORDER BY score_total DESC
     */
    List<Joueur> findAllByOrderByScoreTotalDesc();
}
