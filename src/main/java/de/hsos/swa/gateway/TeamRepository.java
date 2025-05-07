package de.hsos.swa.gateway;

import de.hsos.swa.entity.Team;
import de.hsos.swa.entity.TeamVerwalter;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TeamRepository implements TeamVerwalter {
    private static final Logger LOG = Logger.getLogger(TeamRepository.class);
    private ConcurrentMap<String, Team> teams = new ConcurrentHashMap<>();

    @Override
    @Retry(maxRetries = 3, delay = 200)
    @Timeout(2000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    @Fallback(fallbackMethod = "fallbackAnlegenNeuTeam")
    public String anlegenNeuTeam(String name, String category, String managerId, List<String> playerIds) {
        String id = UUID.randomUUID().toString();
        Team team = new Team(id, name, category, managerId, playerIds);
        this.teams.put(id, team);
        LOG.info("Neues Team angelegt mit ID: " + id);
        return id;
    }

    public String fallbackAnlegenNeuTeam(String name, String zutaten, String zubereitung) {
        LOG.error("Fallback: Team konnte nicht angelegt werden.");
        return "error-fallback-Team";
    }


    @Override
    @Retry(maxRetries = 3, delay = 200)
    @Timeout(1500)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    @Fallback(fallbackMethod = "fallbackEntfernen")
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

    public boolean fallbackEntfernen(String id) {
        LOG.error("Fallback: Entfernen fehlgeschlagen für Team-ID: " + id);
        return false;
    }

    @Override
    @Retry(maxRetries = 2, delay = 300)
    @Timeout(1000)
    @CircuitBreaker(requestVolumeThreshold = 3, failureRatio = 0.5, delay = 4000)
    @Fallback(fallbackMethod = "fallbackAendereName")
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

    public Optional<Team> fallbackAendereName(String id) {
        LOG.error("Fallback: Änderung des Names fehlgeschlagen für Team-ID: " + id);
        return Optional.empty();
    }

    @Override
    @Retry(maxRetries = 2, delay = 300)
    @Timeout(1000)
    @CircuitBreaker(requestVolumeThreshold = 3, failureRatio = 0.5, delay = 4000)
    @Fallback(fallbackMethod = "fallbackAendereKategorie")
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

    public Optional<Team> fallbackAendereKategorie(String id) {
        LOG.error("Fallback: Änderung der Kategorie fehlgeschlagen für Team-ID: " + id);
        return Optional.empty();
    }

    @Override
    @Retry(maxRetries = 2, delay = 300)
    @Timeout(1000)
    @CircuitBreaker(requestVolumeThreshold = 3, failureRatio = 0.5, delay = 4000)
    @Fallback(fallbackMethod = "fallbackAendereManager")
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

    public Optional<Team> fallbackAendereManager(String id) {
        LOG.error("Fallback: Änderung des Managers fehlgeschlagen für Team-ID: " + id);
        return Optional.empty();
    }

    @Override
    @Retry(maxRetries = 2, delay = 300)
    @Timeout(1000)
    @CircuitBreaker(requestVolumeThreshold = 3, failureRatio = 0.5, delay = 4000)
    @Fallback(fallbackMethod = "fallbackAendereSpieler")
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

    public Optional<Team> fallbackAendereSpieler(String id) {
        LOG.error("Fallback: Änderung der Spieler fehlgeschlagen für Team-ID: " + id);
        return Optional.empty();
    }

    @Override
    @Retry(maxRetries = 3, delay = 200)
    @Timeout(2000)
    @CircuitBreaker(requestVolumeThreshold = 5, failureRatio = 0.5, delay = 4000)
    @Fallback(fallbackMethod = "fallbackFindeTeamMitId")
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
    @Retry(maxRetries = 3, delay = 200)
    @Timeout(2000)
    @CircuitBreaker(requestVolumeThreshold = 5, failureRatio = 0.5, delay = 4000)
    @Fallback(fallbackMethod = "fallbackAlleTeams")
    public Collection<Team> alleTeams() {
        LOG.info("Alle Teams werden abgefragt.");
        return Collections.unmodifiableCollection(this.teams.values());
    }
    public Optional<Team> fallbackAlleTeams() {
        LOG.error("Fallback: Fehler beim Abfragen aller Teams.");
        return Optional.empty();
    }
}
