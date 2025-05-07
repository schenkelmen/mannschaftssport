package de.hsos.swa.boundary.util.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;
public record PlayersDTO(@Schema(description = "IDs der Spieler")List<String> playerIds) { }
