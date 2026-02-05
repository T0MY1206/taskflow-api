package com.back.demo.repository;

import com.back.demo.dto.TaskFilter;
import com.back.demo.model.Task;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public final class TaskSpecifications {

    private TaskSpecifications() {
    }

    public static Specification<Task> withFilter(TaskFilter filter) {
        if (filter == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getTitle() != null && !filter.getTitle().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("title")),
                        "%" + filter.getTitle().toLowerCase().trim() + "%"));
            }
            if (filter.getDescription() != null && !filter.getDescription().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("description")),
                        "%" + filter.getDescription().toLowerCase().trim() + "%"));
            }
            if (filter.getCompleted() != null) {
                predicates.add(cb.equal(root.get("completed"), filter.getCompleted()));
            }
            if (filter.getCreatedAtAfter() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getCreatedAtAfter()));
            }
            if (filter.getCreatedAtBefore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.getCreatedAtBefore()));
            }
            if (filter.getUpdatedAtAfter() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("updatedAt"), filter.getUpdatedAtAfter()));
            }
            if (filter.getUpdatedAtBefore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("updatedAt"), filter.getUpdatedAtBefore()));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
