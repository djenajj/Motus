package fr.dauphine.miage.motus.partie;

/**
 * Objets de transfert (DTO) pour les corps de requete REST.
 *
 * On les regroupe ici pour la simplicite. Ce sont de simples
 * structures de donnees qui representent le JSON envoye par le
 * client, sans logique metier.
 */
public class DTOs {

    /**
     * Corps de POST /api/parties : creer une nouvelle partie.
     * JSON attendu : { "joueurId": 1, "longueur": 7 }
     */
    public static class CreerPartieRequete {
        public Long joueurId;
        public int longueur;
    }

    /**
     * Corps de POST /api/parties/{id}/propositions : proposer un mot.
     * JSON attendu : { "motPropose": "MAISON" }
     */
    public static class ProposerMotRequete {
        public String motPropose;
    }
}
