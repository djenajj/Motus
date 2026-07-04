package fr.dauphine.miage.motus.joueur;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository Spring Data JPA pour l'entite Resultat.
 *
 * Permet d'acceder a l'historique des parties terminees.
 */
@Repository
public interface ResultatRepository extends JpaRepository<Resultat, Long> {

    /**
     * Retrouve tous les resultats d'un joueur, du plus recent
     * au plus ancien.
     * Spring Data genere la requete a partir du nom de la methode :
     * SELECT * FROM resultat WHERE joueur_id = ?
     *   ORDER BY date_partie DESC
     */
    List<Resultat> findByJoueurOrderByDatePartieDesc(Joueur joueur);

    /**
     * Compte le nombre de parties gagnees par un joueur.
     * Spring Data genere :
     * SELECT COUNT(*) FROM resultat
     *   WHERE joueur_id = ? AND gagnee = true
     */
    long countByJoueurAndGagneeTrue(Joueur joueur);
}
