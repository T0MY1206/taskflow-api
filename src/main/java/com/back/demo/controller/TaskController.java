package com.back.demo.controller;

import com.back.demo.dto.PagedResponse;
import com.back.demo.dto.TaskFilter;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "CRUD de tareas")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of("id", "title", "description", "completed", "createdAt", "updatedAt");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MIN_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    private final TaskService taskService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Listar tareas",
            description = "Devuelve una página de tareas con paginación, filtros y ordenación. Requiere JWT. " +
                    "Filtros combinables: title (contiene en título), description (contiene), completed (true/false), " +
                    "createdAtAfter/Before y updatedAtAfter/Before (ISO-8601). Orden: sort=campo,asc|desc."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de tareas (content, totalElements, totalPages, size, number, first, last)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PagedResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado (falta o token inválido)"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public PagedResponse<TaskResponse> findAll(
            @Parameter(description = "Número de página (0-based)", schema = @Schema(defaultValue = "0")) @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página (mín. 10, máx. 100)", schema = @Schema(defaultValue = "20")) @RequestParam(required = false, defaultValue = "20") int size,
            @Parameter(description = "Orden: campo,dirección (ej. createdAt,desc). Campos: id, title, description, completed, createdAt, updatedAt", schema = @Schema(defaultValue = "createdAt,desc")) @RequestParam(required = false, defaultValue = "createdAt,desc") String sort,
            @Parameter(description = "Texto a buscar en el título (contiene)") @RequestParam(required = false) String title,
            @Parameter(description = "Texto a buscar en la descripción (contiene)") @RequestParam(required = false) String description,
            @Parameter(description = "Filtrar por completada: true o false") @RequestParam(required = false) Boolean completed,
            @Parameter(description = "Creadas después de (ISO-8601)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAtAfter,
            @Parameter(description = "Creadas antes de (ISO-8601)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAtBefore,
            @Parameter(description = "Actualizadas después de (ISO-8601)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updatedAtAfter,
            @Parameter(description = "Actualizadas antes de (ISO-8601)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updatedAtBefore) {
        TaskFilter filter = TaskFilter.builder()
                .title(title)
                .description(description)
                .completed(completed)
                .createdAtAfter(createdAtAfter)
                .createdAtBefore(createdAtBefore)
                .updatedAtAfter(updatedAtAfter)
                .updatedAtBefore(updatedAtBefore)
                .build();
        int safeSize = Math.min(MAX_PAGE_SIZE, Math.max(MIN_PAGE_SIZE, size));
        int safePage = Math.max(0, page);
        Sort resolvedSort = parseSort(sort);
        Pageable pageable = PageRequest.of(safePage, safeSize, resolvedSort);
        return taskService.findAll(pageable, filter);
    }

    /**
     * Parsea el parámetro sort (ej. "createdAt,desc" o "title,asc") y solo permite propiedades de Task.
     */
    private Sort parseSort(String sortParam) {
        if (sortParam == null || sortParam.isBlank()) {
            return DEFAULT_SORT;
        }
        String[] parts = sortParam.split(",", 2);
        String property = parts[0].trim();
        if (!ALLOWED_SORT_PROPERTIES.contains(property)) {
            return DEFAULT_SORT;
        }
        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())) {
            direction = Sort.Direction.DESC;
        }
        return Sort.by(direction, property);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtener tarea por ID", description = "Devuelve una tarea por su ID. Requiere JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarea encontrada", content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    })
    public TaskResponse findById(
            @Parameter(description = "ID de la tarea") @PathVariable Long id) {
        return taskService.findById(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Crear tarea",
            description = "Crea una nueva tarea. Body: title (obligatorio), description (opcional), completed (opcional, default false). Requiere JWT."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tarea creada", content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validación fallida (ej. título vacío)"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public TaskResponse create(@Valid @RequestBody TaskRequest request) {
        return taskService.create(request);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Actualizar tarea",
            description = "Reemplaza la tarea con el ID dado. Body completo: title, description, completed. Requiere JWT."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarea actualizada", content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validación fallida"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    })
    public TaskResponse update(
            @Parameter(description = "ID de la tarea") @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {
        return taskService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Borrar tarea", description = "Elimina la tarea con el ID dado. No devuelve cuerpo. Requiere JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tarea eliminada"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    })
    public void delete(
            @Parameter(description = "ID de la tarea") @PathVariable Long id) {
        taskService.deleteById(id);
    }
}
