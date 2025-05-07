package de.hsos.swa.boundary.util.dto;

import jakarta.json.bind.annotation.JsonbCreator;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Objects;

public record PersonNeuDTO() {
    @Schema(description = "Name der neu erstellten Person")
    static String name;


    @JsonbCreator
    public PersonNeuDTO {
        Objects.requireNonNull(name);
    }

}
