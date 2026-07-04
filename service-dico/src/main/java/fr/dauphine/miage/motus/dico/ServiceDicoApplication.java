package fr.dauphine.miage.motus.dico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entree du microservice Dictionnaire (Service Dico).
 *
 * Ce microservice gere le dictionnaire de mots du jeu Motus :
 *  - il fournit un mot mystere aleatoire d'une longueur donnee ;
 *  - il verifie qu'un mot propose existe bien dans le dictionnaire.
 *
 * L'annotation @SpringBootApplication active la configuration
 * automatique de Spring Boot (serveur web, JPA, etc.).
 */
@SpringBootApplication
public class ServiceDicoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceDicoApplication.class, args);
    }
}
