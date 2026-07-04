package fr.dauphine.miage.motus.partie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Point d'entree du microservice Partie (Service Partie).
 *
 * C'est le coeur du jeu Motus :
 *  - il cree et gere les parties ;
 *  - il calcule la reponse a chaque proposition (lettres bien
 *    placees, mal placees, absentes) ;
 *  - il communique avec les autres microservices via REST :
 *      * Service Dico   : tirer le mot mystere, valider un mot ;
 *      * Service Joueur : enregistrer le resultat d'une partie.
 */
@SpringBootApplication
public class ServicePartieApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServicePartieApplication.class, args);
    }

    /**
     * Declare un bean RestTemplate, reutilisable dans toute
     * l'application.
     *
     * RestTemplate est l'outil de Spring pour envoyer des requetes
     * HTTP a d'autres services. C'est la "communication directe
     * HTTP/REST" entre microservices vue en cours.
     *
     * En le declarant ici comme @Bean, on peut l'injecter par
     * constructeur dans nos classes (voir DicoClient et JoueurClient).
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
