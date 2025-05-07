package de.hsos.swa.boundary.util.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record CategoryDTO(@Schema(description = "Kategorie des Teams") String category) { }
