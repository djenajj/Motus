package fr.dauphine.miage.motus.partie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository Spring Data JPA pour l'entite Partie.
 *
 * L'heritage de JpaRepository fournit les operations CRUD utilisees
 * par le service (save, findById, findAll).
 */
@Repository
public interface PartieRepository extends JpaRepository<Partie, Long> {
}
