package com.back.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Filtros opcionales para listar tareas")
public class TaskFilter {

    @Schema(description = "Texto a buscar en el título (contiene, sin distinguir mayúsculas)")
    private String title;

    @Schema(description = "Texto a buscar en la descripción (contiene)")
    private String description;

    @Schema(description = "Filtrar por estado: true = completadas, false = pendientes")
    private Boolean completed;

    @Schema(description = "Tareas creadas después de esta fecha/hora (ISO-8601)")
    private LocalDateTime createdAtAfter;

    @Schema(description = "Tareas creadas antes de esta fecha/hora (ISO-8601)")
    private LocalDateTime createdAtBefore;

    @Schema(description = "Tareas actualizadas después de esta fecha/hora (ISO-8601)")
    private LocalDateTime updatedAtAfter;

    @Schema(description = "Tareas actualizadas antes de esta fecha/hora (ISO-8601)")
    private LocalDateTime updatedAtBefore;
}
