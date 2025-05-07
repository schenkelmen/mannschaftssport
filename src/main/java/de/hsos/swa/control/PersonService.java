package de.hsos.swa.control;

import de.hsos.swa.entity.PersonPass;
import de.hsos.swa.gateway.PersonPassClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PersonService {
    // Für den Stub
    @Inject
    PersonPassClient passClient;

    // Für den echten HTTP-Client (falls du willst):
    // @Inject @RestClient
    // PersonPassClient passClient;

    public void verifyPlayerPass(String playerId) {
        PersonPass pass = passClient.fetchPass(playerId)
                .orElseThrow(() -> new IllegalStateException("Kein Pass gefunden"));
        if (!pass.isValid()) {
            throw new IllegalStateException("Pass ungültig");
        }
        // Spieler jetzt ins Team aufnehmen

    }
}
