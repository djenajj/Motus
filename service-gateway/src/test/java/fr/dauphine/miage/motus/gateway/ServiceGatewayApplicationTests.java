package fr.dauphine.miage.motus.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test d'integration minimal : verifie que le contexte Spring du
 * gateway (et donc la table de routage declaree dans application.yml)
 * se charge sans erreur au demarrage.
 */
@SpringBootTest
class ServiceGatewayApplicationTests {

    @Test
    void contextLoads() {
        // Si le contexte ne se charge pas, le test echoue.
    }
}
