package de.hsos.swa.boundary.util.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record NameDTO(@Schema(description = "Name der Person") String name) { }
