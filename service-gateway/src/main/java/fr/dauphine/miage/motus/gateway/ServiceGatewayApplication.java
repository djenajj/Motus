package fr.dauphine.miage.motus.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entree de l'API Gateway.
 *
 * Ce microservice ne contient aucune logique metier : c'est une facade
 * unique (port 8080) qui recoit toutes les requetes du client web et les
 * redirige vers le bon microservice selon le chemin de l'URL. Le routage
 * est declare de maniere purement configurative dans application.yml.
 *
 *   /api/joueurs/**  -> service-joueur (8081)
 *   /api/parties/**  -> service-partie (8082)
 *   /api/mots/**     -> service-dico   (8083)
 */
@SpringBootApplication
public class ServiceGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceGatewayApplication.class, args);
    }
}
