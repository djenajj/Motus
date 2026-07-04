package fr.dauphine.miage.motus.joueur;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository Spring Data JPA pour l'entite Resultat.
 *
 * Les resultats sont persistes en cascade depuis l'entite Joueur
 * (relation @OneToMany) ; ce repository fournit les operations CRUD
 * de base sur l'entite.
 */
@Repository
public interface ResultatRepository extends JpaRepository<Resultat, Long> {
}
