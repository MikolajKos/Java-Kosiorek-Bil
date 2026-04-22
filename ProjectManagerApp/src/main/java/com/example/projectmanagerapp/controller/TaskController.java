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

    // Wstrzykujemy tylko jeden serwis, zamiast trzech repozytoriów!
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
            @Parameter(description = "Data required to create a new task")
            @RequestBody CreateTaskRequest request) {

        // Cała logika tworzenia przeszła do serwisu
        Task savedTask = taskService.createTask(request);

        if (savedTask == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }
}