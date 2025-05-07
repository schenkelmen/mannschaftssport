package de.hsos.swa.boundary.util.dto;

import de.hsos.swa.entity.Team;
import jakarta.json.bind.annotation.JsonbCreator;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;
import java.util.Map;

import static io.quarkus.arc.ComponentsProvider.LOG;

public record TeamDTO(
        @Schema(description = "Name des Teams")
        String name,
        @Schema(description = "ID des Teams")
        String id,
        @Schema(description = "Kategorie des Teams")
        String category,
        @Schema(description = "ID des Managers")
        String managerId,
        @Schema(description = "IDs der Spieler")
        List<String> playerIds,
        @Schema(description = "Hypermedia Links im HATEOAS-Stil")
        Map<String, String> _links) {

    @JsonbCreator
    public TeamDTO {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name darf nicht null oder leer sein");
        }
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID darf nicht null oder leer sein");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Kategorie darf nicht null oder leer sein");
        }
        if (_links == null) {
            throw new IllegalArgumentException("_links darf nicht null sein");
        }
    }

    public static TeamDTO toDto(Team team) {
        String base = "/teams/" + team.getId();
        Map<String, String> links = Map.of(
                "self", base,
                "updateName", base + "/name",
                "updateCategory", base + "/category",
                "updateManager", base + "/manager",
                "updatePlayers", base + "/players",
                "delete", base
        );
        LOG.info("IM toDTO");
        TeamDTO teamDTO = new TeamDTO(team.getName(), team.getId(), team.getCategory(), team.getManagerId(), team.getPlayerIds(), links);
        LOG.info(teamDTO);
        return teamDTO;
    }

    //hashCode
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    //equals
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TeamDTO other = (TeamDTO) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }


}
