package fr.dauphine.miage.motus.partie;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository Spring Data JPA pour l'entite Partie.
 *
 * L'heritage de JpaRepository fournit les operations CRUD.
 * On ajoute deux query methods utiles a l'administration.
 */
@Repository
public interface PartieRepository extends JpaRepository<Partie, Long> {

    /**
     * Retrouve toutes les parties d'un joueur donne.
     */
    List<Partie> findByJoueurId(Long joueurId);

    /**
     * Retrouve toutes les parties ayant un statut donne
     * (EN_COURS, GAGNEE, PERDUE).
     */
    List<Partie> findByStatut(StatutPartie statut);
}
