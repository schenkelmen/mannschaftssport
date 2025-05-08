package de.hsos.swa.gateway;

import de.hsos.swa.entity.PersonPass;

import java.util.Optional;

public interface PersonPassClient {

    /**
     * Fragt den Pass zum gegebenen Spieler (Person) ab.
     * @param personId die ID des Spielers
     * @return Optional mit PersonPass, falls vorhanden
     */
    Optional<PersonPass> fetchPass(String personId);

    void addPass(PersonPass pass);
}
