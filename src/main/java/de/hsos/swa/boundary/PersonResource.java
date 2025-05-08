package de.hsos.swa.boundary;
import de.hsos.swa.boundary.util.dto.PersonDTO;
import de.hsos.swa.boundary.util.dto.PersonIdDTO;
import de.hsos.swa.boundary.util.dto.PersonNeuDTO;
import de.hsos.swa.control.PersonService;
import de.hsos.swa.entity.Person;
import de.hsos.swa.entity.PersonVerwalter;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Path("persons")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
public class PersonResource {

    @Inject
    PersonVerwalter personVerwalter;
    @Inject
    PersonService personService;

    @GET
    @Retry(maxRetries = 3, delay = 200)
    @Timeout(1000)
    @Fallback(fallbackMethod = "fallbackGetAllPersons")
    @Operation(summary = "Alle Personen abrufen")
    public Collection<PersonDTO> getAllPersons() {
        return personVerwalter.allePersonen().stream()
                .map(PersonDTO::toDTO)
                .toList();
    }

    public Collection<PersonDTO> fallbackGetAllPersons() {
        // z.B. leere Liste oder Cached-Werte
        return List.of();
    }

    @GET
    @Path("{id}")
    @Retry(maxRetries = 2, delay = 200)
    @Timeout(500)
    @Fallback(fallbackMethod = "fallbackGetPerson")
    @Operation(summary = "Person nach ID abrufen")
    @APIResponse(responseCode = "404", description = "Person nicht gefunden")
    public Response getPersonById(@PathParam("id") String id) {
        Optional<Person> opt = personVerwalter.findePersonMitId(id);
        return opt.map(p -> Response.ok(PersonDTO.toDTO(p)).build())
                .orElseGet(() -> Response.status(Status.NOT_FOUND).build());
    }

    public Response fallbackGetPerson(String id) {
        return Response.status(Status.SERVICE_UNAVAILABLE)
                .entity("Dienst momentan nicht verfügbar")
                .build();
    }

    @POST
    @Retry(maxRetries = 2, delay = 200)
    @Timeout(1000)
    @Fallback(fallbackMethod = "fallbackCreatePerson")
    @Operation(summary = "Neue Person anlegen")
    @RequestBody(
            required = true,
            description = "Payload mit Name der neuen Person",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = PersonNeuDTO.class)
            )
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Person erfolgreich angelegt",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = PersonIdDTO.class)
                    )
            ),
            @APIResponse(responseCode = "400", description = "Ungültige Eingabedaten"),
            @APIResponse(responseCode = "503", description = "Service nicht verfügbar")
    })
    public Response createPerson(PersonNeuDTO dto) {
        // ID wird von PersonService generiert
        String id = personVerwalter.anlegenNeuPerson(dto.name());
        personService.addNewPlayerPass(id);
        return Response.status(Status.CREATED)
                .entity(new PersonIdDTO(id))
                .build();
    }

    public Response fallbackCreatePerson(PersonNeuDTO dto) {
        return Response.status(Status.SERVICE_UNAVAILABLE)
                .entity("Person konnte nicht angelegt werden, bitte später erneut versuchen.")
                .build();
    }

    @DELETE
    @Path("{id}")
    @Retry(maxRetries = 2, delay = 200)
    @Timeout(500)
    @Fallback(fallbackMethod = "fallbackDeletePerson")
    @Operation(summary = "Person löschen")
    @APIResponse(responseCode = "404", description = "Person nicht gefunden")
    public Response deletePerson(@PathParam("id") String id) {
        boolean removed = personVerwalter.entfernenPerson(id);
        if (removed) {
            return Response.noContent().build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    public Response fallbackDeletePerson(String id) {
        return Response.status(Status.SERVICE_UNAVAILABLE)
                .entity("Dienst momentan nicht verfügbar")
                .build();
    }
}
