package de.hsos.swa.boundary.util.dto;

import jakarta.json.bind.annotation.JsonbCreator;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;
import java.util.Objects;

public record TeamNeuDTO(
        @Schema(description = "Name des Teams")
        String name,
        @Schema(description = "Kategorie des Teams")
        String category,
        @Schema(description = "ID des Managers")
        String managerId,
        @Schema(description = "IDs der Spieler")
        List<String> playerIds)
{
    @JsonbCreator
    public TeamNeuDTO {
        Objects.requireNonNull(name);
        Objects.requireNonNull(category);
        Objects.requireNonNull(managerId);
        Objects.requireNonNull(playerIds);
    }
}
