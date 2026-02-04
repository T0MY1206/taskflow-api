package com.back.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta con los datos de una tarea")
public class TaskResponse {

    @Schema(description = "Identificador único de la tarea")
    private Long id;

    @Schema(description = "Título de la tarea")
    private String title;

    @Schema(description = "Descripción de la tarea")
    private String description;

    @Schema(description = "Indica si la tarea está completada")
    private Boolean completed;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización")
    private LocalDateTime updatedAt;
}
