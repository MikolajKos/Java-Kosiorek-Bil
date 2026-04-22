package com.example.projectmanagerapp.service;

import com.example.projectmanagerapp.model.Project;
import com.example.projectmanagerapp.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    // --- NOWE METODY ---

    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    public Project updateProject(Long id, Project updatedData) {
        Project existing = projectRepository.findById(id).orElse(null);
        if (existing == null) {
            return null; // Zwracamy null, co kontroler zamieni na 404
        }
        existing.setName(updatedData.getName());
        existing.setDescription(updatedData.getDescription());
        return projectRepository.save(existing);
    }

    public boolean deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            return false;
        }
        projectRepository.deleteById(id);
        return true;
    }
}