package fr.dauphine.miage.motus.partie;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Client REST vers le Service Joueur.
 *
 * Quand une partie se termine (gagnee ou perdue), le Service Partie
 * envoie le resultat au Service Joueur pour qu'il l'historise et
 * mette a jour le score du joueur.
 *
 * Appelle : POST {joueurUrl}/api/joueurs/{joueurId}/resultats
 */
@Component
public class JoueurClient {

    private final RestTemplate restTemplate;
    private final String joueurUrl;

    public JoueurClient(RestTemplate restTemplate,
                        @Value("${services.joueur.url}") String joueurUrl) {
        this.restTemplate = restTemplate;
        this.joueurUrl = joueurUrl;
    }

    /**
     * Envoie le resultat d'une partie terminee au Service Joueur.
     *
     * On encapsule l'appel dans un try/catch : si le Service Joueur
     * est indisponible, la partie reste jouable et terminee cote
     * Service Partie (on ne bloque pas le jeu pour autant). On se
     * contente de tracer l'erreur. C'est une forme simple de
     * tolerance aux pannes entre microservices.
     *
     * @param joueurId identifiant du joueur
     * @param partieId identifiant de la partie
     * @param gagnee   true si gagnee, false si perdue
     * @param nbEssais nombre d'essais utilises
     */
    public void envoyerResultat(Long joueurId, Long partieId,
                                boolean gagnee, int nbEssais) {
        String url = joueurUrl + "/api/joueurs/" + joueurId + "/resultats";

        // Corps de la requete : le Service Joueur attend un JSON
        // { partieId, gagnee, nbEssais, score }. Le score est
        // recalcule cote Service Joueur, on envoie 0 par defaut.
        Map<String, Object> corps = Map.of(
                "partieId", partieId,
                "gagnee", gagnee,
                "nbEssais", nbEssais,
                "score", 0);

        try {
            restTemplate.postForObject(url, corps, Map.class);
        } catch (RestClientException e) {
            // Le Service Joueur est peut-etre arrete : on ne bloque
            // pas la partie, on signale juste le probleme.
            System.err.println(">>> Impossible d'enregistrer le resultat "
                    + "aupres du Service Joueur : " + e.getMessage());
        }
    }

    /**
     * Demande au Service Joueur si le joueur donne a le role ADMIN.
     *
     * Appelle : GET {joueurUrl}/api/joueurs/{joueurId}/est-admin
     *
     * Contrairement a envoyerResultat(), ici on ne tolere pas la
     * panne : c'est un controle d'acces, donc en cas de doute
     * (Service Joueur indisponible, joueur inconnu...) on refuse
     * l'acces plutot que de l'autoriser par defaut.
     *
     * @return true seulement si le Service Joueur confirme le role ADMIN
     */
    public boolean estAdmin(Long joueurId) {
        if (joueurId == null) {
            return false;
        }
        String url = joueurUrl + "/api/joueurs/" + joueurId + "/est-admin";
        try {
            Map<?, ?> reponse = restTemplate.getForObject(url, Map.class);
            return reponse != null && Boolean.parseBoolean(
                    String.valueOf(reponse.get("estAdmin")));
        } catch (RestClientException e) {
            return false;
        }
    }
}
