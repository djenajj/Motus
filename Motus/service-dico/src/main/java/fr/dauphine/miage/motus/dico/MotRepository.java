package fr.dauphine.miage.motus.dico;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository Spring Data JPA pour l'entite Mot.
 *
 * En heritant de JpaRepository, on obtient GRATUITEMENT toutes les
 * operations CRUD (save, findById, findAll, delete...) sans ecrire
 * la moindre requete SQL ni de classe DAO.
 *
 * Les deux parametres generiques sont :
 *  - Mot   : l'entite geree par ce repository ;
 *  - Long  : le type de la cle primaire (le champ id).
 *
 * On peut aussi declarer des "Query Methods" : Spring Data genere
 * automatiquement la requete a partir du nom de la methode.
 */
@Repository
public interface MotRepository extends JpaRepository<Mot, Long> {

    /**
     * Retrouve tous les mots d'une longueur donnee.
     * Spring Data genere : SELECT * FROM mot WHERE longueur = ?
     */
    List<Mot> findByLongueur(int longueur);

    /**
     * Retrouve un mot par sa valeur exacte (deja en majuscules).
     * Renvoie un Optional vide si le mot n'existe pas.
     */
    Optional<Mot> findByValeur(String valeur);

    /**
     * Indique si un mot existe dans le dictionnaire.
     * Spring Data genere : SELECT COUNT(*) > 0 FROM mot WHERE valeur = ?
     */
    boolean existsByValeur(String valeur);

    /**
     * Compte le nombre de mots d'une longueur donnee.
     * Utile pour tirer un mot aleatoire (voir le service).
     */
    long countByLongueur(int longueur);
}
