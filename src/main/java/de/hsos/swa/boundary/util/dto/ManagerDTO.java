package de.hsos.swa.boundary.util.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record ManagerDTO(@Schema(description = "ID des Managers")String managerId) { }
