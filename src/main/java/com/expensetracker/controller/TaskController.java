package com.expensetracker.controller;

import com.expensetracker.dto.TaskRequest;
import com.expensetracker.entity.Task;
import com.expensetracker.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        Optional<Task> task = taskService.getTaskById(id);
        if (task.isPresent()) {
            return ResponseEntity.ok(task.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Task not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody TaskRequest taskRequest) {
        try {
            // Validate required fields
            if (taskRequest.getTitle() == null || taskRequest.getTitle().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Missing required fields: title");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            if (taskRequest.getDate() == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Missing required fields: date");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Validate time format if provided
            if (taskRequest.getStartTime() != null && !taskRequest.getStartTime().matches("^([0-1][0-9]|2[0-3]):[0-5][0-9]$")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Start time must be in HH:MM format (24-hour)");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            if (taskRequest.getEndTime() != null && !taskRequest.getEndTime().matches("^([0-1][0-9]|2[0-3]):[0-5][0-9]$")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "End time must be in HH:MM format (24-hour)");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            Task createdTask = taskService.createTask(taskRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error creating task");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody TaskRequest taskRequest) {
        try {
            // Validate time format if provided
            if (taskRequest.getStartTime() != null && !taskRequest.getStartTime().matches("^([0-1][0-9]|2[0-3]):[0-5][0-9]$")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Start time must be in HH:MM format (24-hour)");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            if (taskRequest.getEndTime() != null && !taskRequest.getEndTime().matches("^([0-1][0-9]|2[0-3]):[0-5][0-9]$")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "End time must be in HH:MM format (24-hour)");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            Task updatedTask = taskService.updateTask(id, taskRequest);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Task not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error updating task");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

