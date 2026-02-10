package com.back.demo.service;

import com.back.demo.dto.PagedResponse;
import com.back.demo.dto.TaskFilter;
import com.back.demo.dto.TaskRequest;
import com.back.demo.dto.TaskResponse;
import com.back.demo.exception.ResourceNotFoundException;
import com.back.demo.mapper.TaskMapper;
import com.back.demo.model.Task;
import com.back.demo.repository.TaskRepository;
import com.back.demo.repository.TaskSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Transactional(readOnly = true)
    public PagedResponse<TaskResponse> findAll(Pageable pageable, TaskFilter filter) {
        Page<Task> page = taskRepository.findAll(TaskSpecifications.withFilter(filter), pageable);
        return PagedResponse.from(page, taskMapper.toResponseList(page.getContent()));
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea", id));
        return taskMapper.toResponse(task);
    }

    @Transactional
    public TaskResponse create(TaskRequest request) {
        Task task = taskMapper.toEntity(request);
        task.setCompleted(Boolean.TRUE.equals(request.getCompleted()));
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse update(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea", id));
        taskMapper.updateEntity(task, request);
        if (request.getCompleted() != null) {
            task.setCompleted(request.getCompleted());
        }
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public void deleteById(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tarea", id);
        }
        taskRepository.deleteById(id);
    }
}
