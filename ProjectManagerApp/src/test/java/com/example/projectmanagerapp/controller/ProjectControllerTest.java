package com.example.projectmanagerapp.controller;

import com.example.projectmanagerapp.model.Project;
import com.example.projectmanagerapp.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProjectControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        projectService = mock(ProjectService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new ProjectController(projectService)).build();
    }

    @Test
    @DisplayName("GET /api/projects returns projects")
    void getAllProjects() throws Exception {
        Project p1 = new Project();
        p1.setId(1L);
        p1.setName("P1");
        p1.setDescription("D1");

        Project p2 = new Project();
        p2.setId(2L);
        p2.setName("P2");
        p2.setDescription("D2");

        when(projectService.getAllProjects()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("P1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("P2"));

        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    @DisplayName("POST /api/projects creates project")
    void createProject() throws Exception {
        Project saved = new Project();
        saved.setId(1L);
        saved.setName("P");
        saved.setDescription("D");

        when(projectService.createProject(any(Project.class))).thenReturn(saved);

        Project payload = new Project();
        payload.setName("P");
        payload.setDescription("D");

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("P"))
                .andExpect(jsonPath("$.description").value("D"));

        verify(projectService, times(1)).createProject(any(Project.class));
    }

    @Test
    @DisplayName("PUT /api/projects/{id} returns 404 when project missing")
    void updateProjectNotFound() throws Exception {
        when(projectService.updateProject(eq(1L), any(Project.class))).thenReturn(null);

        Project payload = new Project();
        payload.setName("New");
        payload.setDescription("Desc");

        mockMvc.perform(put("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isNotFound());

        verify(projectService, times(1)).updateProject(eq(1L), any(Project.class));
    }

    @Test
    @DisplayName("PUT /api/projects/{id} updates project")
    void updateProject() throws Exception {
        Project updated = new Project();
        updated.setId(1L);
        updated.setName("New");
        updated.setDescription("Desc");

        when(projectService.updateProject(eq(1L), any(Project.class))).thenReturn(updated);

        Project payload = new Project();
        payload.setName("New");
        payload.setDescription("Desc");

        mockMvc.perform(put("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New"))
                .andExpect(jsonPath("$.description").value("Desc"));

        verify(projectService, times(1)).updateProject(eq(1L), any(Project.class));
    }

    @Test
    @DisplayName("DELETE /api/projects/{id} returns 404 when project missing")
    void deleteProjectNotFound() throws Exception {
        when(projectService.deleteProject(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isNotFound());

        verify(projectService, times(1)).deleteProject(1L);
    }

    @Test
    @DisplayName("DELETE /api/projects/{id} deletes project")
    void deleteProject() throws Exception {
        when(projectService.deleteProject(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isNoContent());

        verify(projectService, times(1)).deleteProject(1L);
    }
}

