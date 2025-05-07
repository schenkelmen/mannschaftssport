package de.hsos.swa.gateway;

import de.hsos.swa.entity.PersonPass;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class DevPersonPassClient implements PersonPassClient {

    private final Map<String, PersonPass> store = new ConcurrentHashMap<>();

    public DevPersonPassClient() {
        // Beispiel-Daten
        store.put("p1", new PersonPass("p1", true,  "2024-01-01"));
        store.put("p2", new PersonPass("p2", false, "2023-12-31"));
    }

    @Override
    public Optional<PersonPass> fetchPass(String personId) {
        return Optional.ofNullable(store.get(personId));
    }
}
