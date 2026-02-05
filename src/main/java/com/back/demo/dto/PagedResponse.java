package com.back.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta paginada")
public class PagedResponse<T> {

    @Schema(description = "Lista de elementos de la página actual")
    private List<T> content;

    @Schema(description = "Número total de elementos")
    private long totalElements;

    @Schema(description = "Número total de páginas")
    private int totalPages;

    @Schema(description = "Tamaño de la página")
    private int size;

    @Schema(description = "Número de página actual (0-based)")
    private int number;

    @Schema(description = "Indica si es la primera página")
    private boolean first;

    @Schema(description = "Indica si es la última página")
    private boolean last;
}
