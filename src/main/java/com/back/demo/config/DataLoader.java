package com.back.demo.config;

import com.back.demo.model.Task;
import com.back.demo.model.User;
import com.back.demo.repository.TaskRepository;
import com.back.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        loadUsersIfEmpty();
        loadSampleTasksIfEmpty();
    }

    private void loadUsersIfEmpty() {
        if (userRepository.count() == 0) {
            userRepository.save(User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user123"))
                    .roles(Set.of("ROLE_USER"))
                    .build());
            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .roles(Set.of("ROLE_USER", "ROLE_ADMIN"))
                    .build());
        }
    }

    private void loadSampleTasksIfEmpty() {
        if (taskRepository.count() == 0) {
            LocalDateTime now = LocalDateTime.now();
            taskRepository.save(Task.builder()
                    .title("Revisar documentación del API")
                    .description("Leer OpenAPI y probar endpoints con Swagger UI")
                    .completed(false)
                    .createdAt(now)
                    .updatedAt(now)
                    .build());
            taskRepository.save(Task.builder()
                    .title("Implementar tests de integración")
                    .description("Añadir pruebas con Testcontainers y MockMvc")
                    .completed(true)
                    .createdAt(now.minusDays(2))
                    .updatedAt(now.minusDays(1))
                    .build());
            taskRepository.save(Task.builder()
                    .title("Configurar CI en GitHub Actions")
                    .description("Workflow para build y tests en push/PR")
                    .completed(true)
                    .createdAt(now.minusDays(5))
                    .updatedAt(now.minusDays(3))
                    .build());
            taskRepository.save(Task.builder()
                    .title("Desplegar en entorno de staging")
                    .description("Preparar deploy con Docker o plataforma cloud")
                    .completed(false)
                    .createdAt(now.minusDays(1))
                    .updatedAt(now.minusDays(1))
                    .build());
            taskRepository.save(Task.builder()
                    .title("Refactorizar capa de servicios")
                    .description("Extraer lógica común y mejorar manejo de errores")
                    .completed(false)
                    .createdAt(now)
                    .updatedAt(now)
                    .build());
        }
    }
}
