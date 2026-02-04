package com.back.demo;

import com.back.demo.dto.TaskRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Integración: API Tasks con JWT y PostgreSQL")
@AutoConfigureMockMvc
class TaskControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    void login() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "username", "user",
                "password", "user123"
        ));
        ResultActions loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"));
        String response = loginResult.andReturn().getResponse().getContentAsString();
        jwtToken = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    @DisplayName("GET /api/v1/tasks sin token devuelve 401")
    void getTasks_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/tasks").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/tasks con token devuelve lista (vacía o con datos)")
    void getTasks_withToken_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/tasks")
                        .header("Authorization", "Bearer " + jwtToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("POST /api/v1/tasks crea tarea y GET por id la devuelve")
    void createTask_andGetById_returnsTask() throws Exception {
        TaskRequest request = TaskRequest.builder()
                .title("Tarea integración")
                .description("Descripción de prueba")
                .completed(false)
                .build();
        String createBody = objectMapper.writeValueAsString(request);

        ResultActions createResult = mockMvc.perform(post("/api/v1/tasks")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Tarea integración"))
                .andExpect(jsonPath("$.completed").value(false));

        String createResponse = createResult.andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(get("/api/v1/tasks/" + id)
                        .header("Authorization", "Bearer " + jwtToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Tarea integración"));
    }

    @Test
    @DisplayName("PUT /api/v1/tasks/{id} actualiza tarea")
    void updateTask_returns200() throws Exception {
        TaskRequest createReq = TaskRequest.builder()
                .title("Original")
                .completed(false)
                .build();
        ResultActions createRes = mockMvc.perform(post("/api/v1/tasks")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated());
        Long id = objectMapper.readTree(createRes.andReturn().getResponse().getContentAsString()).get("id").asLong();

        TaskRequest updateReq = TaskRequest.builder()
                .title("Actualizada")
                .description("Nueva desc")
                .completed(true)
                .build();
        mockMvc.perform(put("/api/v1/tasks/" + id)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Actualizada"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    @DisplayName("DELETE /api/v1/tasks/{id} elimina y GET devuelve 404")
    void deleteTask_thenGet_returns404() throws Exception {
        TaskRequest createReq = TaskRequest.builder()
                .title("Para borrar")
                .completed(false)
                .build();
        ResultActions createRes = mockMvc.perform(post("/api/v1/tasks")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated());
        Long id = objectMapper.readTree(createRes.andReturn().getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/api/v1/tasks/" + id)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/tasks/" + id)
                        .header("Authorization", "Bearer " + jwtToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Login con admin devuelve token")
    void loginAdmin_returnsToken() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "username", "admin",
                "password", "admin123"
        ));
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("admin"));
    }
}
