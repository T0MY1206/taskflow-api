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
        List<TaskResponse> content = taskMapper.toResponseList(page.getContent());
        return PagedResponse.<TaskResponse>builder()
                .content(content)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .size(page.getSize())
                .number(page.getNumber())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
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
        if (request.getCompleted() != null) {
            task.setCompleted(request.getCompleted());
        } else {
            task.setCompleted(false);
        }
        task = taskRepository.save(task);
        return taskMapper.toResponse(task);
    }

    @Transactional
    public TaskResponse update(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea", id));
        taskMapper.updateEntity(task, request);
        if (request.getCompleted() != null) {
            task.setCompleted(request.getCompleted());
        }
        task = taskRepository.save(task);
        return taskMapper.toResponse(task);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tarea", id);
        }
        taskRepository.deleteById(id);
    }
}
