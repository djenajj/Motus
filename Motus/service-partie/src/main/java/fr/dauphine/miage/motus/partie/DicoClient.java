package fr.dauphine.miage.motus.partie;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Client REST vers le Service Dico.
 *
 * C'est ICI que se fait la communication inter-microservices avec
 * RestTemplate : le Service Partie appelle le Service Dico par HTTP
 * pour tirer un mot mystere et pour valider les propositions.
 *
 * L'URL de base du Service Dico est lue depuis application.properties
 * (cle "services.dico.url"), ce qui permet de la changer facilement
 * selon l'environnement (local, Docker, Kubernetes).
 */
@Component
public class DicoClient {

    private final RestTemplate restTemplate;
    private final String dicoUrl;

    public DicoClient(RestTemplate restTemplate,
                      @Value("${services.dico.url}") String dicoUrl) {
        this.restTemplate = restTemplate;
        this.dicoUrl = dicoUrl;
    }

    /**
     * Demande au Service Dico un mot mystere aleatoire de la
     * longueur voulue.
     *
     * Appelle : GET {dicoUrl}/api/mots/aleatoire?longueur=N
     *
     * @return le mot mystere (en majuscules)
     */
    public String tirerMotMystere(int longueur) {
        String url = dicoUrl + "/api/mots/aleatoire?longueur=" + longueur;

        // Le Service Dico renvoie un JSON { id, valeur, longueur, langue }.
        // On le recupere dans une Map et on extrait le champ "valeur".
        Map<?, ?> reponse = restTemplate.getForObject(url, Map.class);

        if (reponse == null || reponse.get("valeur") == null) {
            throw new CommunicationServiceException(
                    "Le Service Dico n'a pas renvoye de mot.");
        }
        return reponse.get("valeur").toString().toUpperCase();
    }

    /**
     * Demande au Service Dico si un mot existe dans le dictionnaire.
     *
     * Appelle : GET {dicoUrl}/api/mots/existe?valeur=MOT
     *
     * @return true si le mot existe, false sinon
     */
    public boolean motExiste(String valeur) {
        String url = dicoUrl + "/api/mots/existe?valeur=" + valeur.toUpperCase();

        // Le Service Dico renvoie un JSON { valeur, existe }.
        Map<?, ?> reponse = restTemplate.getForObject(url, Map.class);

        if (reponse == null || reponse.get("existe") == null) {
            return false;
        }
        return Boolean.parseBoolean(reponse.get("existe").toString());
    }
}
