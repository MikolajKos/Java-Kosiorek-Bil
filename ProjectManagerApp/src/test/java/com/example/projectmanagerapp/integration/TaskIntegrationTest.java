package com.example.projectmanagerapp.integration;

import com.example.projectmanagerapp.dto.CreateTaskRequest;
import com.example.projectmanagerapp.model.Project;
import com.example.projectmanagerapp.model.Task;
import com.example.projectmanagerapp.model.TaskType;
import com.example.projectmanagerapp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskIntegrationTest extends AbstractIntegrationTest {

    private User persistUser(String username) {
        User user = new User();
        user.setUsername(username);
        return userRepository.save(user);
    }

    private Project persistProject(String name) {
        Project project = new Project();
        project.setName(name);
        return projectRepository.save(project);
    }

    @Test
    @DisplayName("POST /api/tasks creates a task linked to a project and user")
    void createTask() throws Exception {
        User user = persistUser("eve");
        Project project = persistProject("Voyager");

        CreateTaskRequest request = new CreateTaskRequest(
                "Write docs", "Document the API", TaskType.FEATURE,
                project.getId(), user.getId());

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Write docs"))
                .andExpect(jsonPath("$.taskType").value("FEATURE"))
                .andExpect(jsonPath("$.project.id").value(project.getId()))
                .andExpect(jsonPath("$.user.id").value(user.getId()));

        Task persisted = taskRepository.findAll().getFirst();
        assertThat(persisted.getProject().getId()).isEqualTo(project.getId());
        assertThat(persisted.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("POST /api/tasks returns 404 when the project does not exist")
    void createTaskUnknownProject() throws Exception {
        User user = persistUser("frank");

        CreateTaskRequest request = new CreateTaskRequest(
                "Orphan", "No project", TaskType.BUG, 9999L, user.getId());

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        assertThat(taskRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("POST /api/tasks returns 404 when the user does not exist")
    void createTaskUnknownUser() throws Exception {
        Project project = persistProject("Pioneer");

        CreateTaskRequest request = new CreateTaskRequest(
                "Orphan", "No user", TaskType.BUG, project.getId(), 9999L);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        assertThat(taskRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("GET /api/tasks returns persisted tasks with their relations")
    void getAllTasks() throws Exception {
        User user = persistUser("grace");
        Project project = persistProject("Cassini");

        Task task = new Task();
        task.setTitle("Review");
        task.setDescription("Review the PR");
        task.setTaskType(TaskType.IMPROVEMENT);
        task.setProject(project);
        task.setUser(user);
        taskRepository.save(task);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Review"))
                .andExpect(jsonPath("$[0].project.id").value(project.getId()))
                .andExpect(jsonPath("$[0].project.name").value("Cassini"))
                .andExpect(jsonPath("$[0].user.id").value(user.getId()))
                .andExpect(jsonPath("$[0].user.username").value("grace"));
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} updates an existing task")
    void updateTask() throws Exception {
        User user = persistUser("heidi");
        Project project = persistProject("Juno");

        Task task = new Task();
        task.setTitle("Old title");
        task.setDescription("Old desc");
        task.setTaskType(TaskType.OTHER);
        task.setProject(project);
        task.setUser(user);
        Long id = taskRepository.save(task).getId();

        CreateTaskRequest request = new CreateTaskRequest(
                "New title", "New desc", TaskType.BUG,
                project.getId(), user.getId());

        mockMvc.perform(put("/api/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("New title"))
                .andExpect(jsonPath("$.taskType").value("BUG"));

        Task updated = taskRepository.findById(id).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("New title");
        assertThat(updated.getTaskType()).isEqualTo(TaskType.BUG);
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} returns 404 when the task is missing")
    void updateTaskNotFound() throws Exception {
        User user = persistUser("ivan");
        Project project = persistProject("Kepler");

        CreateTaskRequest request = new CreateTaskRequest(
                "X", "Y", TaskType.BUG, project.getId(), user.getId());

        mockMvc.perform(put("/api/tasks/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} removes the task")
    void deleteTask() throws Exception {
        User user = persistUser("judy");
        Project project = persistProject("Hubble");

        Task task = new Task();
        task.setTitle("Temp");
        task.setTaskType(TaskType.OTHER);
        task.setProject(project);
        task.setUser(user);
        Long id = taskRepository.save(task).getId();

        mockMvc.perform(delete("/api/tasks/" + id))
                .andExpect(status().isNoContent());

        assertThat(taskRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} returns 404 when the task is missing")
    void deleteTaskNotFound() throws Exception {
        mockMvc.perform(delete("/api/tasks/9999"))
                .andExpect(status().isNotFound());
    }
}
