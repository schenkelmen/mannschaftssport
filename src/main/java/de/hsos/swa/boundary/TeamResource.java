package de.hsos.swa.boundary;

import de.hsos.swa.boundary.util.dto.NameDTO;
import de.hsos.swa.boundary.util.dto.CategoryDTO;
import de.hsos.swa.boundary.util.dto.ManagerDTO;
import de.hsos.swa.boundary.util.dto.PlayersDTO;
import de.hsos.swa.boundary.util.dto.TeamDTO;
import de.hsos.swa.boundary.util.dto.TeamIdDTO;
import de.hsos.swa.boundary.util.dto.TeamNeuDTO;
import de.hsos.swa.control.PersonService;
import de.hsos.swa.entity.Person;
import de.hsos.swa.entity.TeamVerwalter;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Shutdown;
import jakarta.enterprise.event.Startup;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.logging.Logger;

import java.util.Collection;
import java.util.List;

@Path("teams")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
@OpenAPIDefinition(
        info = @Info(
                title = "Teams API",
                version = "1.0",
                description = "REST-API zur Verwaltung aller Teams"
        )
)
public class TeamResource {

    private static final Logger LOG = Logger.getLogger(TeamResource.class);

    @Inject
    TeamVerwalter teamVerwalter;
    @Inject
    PersonService personService;

    @ConfigProperty(name = "defaultTeamSize", defaultValue = "0")
    int defaultTeamSize;

    void onStart(@Observes Startup event) {
        LOG.info("Startup: Beispiel-Teams anlegen...");
        this.teamVerwalter.anlegenNeuTeam("Alpha", "Kategorie A", "mgr1", List.of("p1","p2"));
        this.teamVerwalter.anlegenNeuTeam("Beta",  "Kategorie B", "mgr2", List.of("p3","p4"));
    }

    void onStop(@Observes Shutdown event) {
        LOG.info("Shutdown: Aufräumen...");
    }

    @GET
    @Retry(maxRetries = 3, delay = 500)
    @Timeout(2000)
    @Fallback(fallbackMethod = "fallbackAlleTeams")
    @Operation(summary = "Alle Teams abrufen",
            description = "Gibt eine Liste aller existierenden Teams zurück.")
    @APIResponse(responseCode = "200",
            description = "Teams erfolgreich abgerufen",
            content = @Content(schema = @Schema(implementation = TeamDTO.class)))
    public Collection<TeamDTO> abfragenAlleTeams() {
        LOG.info("GET /teams");
        return this.teamVerwalter.alleTeams()
                .stream()
                .map(TeamDTO::toDto)
                .toList();
    }

    public Collection<TeamDTO> fallbackAlleTeams() {
        LOG.warn("Fallback: abfragenAlleTeams()");
        return List.of();
    }

    @PATCH
    @Operation(summary = "Nicht erlaubt",
            description = "PATCH auf /teams ist nicht gestattet.")
    @APIResponse(responseCode = "405", description = "Methode nicht erlaubt")
    public Response patchNotAllowed() {
        LOG.warn("PATCH /teams nicht erlaubt");
        return Response.status(Status.METHOD_NOT_ALLOWED).build();
    }

    @POST
    @CircuitBreaker(requestVolumeThreshold = 5, failureRatio = 0.5, delay = 3000)
    @Timeout(3000)
    @Fallback(fallbackMethod = "fallbackNeuesTeamAnlegen")
    @Operation(summary = "Neues Team anlegen",
            description = "Legt ein neues Team mit den angegebenen Daten an.")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "Team erfolgreich angelegt",
                    content = @Content(schema = @Schema(implementation = TeamIdDTO.class))),
            @APIResponse(responseCode = "400",
                    description = "Ungültige Eingabedaten")
    })
    public Response anlegenNeuesTeam(TeamNeuDTO dto) {
        LOG.infof("POST /teams – Neuer Eintrag: %s", dto.name());
        try {
            String newId = this.teamVerwalter.anlegenNeuTeam(
                    dto.name(), dto.category(), dto.managerId(), dto.playerIds()
            );
            LOG.infof("Team angelegt mit ID: %s", newId);
            return Response
                    .status(Status.CREATED)
                    .entity(new TeamIdDTO(newId))
                    .build();
        } catch (IllegalArgumentException e) {
            LOG.error("Fehler bei Eingabe", e);
            return Response.status(Status.BAD_REQUEST).build();
        }
    }

    public Response fallbackNeuesTeamAnlegen(TeamNeuDTO dto) {
        LOG.warnf("Fallback: anlegenNeuesTeam() für %s", dto.name());
        return Response.status(Status.SERVICE_UNAVAILABLE)
                .entity("Dienst momentan nicht verfügbar, bitte später erneut versuchen.")
                .build();
    }

    @DELETE
    @Path("{id}")
    @Operation(
            summary     = "Team löschen",
            description = "Entfernt das Team mit der angegebenen ID."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description  = "Team erfolgreich gelöscht"
            ),
            @APIResponse(
                    responseCode = "404",
                    description  = "Kein Team mit dieser ID gefunden"
            )
    })
    public Response loescheTeam(@PathParam("id") String id) {
        LOG.infof("DELETE /teams/%s", id);
        boolean removed = this.teamVerwalter.entfernenTeam(id);
        if (removed) {
            return Response.noContent().build(); // 204
        } else {
            return Response.status(Status.NOT_FOUND).build(); // 404
        }
    }

    @PATCH
    @Path("{id}/name")
    @Operation(summary = "Team-Name ändern")
    public Response patchName(@PathParam("id") String id, NameDTO dto) {
        return teamVerwalter.aendereName(id, dto.name())
                .map(t -> Response.noContent().build())
                .orElseGet(() -> Response.status(Status.NOT_FOUND).build());
    }

    @PATCH
    @Path("{id}/category")
    @Operation(summary = "Team-Kategorie ändern")
    public Response patchCategory(@PathParam("id") String id, CategoryDTO dto) {
        return teamVerwalter.aendereKategorie(id, dto.category())
                .map(t -> Response.noContent().build())
                .orElseGet(() -> Response.status(Status.NOT_FOUND).build());
    }

    @PATCH
    @Path("{id}/manager")
    @Operation(summary = "Team-Manager ändern")
    public Response patchManager(@PathParam("id") String id, ManagerDTO dto) {
        return teamVerwalter.aendereManager(id, dto.managerId())
                .map(t -> Response.noContent().build())
                .orElseGet(() -> Response.status(Status.NOT_FOUND).build());
    }

    @PATCH
    @Path("{id}/players")
    @Operation(summary = "Team-Spieler-IDs ändern")
    public Response patchPlayers(@PathParam("id") String id, PlayersDTO dto) {
        // 1. Jeden neuen Spieler-Pass validieren
        try {
            dto.playerIds().forEach(personService::verifyAndAddPlayer);
        } catch (IllegalStateException e) {
            // wenn ein Pass fehlt oder ungültig ist: Bad Request
            return Response.status(Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }

        // 2. Nur wenn alle validiert sind, die Änderung durchführen
        return teamVerwalter.aendereSpieler(id, dto.playerIds())
                .map(t -> Response.noContent().build())
                .orElseGet(() -> Response.status(Status.NOT_FOUND).build());
    }



}
