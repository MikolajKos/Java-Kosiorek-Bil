package com.example.projectmanagerapp.controller;

import com.example.projectmanagerapp.dto.CreateTaskRequest;
import com.example.projectmanagerapp.model.Task;
import com.example.projectmanagerapp.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Operations for managing tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "Retrieve all tasks", description = "Returns a list of all tasks from the database")
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @PostMapping
    @Operation(summary = "Create a new task", description = "Adds a new task and assigns it to a project and user")
    public ResponseEntity<Task> createTask(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data required to create a new task",
                    required = true
            )
            @RequestBody CreateTaskRequest request) {
        Task savedTask = taskService.createTask(request);
        if (savedTask == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing task", description = "Updates an existing task by ID")
    public ResponseEntity<Task> updateTask(
            @Parameter(description = "Task ID", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data required to update the task",
                    required = true
            )
            @RequestBody CreateTaskRequest request) {
        Task updated = taskService.updateTask(id, request);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task", description = "Deletes a task by ID")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "Task ID", required = true, example = "1")
            @PathVariable Long id) {
        if (!taskService.deleteTask(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
