package com.back.demo.controller;

import com.back.demo.dto.TaskRequest;
import com.back.demo.dto.TaskResponse;
import com.back.demo.exception.ResourceNotFoundException;
import com.back.demo.security.JwtService;
import com.back.demo.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser
    @DisplayName("GET /api/v1/tasks devuelve 200 y lista")
    void findAll_returns200() throws Exception {
        TaskResponse resp = TaskResponse.builder()
                .id(1L)
                .title("Tarea")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(taskService.findAll()).thenReturn(List.of(resp));

        mockMvc.perform(get("/api/v1/tasks").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Tarea"));
        verify(taskService).findAll();
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/v1/tasks/{id} devuelve 200 cuando existe")
    void findById_whenExists_returns200() throws Exception {
        TaskResponse resp = TaskResponse.builder()
                .id(1L)
                .title("Tarea")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(taskService.findById(1L)).thenReturn(resp);

        mockMvc.perform(get("/api/v1/tasks/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Tarea"));
        verify(taskService).findById(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/v1/tasks crea tarea y devuelve 201")
    void create_returns201() throws Exception {
        TaskRequest request = TaskRequest.builder()
                .title("Nueva tarea")
                .description("Desc")
                .completed(false)
                .build();
        TaskResponse resp = TaskResponse.builder()
                .id(1L)
                .title("Nueva tarea")
                .description("Desc")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(taskService.create(any(TaskRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/v1/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Nueva tarea"));
        verify(taskService).create(any(TaskRequest.class));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/v1/tasks sin título devuelve 400")
    void create_withoutTitle_returns400() throws Exception {
        TaskRequest request = TaskRequest.builder()
                .description("Solo desc")
                .build();

        mockMvc.perform(post("/api/v1/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        verify(taskService, never()).create(any());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/v1/tasks/{id} actualiza y devuelve 200")
    void update_returns200() throws Exception {
        TaskRequest request = TaskRequest.builder()
                .title("Tarea actualizada")
                .completed(true)
                .build();
        TaskResponse resp = TaskResponse.builder()
                .id(1L)
                .title("Tarea actualizada")
                .completed(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(taskService.update(eq(1L), any(TaskRequest.class))).thenReturn(resp);

        mockMvc.perform(put("/api/v1/tasks/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Tarea actualizada"))
                .andExpect(jsonPath("$.completed").value(true));
        verify(taskService).update(eq(1L), any(TaskRequest.class));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/v1/tasks/{id} devuelve 204")
    void delete_returns204() throws Exception {
        doNothing().when(taskService).deleteById(1L);

        mockMvc.perform(delete("/api/v1/tasks/1").with(csrf()))
                .andExpect(status().isNoContent());
        verify(taskService).deleteById(1L);
    }

    @Test
    @DisplayName("Endpoints requieren autenticación")
    void endpoints_requireAuth() throws Exception {
        mockMvc.perform(get("/api/v1/tasks").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
