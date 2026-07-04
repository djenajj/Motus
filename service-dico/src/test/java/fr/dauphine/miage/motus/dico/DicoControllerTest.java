package fr.dauphine.miage.motus.dico;

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
 * Tests d'integration du Service Dico.
 *
 * @SpringBootTest demarre l'application complete (avec la base H2
 * et le dictionnaire pre-charge).
 * MockMvc permet d'envoyer des requetes HTTP simulees aux controleurs
 * et de verifier les reponses, sans avoir besoin d'un vrai serveur.
 *
 * Ces tests servent aussi de "client de demonstration" du service,
 * comme l'autorise l'enonce du projet.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Verifie que GET /api/mots/aleatoire?longueur=6 renvoie
     * bien un mot de 6 lettres.
     */
    @Test
    void motAleatoire_renvoieUnMotDeLaBonneLongueur() throws Exception {
        mockMvc.perform(get("/api/mots/aleatoire").param("longueur", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.longueur").value(6));
    }

    /**
     * Verifie qu'un mot present dans le dictionnaire est bien
     * reconnu comme existant.
     */
    @Test
    void verifierExistence_motConnu_renvoieVrai() throws Exception {
        mockMvc.perform(get("/api/mots/existe").param("valeur", "maison"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.existe").value(true));
    }

    /**
     * Verifie qu'un mot absent du dictionnaire est bien
     * reconnu comme inexistant.
     */
    @Test
    void verifierExistence_motInconnu_renvoieFaux() throws Exception {
        mockMvc.perform(get("/api/mots/existe").param("valeur", "ZZZZZZ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.existe").value(false));
    }

    /**
     * Verifie qu'on peut ajouter un mot via POST /api/mots
     * et que le service renvoie un code 201 CREATED.
     *
     * "PARAPLUIE" (9 lettres) est utilise plutot qu'un mot de 5-8
     * lettres : le pool de mots jouables (mots-jouables.txt) ne
     * couvre que ces longueurs, donc un mot de 9 lettres est garanti
     * absent au demarrage (pas de collision avec les donnees d'amorcage).
     */
    @Test
    void ajouterMot_renvoie201() throws Exception {
        String corpsJson = "{\"valeur\":\"PARAPLUIE\",\"langue\":\"FR\"}";

        mockMvc.perform(post("/api/mots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpsJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valeur").value("PARAPLUIE"))
                .andExpect(jsonPath("$.longueur").value(9));
    }

    /**
     * Verifie qu'une demande de mot d'une longueur inexistante
     * renvoie bien un code 404 NOT FOUND.
     */
    @Test
    void motAleatoire_longueurInexistante_renvoie404() throws Exception {
        mockMvc.perform(get("/api/mots/aleatoire").param("longueur", "20"))
                .andExpect(status().isNotFound());
    }

    /**
     * Verifie qu'une suite de lettres qui n'est pas un mot francais
     * (ex : "ZZZZZ") est rejetee avec un code 400 BAD REQUEST.
     */
    @Test
    void ajouterMot_pasUnMotFrancais_renvoie400() throws Exception {
        String corpsJson = "{\"valeur\":\"ZZZZZ\",\"langue\":\"FR\"}";

        mockMvc.perform(post("/api/mots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpsJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erreur").value("Mot invalide"));
    }

    /**
     * Verifie qu'un mot deja present dans le dictionnaire est
     * rejete avec un code 400 BAD REQUEST.
     */
    @Test
    void ajouterMot_dejaPresent_renvoie400() throws Exception {
        String corpsJson = "{\"valeur\":\"MAISON\",\"langue\":\"FR\"}";

        mockMvc.perform(post("/api/mots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpsJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erreur").value("Mot invalide"));
    }
}
