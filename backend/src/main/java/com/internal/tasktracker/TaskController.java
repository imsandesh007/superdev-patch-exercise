package com.internal.tasktracker;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/api/tasks")
    public ResponseEntity<?> searchTasks(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize) {

        // Normalize query input
        String query = q == null ? "" : q.trim();
        String searchTerm = "%" + query.toLowerCase() + "%";

        // Safely parse status filter without throwing 500 on invalid enum input
        String normalizedStatus = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                normalizedStatus = TaskStatus.valueOf(status.trim().toUpperCase()).name();
            } catch (IllegalArgumentException e) {
                // Return clear 400 Bad Request if invalid status is provided
                Map<String, String> errorResponse = new LinkedHashMap<>();
                errorResponse.put("error", "Invalid status value: " + status);
                return ResponseEntity.badRequest().body(errorResponse);
            }
        }

        // Sanitize and bound pagination inputs
        int safePage = Math.max(1, page);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));

        Page<Task> taskPage = taskRepository.searchTasks(
                searchTerm,
                normalizedStatus,
                PageRequest.of(safePage - 1, safePageSize)
        );

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("items", taskPage.getContent());
        response.put("total", taskPage.getTotalElements());
        response.put("page", safePage);
        response.put("pageSize", safePageSize);

        return ResponseEntity.ok(response);
    }
}
