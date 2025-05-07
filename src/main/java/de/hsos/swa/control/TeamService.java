package de.hsos.swa.control;

import de.hsos.swa.entity.Team;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.*;

@ApplicationScoped
public class TeamService {
    private final Map<String, Team> store = new LinkedHashMap<>();

    public List<Team> findAll() {
        return new ArrayList<>(store.values());
    }

    public Team find(String id) {
        return store.get(id);
    }

    public void save(Team t) {
        store.put(t.getId(), t);
    }

    public void delete(String id) {
        store.remove(id);
    }
}