package de.hsos.swa.boundary.util.dto;

import de.hsos.swa.entity.Person;
import jakarta.json.bind.annotation.JsonbCreator;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record PersonDTO(

        @Schema(description = "Eindeutige ID der Person")
        String id,
        @Schema(description = "Name der Person")
        String name
) {

    @JsonbCreator
    public PersonDTO {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID must not be null or empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be null or empty");
        }
    }

    public static PersonDTO toDTO(Person person) {
        return new PersonDTO(person.getId(), person.getName());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PersonDTO other = (PersonDTO) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
