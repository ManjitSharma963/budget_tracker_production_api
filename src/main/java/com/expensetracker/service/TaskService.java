package com.expensetracker.service;

import com.expensetracker.dto.TaskRequest;
import com.expensetracker.entity.Task;
import com.expensetracker.entity.User;
import com.expensetracker.repository.TaskRepository;
import com.expensetracker.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final SecurityUtil securityUtil;

    @Autowired
    public TaskService(TaskRepository taskRepository, SecurityUtil securityUtil) {
        this.taskRepository = taskRepository;
        this.securityUtil = securityUtil;
    }

    public List<Task> getAllTasks() {
        User currentUser = securityUtil.getCurrentUser();
        return taskRepository.findByUser(currentUser);
    }

    public Page<Task> getAllTasks(int page, int size, String sortBy, String sortDir) {
        User currentUser = securityUtil.getCurrentUser();
        Sort sort = sortDir != null && sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return taskRepository.findByUser(currentUser, pageable);
    }

    public Page<Task> getTasksByDate(LocalDate date, int page, int size) {
        User currentUser = securityUtil.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").ascending());
        return taskRepository.findByUserAndDate(currentUser, date, pageable);
    }

    public Optional<Task> getTaskById(Long id) {
        User currentUser = securityUtil.getCurrentUser();
        return taskRepository.findByIdAndUser(id, currentUser);
    }

    public Task createTask(TaskRequest taskRequest) {
        User currentUser = securityUtil.getCurrentUser();
        Task task = taskRequest.toTask();
        task.setUser(currentUser);
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, TaskRequest taskRequest) {
        User currentUser = securityUtil.getCurrentUser();
        Task task = taskRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Update only provided fields
        if (taskRequest.getTitle() != null) {
            task.setTitle(taskRequest.getTitle());
        }
        if (taskRequest.getSubtitle() != null) {
            task.setSubtitle(taskRequest.getSubtitle());
        }
        if (taskRequest.getDate() != null) {
            task.setDate(taskRequest.getDate());
        }
        if (taskRequest.getStartTime() != null) {
            task.setStartTime(taskRequest.getStartTime());
        }
        if (taskRequest.getEndTime() != null) {
            task.setEndTime(taskRequest.getEndTime());
        }
        if (taskRequest.getStatus() != null) {
            task.setStatus(taskRequest.getStatus());
        }

        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        User currentUser = securityUtil.getCurrentUser();
        if (!taskRepository.existsByIdAndUser(id, currentUser)) {
            throw new RuntimeException("Task not found");
        }
        taskRepository.deleteById(id);
    }
}

