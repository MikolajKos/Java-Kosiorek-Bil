package com.example.projectmanagerapp.service;

import com.example.projectmanagerapp.model.Project;
import com.example.projectmanagerapp.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    private ProjectRepository projectRepository;
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        projectRepository = mock(ProjectRepository.class);
        projectService = new ProjectService(projectRepository);
    }

    @Test
    @DisplayName("Should return all projects")
    void testGetAllProjects() {
        Project p1 = new Project();
        p1.setName("P1");

        Project p2 = new Project();
        p2.setName("P2");

        when(projectRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Project> projects = projectService.getAllProjects();

        assertEquals(2, projects.size());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should create project")
    void testCreateProject() {
        Project input = new Project();
        input.setName("Name");
        input.setDescription("Desc");

        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

        Project created = projectService.createProject(input);

        assertNotNull(created);
        assertEquals("Name", created.getName());
        assertEquals("Desc", created.getDescription());
        verify(projectRepository, times(1)).save(input);
    }

    @Test
    @DisplayName("Should return null when updating missing project")
    void testUpdateProjectMissing() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        Project patch = new Project();
        patch.setName("N");
        patch.setDescription("D");

        Project updated = projectService.updateProject(1L, patch);

        assertNull(updated);
        verify(projectRepository, times(1)).findById(1L);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("Should update existing project")
    void testUpdateProject() {
        Project existing = new Project();
        existing.setName("Old");
        existing.setDescription("OldD");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

        Project patch = new Project();
        patch.setName("New");
        patch.setDescription("NewD");

        Project updated = projectService.updateProject(1L, patch);

        assertNotNull(updated);
        assertEquals("New", updated.getName());
        assertEquals("NewD", updated.getDescription());
        verify(projectRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("Should return false when deleting missing project")
    void testDeleteProjectMissing() {
        when(projectRepository.existsById(1L)).thenReturn(false);

        boolean deleted = projectService.deleteProject(1L);

        assertFalse(deleted);
        verify(projectRepository, times(1)).existsById(1L);
        verify(projectRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should delete existing project")
    void testDeleteProject() {
        when(projectRepository.existsById(1L)).thenReturn(true);

        boolean deleted = projectService.deleteProject(1L);

        assertTrue(deleted);
        verify(projectRepository, times(1)).existsById(1L);
        verify(projectRepository, times(1)).deleteById(1L);
    }
}

