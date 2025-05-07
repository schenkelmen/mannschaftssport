package de.hsos.swa.entity;

import java.util.Collection;
import java.util.Optional;

public interface PersonVerwalter {
    String anlegenNeuPerson(String name);
    boolean entfernenPerson(String id);
    Optional<Person> findePersonMitId(String id);
    Collection<Person> allePersonen();
}
