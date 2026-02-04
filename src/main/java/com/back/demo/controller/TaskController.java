package com.back.demo.controller;

import com.back.demo.dto.TaskRequest;
import com.back.demo.dto.TaskResponse;
import com.back.demo.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "CRUD de tareas")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Listar todas las tareas")
    @ApiResponse(responseCode = "200", description = "Lista de tareas")
    public List<TaskResponse> findAll() {
        return taskService.findAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtener tarea por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarea encontrada"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public TaskResponse findById(
            @Parameter(description = "ID de la tarea") @PathVariable Long id) {
        return taskService.findById(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear nueva tarea")
    @ApiResponse(responseCode = "201", description = "Tarea creada")
    public TaskResponse create(@Valid @RequestBody TaskRequest request) {
        return taskService.create(request);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Actualizar tarea")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarea actualizada"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public TaskResponse update(
            @Parameter(description = "ID de la tarea") @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {
        return taskService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Borrar tarea")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tarea eliminada"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public void delete(
            @Parameter(description = "ID de la tarea") @PathVariable Long id) {
        taskService.deleteById(id);
    }
}
