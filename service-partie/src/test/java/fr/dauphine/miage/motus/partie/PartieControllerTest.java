package fr.dauphine.miage.motus.partie;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests d'integration du Service Partie.
 *
 * Les clients REST (DicoClient, JoueurClient) sont remplaces par des
 * "mocks" grace a @MockBean : on simule leurs reponses sans avoir
 * besoin que les Services Dico et Joueur tournent reellement.
 * Cela rend les tests independants et reproductibles.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PartieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Faux DicoClient : on decide nous-memes ses reponses.
    @MockBean
    private DicoClient dicoClient;

    // Faux JoueurClient : il ne fera rien (la methode est void).
    @MockBean
    private JoueurClient joueurClient;

    /**
     * Cree une partie et verifie le code 201 + le statut EN_COURS.
     */
    @Test
    void creerPartie_renvoie201() throws Exception {
        // On simule le mot mystere renvoye par le Service Dico.
        when(dicoClient.tirerMotMystere(anyInt())).thenReturn("MAISON");

        String corps = "{\"joueurId\":1,\"longueur\":6}";

        mockMvc.perform(post("/api/parties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corps))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statut").value("EN_COURS"))
                .andExpect(jsonPath("$.longueur").value(6));
    }

    /**
     * Joue une proposition gagnante et verifie que la partie
     * passe au statut GAGNEE.
     */
    @Test
    void proposerMot_gagnant_partieGagnee() throws Exception {
        // Le mot mystere sera MAISON, et MAISON existe au dico.
        when(dicoClient.tirerMotMystere(anyInt())).thenReturn("MAISON");
        when(dicoClient.motExiste(anyString())).thenReturn(true);

        // 1. Creer la partie (id genere : 1 car base vide au demarrage).
        String corpsPartie = "{\"joueurId\":1,\"longueur\":6}";
        mockMvc.perform(post("/api/parties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpsPartie))
                .andExpect(status().isCreated());

        // 2. Proposer le bon mot.
        String corpsProp = "{\"motPropose\":\"MAISON\"}";
        mockMvc.perform(post("/api/parties/1/propositions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpsProp))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("GAGNEE"));
    }

    /**
     * Proposer un mot de mauvaise longueur doit renvoyer 400.
     */
    @Test
    void proposerMot_mauvaiseLongueur_renvoie400() throws Exception {
        when(dicoClient.tirerMotMystere(anyInt())).thenReturn("MAISON");
        when(dicoClient.motExiste(anyString())).thenReturn(true);

        mockMvc.perform(post("/api/parties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"joueurId\":1,\"longueur\":6}"))
                .andExpect(status().isCreated());

        // "CHAT" fait 4 lettres, le mot mystere en fait 6 -> 400.
        mockMvc.perform(post("/api/parties/1/propositions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"motPropose\":\"CHAT\"}"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Proposer un mot qui n'existe pas dans le dictionnaire doit
     * etre refuse (400), sans meme calculer de reponse Motus.
     * Ce test verifie que le Service Dico est bien consulte
     * (dicoClient.motExiste) avant d'accepter une proposition.
     */
    @Test
    void proposerMot_absentDuDictionnaire_renvoie400() throws Exception {
        when(dicoClient.tirerMotMystere(anyInt())).thenReturn("MAISON");
        when(dicoClient.motExiste("ZAZOUX")).thenReturn(false);

        mockMvc.perform(post("/api/parties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"joueurId\":1,\"longueur\":6}"))
                .andExpect(status().isCreated());

        // "ZAZOUX" a la bonne longueur (6) mais n'existe pas au dico -> 400.
        mockMvc.perform(post("/api/parties/1/propositions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"motPropose\":\"ZAZOUX\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erreur").value("Coup invalide"));
    }

    /**
     * GET /api/parties (liste d'administration) doit etre refuse
     * (403) a un joueur qui n'a pas le role ADMIN.
     */
    @Test
    void listerParties_nonAdmin_renvoie403() throws Exception {
        when(joueurClient.estAdmin(42L)).thenReturn(false);

        mockMvc.perform(get("/api/parties").param("adminId", "42"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.erreur").value("Acces refuse"));
    }

    /**
     * GET /api/parties doit fonctionner pour un joueur ayant le
     * role ADMIN.
     */
    @Test
    void listerParties_admin_renvoie200() throws Exception {
        when(joueurClient.estAdmin(1L)).thenReturn(true);

        mockMvc.perform(get("/api/parties").param("adminId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    /**
     * Creer une partie sans joueurId doit etre refuse (400) : un
     * compte est desormais obligatoire pour jouer.
     */
    @Test
    void creerPartie_sansJoueurId_renvoie400() throws Exception {
        when(dicoClient.tirerMotMystere(anyInt())).thenReturn("MAISON");

        mockMvc.perform(post("/api/parties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"longueur\":6}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erreur").value("Coup invalide"));
    }
}
