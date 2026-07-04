package fr.dauphine.miage.motus.joueur;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entree du microservice Joueur (Service Joueur).
 *
 * Ce microservice gere :
 *  - l'enregistrement et la consultation des joueurs ;
 *  - l'historique des resultats de leurs parties ;
 *  - le classement global des joueurs par score.
 *
 * L'annotation @SpringBootApplication active la configuration
 * automatique de Spring Boot (serveur web, JPA, etc.).
 */
@SpringBootApplication
public class ServiceJoueurApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceJoueurApplication.class, args);
    }
}
