package com.back.demo.mapper;

import com.back.demo.dto.TaskRequest;
import com.back.demo.dto.TaskResponse;
import com.back.demo.model.Task;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskResponse toResponse(Task task);

    List<TaskResponse> toResponseList(List<Task> tasks);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Task toEntity(TaskRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Task task, TaskRequest request);
}
