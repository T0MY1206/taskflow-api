package com.back.demo.service;

import com.back.demo.dto.TaskRequest;
import com.back.demo.dto.TaskResponse;
import com.back.demo.exception.ResourceNotFoundException;
import com.back.demo.mapper.TaskMapper;
import com.back.demo.model.Task;
import com.back.demo.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskResponse taskResponse;
    private TaskRequest taskRequest;

    @BeforeEach
    void setUp() {
        task = Task.builder()
                .id(1L)
                .title("Tarea de prueba")
                .description("Descripción")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        taskResponse = TaskResponse.builder()
                .id(1L)
                .title("Tarea de prueba")
                .description("Descripción")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        taskRequest = TaskRequest.builder()
                .title("Tarea de prueba")
                .description("Descripción")
                .completed(false)
                .build();
    }

    @Test
    @DisplayName("findAll devuelve lista de tareas")
    void findAll_returnsList() {
        when(taskRepository.findAll()).thenReturn(List.of(task));
        when(taskMapper.toResponseList(List.of(task))).thenReturn(List.of(taskResponse));

        List<TaskResponse> result = taskService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Tarea de prueba");
        verify(taskRepository).findAll();
    }

    @Test
    @DisplayName("findById devuelve tarea cuando existe")
    void findById_whenExists_returnsTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        TaskResponse result = taskService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Tarea de prueba");
        verify(taskRepository).findById(1L);
    }

    @Test
    @DisplayName("findById lanza ResourceNotFoundException cuando no existe")
    void findById_whenNotExists_throws() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
        verify(taskRepository).findById(999L);
    }

    @Test
    @DisplayName("create guarda y devuelve tarea")
    void create_savesAndReturnsTask() {
        when(taskMapper.toEntity(taskRequest)).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        TaskResponse result = taskService.create(taskRequest);

        assertThat(result.getTitle()).isEqualTo("Tarea de prueba");
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("update actualiza tarea existente")
    void update_whenExists_updatesTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        TaskResponse result = taskService.update(1L, taskRequest);

        assertThat(result.getId()).isEqualTo(1L);
        verify(taskMapper).updateEntity(eq(task), eq(taskRequest));
        verify(taskRepository).save(task);
    }

    @Test
    @DisplayName("update lanza ResourceNotFoundException cuando no existe")
    void update_whenNotExists_throws() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.update(999L, taskRequest))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteById elimina cuando existe")
    void deleteById_whenExists_deletes() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteById(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteById lanza ResourceNotFoundException cuando no existe")
    void deleteById_whenNotExists_throws() {
        when(taskRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> taskService.deleteById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(taskRepository, never()).deleteById(any());
    }
}
