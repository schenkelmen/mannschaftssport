package de.hsos.swa.boundary.util.dto;

import jakarta.json.bind.annotation.JsonbCreator;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record PersonIdDTO(@Schema(description = "Eindeutige ID der neu erstellten Person") String id) {
    @JsonbCreator
    public PersonIdDTO {}

}
