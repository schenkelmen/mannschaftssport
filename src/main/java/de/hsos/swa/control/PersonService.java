package de.hsos.swa.control;

import de.hsos.swa.entity.Person;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.*;

@ApplicationScoped
public class PersonService {
    private final Map<String, Person> store = new LinkedHashMap<>();

    public List<Person> findAll() {
        return new ArrayList<>(store.values());
    }

    public Person find(String id) {
        return store.get(id);
    }

    public void save(Person p) {
        store.put(p.getId(), p);
    }

    public void delete(String id) {
        store.remove(id);
    }
}
