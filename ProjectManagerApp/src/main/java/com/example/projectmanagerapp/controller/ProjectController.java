package com.example.projectmanagerapp.controller;

import com.example.projectmanagerapp.model.Project;
import com.example.projectmanagerapp.service.ProjectService;
import com.example.projectmanagerapp.repository.ProjectRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "Operations for managing projects")
public class ProjectController {

    private final ProjectService projectService;

    // Wstrzykujemy Serwis zamiast Repozytorium
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    @Operation(summary = "Retrieve all projects", description = "Returns a list of all projects from the database")
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    @PostMapping
    @Operation(summary = "Create a new project", description = "Adds a new project to the database")
    public Project createProject(
            @Parameter(description = "Project object to be created")
            @RequestBody Project project) {
        return projectService.createProject(project);
    }
}