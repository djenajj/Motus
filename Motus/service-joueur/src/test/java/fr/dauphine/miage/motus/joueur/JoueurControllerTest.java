package fr.dauphine.miage.motus.joueur;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests d'integration du Service Joueur.
 *
 * @SpringBootTest demarre l'application complete (avec la base H2
 * et les joueurs de test pre-charges).
 * MockMvc envoie des requetes HTTP simulees aux controleurs.
 *
 * Ces tests servent aussi de "client de demonstration" du service.
 */
@SpringBootTest
@AutoConfigureMockMvc
class JoueurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * GET /api/joueurs doit renvoyer la liste des joueurs (200 OK).
     */
    @Test
    void listerJoueurs_renvoieLaListe() throws Exception {
        mockMvc.perform(get("/api/joueurs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    /**
     * POST /api/joueurs doit creer un joueur et renvoyer 201 CREATED.
     */
    @Test
    void enregistrerJoueur_renvoie201() throws Exception {
        String corpsJson = "{\"pseudo\":\"david\","
                + "\"motDePasse\":\"secret\"}";

        mockMvc.perform(post("/api/joueurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpsJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pseudo").value("david"))
                .andExpect(jsonPath("$.scoreTotal").value(0));
    }

    /**
     * Enregistrer un joueur avec un pseudo deja pris doit renvoyer
     * un code 409 CONFLICT.
     */
    @Test
    void enregistrerJoueur_pseudoDejaPris_renvoie409() throws Exception {
        String corpsJson = "{\"pseudo\":\"bianca\",\"motDePasse\":\"secret\"}";

        // Premiere inscription : doit reussir.
        mockMvc.perform(post("/api/joueurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpsJson))
                .andExpect(status().isCreated());

        // Deuxieme inscription avec le meme pseudo : refusee.
        mockMvc.perform(post("/api/joueurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpsJson))
                .andExpect(status().isConflict());
    }

    /**
     * GET /api/joueurs/{id} avec un id inexistant doit renvoyer 404.
     */
    @Test
    void getJoueur_idInexistant_renvoie404() throws Exception {
        mockMvc.perform(get("/api/joueurs/9999"))
                .andExpect(status().isNotFound());
    }

    /**
     * GET /api/joueurs/classement doit renvoyer une liste triee.
     */
    @Test
    void classement_renvoieUneListe() throws Exception {
        mockMvc.perform(get("/api/joueurs/classement"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    /**
     * POST /api/joueurs/{id}/resultats doit enregistrer un resultat
     * et renvoyer 201 CREATED.
     */
    @Test
    void ajouterResultat_renvoie201() throws Exception {
        // On cree d'abord un joueur et on recupere son id reel
        // (aucun joueur de demo n'est pre-charge au demarrage).
        String reponseInscription = mockMvc.perform(post("/api/joueurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pseudo\":\"celine\",\"motDePasse\":\"secret\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        int id = com.jayway.jsonpath.JsonPath.read(reponseInscription, "$.id");

        String corpsJson = "{\"partieId\":201,\"gagnee\":true,"
                + "\"nbEssais\":4,\"score\":0}";

        mockMvc.perform(post("/api/joueurs/" + id + "/resultats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpsJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gagnee").value(true));
    }

    /**
     * Le compte ADMIN cree au demarrage n'est pas un vrai joueur :
     * il ne doit jamais apparaitre dans le classement.
     */
    @Test
    void classement_exclutLeCompteAdmin() throws Exception {
        mockMvc.perform(get("/api/joueurs/classement"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.pseudo == 'admin')]").isEmpty());
    }
}
