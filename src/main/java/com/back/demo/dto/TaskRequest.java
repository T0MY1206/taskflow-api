package com.back.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos para crear o actualizar una tarea")
public class TaskRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 1, max = 255, message = "El título debe tener entre 1 y 255 caracteres")
    @Schema(description = "Título de la tarea", example = "Implementar login", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Size(max = 2000, message = "La descripción no puede superar 2000 caracteres")
    @Schema(description = "Descripción opcional de la tarea")
    private String description;

    @Schema(description = "Indica si la tarea está completada", example = "false")
    private Boolean completed;
}
