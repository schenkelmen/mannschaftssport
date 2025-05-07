package de.hsos.swa.boundary.util.dto;

import jakarta.json.bind.annotation.JsonbCreator;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record TeamIdDTO(@Schema(description = "Eindeutige ID des neu erstellten Pteams") String id) {
    @JsonbCreator
    public TeamIdDTO {}

}