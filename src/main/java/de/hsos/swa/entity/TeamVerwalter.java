package de.hsos.swa.entity;

import io.smallrye.mutiny.helpers.BlockingIterable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TeamVerwalter {
    String anlegenNeuTeam(String name, String category, String managerId, Collection<String> playerIds);

    boolean entfernenTeam(String id);

    Optional<Team> aendereName(String id, String neuerName);
    Optional<Team> aendereKategorie(String id, String neueKategorie);
    Optional<Team> aendereManager(String id, String neuerManagerId);
    Optional<Team> aendereSpieler(String id, Collection<String> neuePlayerIds);

    Optional<Team> findeTeamMitId(String id);
    Collection<Team> alleTeams();
}
