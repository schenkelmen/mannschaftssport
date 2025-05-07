package de.hsos.swa.gateway;

import de.hsos.swa.entity.Person;
import de.hsos.swa.entity.PersonVerwalter;
import de.hsos.swa.entity.Team;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ApplicationScoped
public class PersonRepository implements PersonVerwalter {
    private static final Logger LOG = Logger.getLogger(PersonRepository.class);
    private ConcurrentMap<String, Person> personen = new ConcurrentHashMap<>();


    @Override
    @Retry(maxRetries = 3, delay = 200)
    @Timeout(2000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    public String anlegenNeuPerson(String name) {
        String id = UUID.randomUUID().toString();
        Person person = new Person(id, name);
        this.personen.put(id, person);
        LOG.info("Neue Person angelegt mit ID: " + id);
        return id;
    }

    @Override
    @Retry(maxRetries = 3, delay = 200)
    @Timeout(1500)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    public boolean entfernenPerson(String id) {
        try {
            Person remove = this.personen.remove(id);
            if(remove == null) {
                LOG.warn("Person zum Entfernen nicht gefunden: " + id);
                return false;
            }
            LOG.info("Person erfolgreich entfernt: " + id);
            return true;
        } catch (Exception e) {
            LOG.error("Fehler beim Entfernen der Person mit ID: " + id, e);
            return false;
        }
    }

    @Override
    @Retry(maxRetries = 2, delay = 300)
    @Timeout(1000)
    @CircuitBreaker(requestVolumeThreshold = 3, failureRatio = 0.5, delay = 4000)
    public Optional<Person> aendereName(String id, String neuerName) {
        try {
            Optional<Person> personOptional = Optional.ofNullable(this.personen.get(id));
            if(personOptional.isPresent()) {
                Person person = personOptional.get();
                person.setName(neuerName);
                LOG.info("Name geändert für Person ID: " + id);
            }
            return personOptional;
        } catch (IllegalArgumentException ie) {
            LOG.error("Fehler beim Ändern dedes Namen für Person: " + id, ie);
            throw new IllegalArgumentException("Person not updated.", ie);
        }
    }

    @Retry(maxRetries = 3, delay = 200)
    @Timeout(2000)
    @CircuitBreaker(requestVolumeThreshold = 5, failureRatio = 0.5, delay = 4000)
    public Optional<Person> findePersonMitId(String id) {
        Person person = this.personen.get(id);
        if (person == null) {
            LOG.warn("Person nicht gefunden bei ID: " + id);
        } else {
            LOG.info("Person gefunden bei ID: " + id);
        }
        return Optional.ofNullable(person);
    }

    @Override
    @Retry(maxRetries = 3, delay = 200)
    @Timeout(2000)
    @CircuitBreaker(requestVolumeThreshold = 5, failureRatio = 0.5, delay = 4000)
    public Collection<Person> allePersonen() {
        LOG.info("Alle Mocktails werden abgefragt.");
        return Collections.unmodifiableCollection(this.personen.values());
    }
}
