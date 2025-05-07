package de.hsos.swa.gateway;

import de.hsos.swa.entity.Team;
import de.hsos.swa.entity.TeamVerwalter;
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
public class TeamRepository implements TeamVerwalter {
    private static final Logger LOG = Logger.getLogger(TeamRepository.class);
    private ConcurrentMap<String, Team> teams = new ConcurrentHashMap<>();

    @Override
    public String anlegenNeuTeam(String name, String category, String managerId, List<String> playerIds) {
        String id = UUID.randomUUID().toString();
        Team team = new Team(id, name, category, managerId, playerIds);
        this.teams.put(id, team);
        LOG.info("Neues Team angelegt mit ID: " + id);
        return id;
    }

    @Override
    public boolean entfernenTeam(String id) {
        try {
            Team remove = this.teams.remove(id);
            if(remove == null) {
                LOG.warn("Team zum Entfernen nicht gefunden: " + id);
                return false;
            }
            LOG.info("Team erfolgreich entfernt: " + id);
            return true;
        } catch (Exception e) {
            LOG.error("Fehler beim Entfernen des Teams mit ID: " + id, e);
            return false;
        }
    }

    @Override
    public Optional<Team> aendereName(String id, String neuerName) {
        try {
            Optional<Team> teamOptional = Optional.ofNullable(this.teams.get(id));
            if(teamOptional.isPresent()) {
                Team team = teamOptional.get();
                team.setName(neuerName);
                LOG.info("Name geändert für Team ID: " + id);
            }
            return teamOptional;
        } catch (IllegalArgumentException ie) {
            LOG.error("Fehler beim Ändern des Names für Team: " + id, ie);
            throw new IllegalArgumentException("Name not updated.", ie);
        }
    }

    @Override
    public Optional<Team> aendereKategorie(String id, String neueKategorie) {
        try {
            Optional<Team> teamOptional = Optional.ofNullable(this.teams.get(id));
            if(teamOptional.isPresent()) {
                Team team = teamOptional.get();
                team.setCategory(neueKategorie);
                LOG.info("Kategorie geändert für Team ID: " + id);
            }
            return teamOptional;
        } catch (IllegalArgumentException ie) {
            LOG.error("Fehler beim Ändern der Kategorie für Team: " + id, ie);
            throw new IllegalArgumentException("Name not updated.", ie);
        }
    }

    @Override
    public Optional<Team> aendereManager(String id, String neuerManagerId) {
        try {
            Optional<Team> teamOptional = Optional.ofNullable(this.teams.get(id));
            if(teamOptional.isPresent()) {
                Team team = teamOptional.get();
                team.setManagerId(neuerManagerId);
                LOG.info("Manager geändert für Team ID: " + id);
            }
            return teamOptional;
        } catch (IllegalArgumentException ie) {
            LOG.error("Fehler beim Ändern des Managers für Team: " + id, ie);
            throw new IllegalArgumentException("Name not updated.", ie);
        }
    }

    @Override
    public Optional<Team> aendereSpieler(String id, List<String> neuePlayerIds) {
        try {
            Optional<Team> teamOptional = Optional.ofNullable(this.teams.get(id));
            if(teamOptional.isPresent()) {
                Team team = teamOptional.get();
                team.setPlayerIds(neuePlayerIds);
                LOG.info("Spieler geändert für Team ID: " + id);
            }
            return teamOptional;
        } catch (IllegalArgumentException ie) {
            LOG.error("Fehler beim Ändern der Spieler für Team: " + id, ie);
            throw new IllegalArgumentException("Name not updated.", ie);
        }
    }

    @Override
    public Optional<Team> findeTeamMitId(String id) {
        Team team = this.teams.get(id);
        if (team == null) {
            LOG.warn("Team nicht gefunden bei ID: " + id);
        } else {
            LOG.info("Team gefunden bei ID: " + id);
        }
        return Optional.ofNullable(team);
    }

    public Optional<Team> fallbackFindeTeamMitId(String id) {
        LOG.error("Fallback: Team konnte nicht gefunden werden für ID: " + id);
        return Optional.empty();
    }

    @Override
    public Collection<Team> alleTeams() {
        LOG.info("Alle Teams werden abgefragt.");
        LOG.info(Collections.unmodifiableCollection(this.teams.values()));
        return Collections.unmodifiableCollection(this.teams.values());
    }

}
