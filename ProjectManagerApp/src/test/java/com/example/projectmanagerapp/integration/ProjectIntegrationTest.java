package com.example.projectmanagerapp.integration;

import com.example.projectmanagerapp.model.Project;
import com.example.projectmanagerapp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProjectIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("POST /api/projects creates a project and persists it")
    void createProject() throws Exception {
        Project payload = new Project();
        payload.setName("Apollo");
        payload.setDescription("Moon project");

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Apollo"))
                .andExpect(jsonPath("$.description").value("Moon project"));

        assertThat(projectRepository.findAll())
                .singleElement()
                .extracting(Project::getName)
                .isEqualTo("Apollo");
    }

    @Test
    @DisplayName("GET /api/projects returns persisted projects")
    void getAllProjects() throws Exception {
        Project p1 = new Project();
        p1.setName("P1");
        Project p2 = new Project();
        p2.setName("P2");
        projectRepository.save(p1);
        projectRepository.save(p2);

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].name", org.hamcrest.Matchers.containsInAnyOrder("P1", "P2")));
    }

    @Test
    @DisplayName("PUT /api/projects/{id} updates an existing project")
    void updateProject() throws Exception {
        Project project = new Project();
        project.setName("Old");
        project.setDescription("Old desc");
        Long id = projectRepository.save(project).getId();

        Project payload = new Project();
        payload.setName("New");
        payload.setDescription("New desc");

        mockMvc.perform(put("/api/projects/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("New"))
                .andExpect(jsonPath("$.description").value("New desc"));

        assertThat(projectRepository.findById(id))
                .get()
                .extracting(Project::getName)
                .isEqualTo("New");
    }

    @Test
    @DisplayName("PUT /api/projects/{id} returns 404 when project is missing")
    void updateProjectNotFound() throws Exception {
        Project payload = new Project();
        payload.setName("New");

        mockMvc.perform(put("/api/projects/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/projects/{id} removes the project")
    void deleteProject() throws Exception {
        Project project = new Project();
        project.setName("Temp");
        Long id = projectRepository.save(project).getId();

        mockMvc.perform(delete("/api/projects/" + id))
                .andExpect(status().isNoContent());

        assertThat(projectRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/projects/{id} returns 404 when project is missing")
    void deleteProjectNotFound() throws Exception {
        mockMvc.perform(delete("/api/projects/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @DisplayName("Project <-> User many-to-many association persists in the join table")
    void projectUserRelationshipPersists() {
        User user = new User();
        user.setUsername("dave");
        user = userRepository.save(user);

        Project project = new Project();
        project.setName("Gemini");
        project.getUsers().add(user);
        project = projectRepository.save(project);

        Long projectId = project.getId();
        Long userId = user.getId();

        entityManager.flush();
        entityManager.clear();

        Project reloadedProject = projectRepository.findById(projectId).orElseThrow();
        assertThat(reloadedProject.getUsers())
                .extracting(User::getId)
                .containsExactly(userId);

        User reloadedUser = userRepository.findById(userId).orElseThrow();
        assertThat(reloadedUser.getProjects())
                .extracting(Project::getId)
                .containsExactly(projectId);
    }
}
