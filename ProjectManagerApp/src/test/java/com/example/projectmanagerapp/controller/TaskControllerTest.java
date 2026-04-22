package com.example.projectmanagerapp.controller;

import com.example.projectmanagerapp.dto.CreateTaskRequest;
import com.example.projectmanagerapp.model.Task;
import com.example.projectmanagerapp.model.TaskType;
import com.example.projectmanagerapp.service.TaskService;
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

class TaskControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        taskService = mock(TaskService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new TaskController(taskService)).build();
    }

    @Test
    @DisplayName("GET /api/tasks returns tasks")
    void getAllTasks() throws Exception {
        Task t1 = new Task();
        t1.setId(1L);
        t1.setTitle("T1");
        t1.setDescription("D1");
        t1.setTaskType(TaskType.BUG);

        Task t2 = new Task();
        t2.setId(2L);
        t2.setTitle("T2");
        t2.setDescription("D2");
        t2.setTaskType(TaskType.FEATURE);

        when(taskService.getAllTasks()).thenReturn(List.of(t1, t2));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("T1"))
                .andExpect(jsonPath("$[0].taskType").value("BUG"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("T2"))
                .andExpect(jsonPath("$[1].taskType").value("FEATURE"));

        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    @DisplayName("POST /api/tasks returns 404 when service returns null")
    void createTaskNotFound() throws Exception {
        when(taskService.createTask(any(CreateTaskRequest.class))).thenReturn(null);

        CreateTaskRequest payload = new CreateTaskRequest("T", "D", TaskType.BUG, 1L, 2L);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).createTask(any(CreateTaskRequest.class));
    }

    @Test
    @DisplayName("POST /api/tasks creates task")
    void createTask() throws Exception {
        Task saved = new Task();
        saved.setId(1L);
        saved.setTitle("T");
        saved.setDescription("D");
        saved.setTaskType(TaskType.BUG);

        when(taskService.createTask(any(CreateTaskRequest.class))).thenReturn(saved);

        CreateTaskRequest payload = new CreateTaskRequest("T", "D", TaskType.BUG, 1L, 2L);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("T"))
                .andExpect(jsonPath("$.description").value("D"))
                .andExpect(jsonPath("$.taskType").value("BUG"));

        verify(taskService, times(1)).createTask(any(CreateTaskRequest.class));
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} returns 404 when task missing")
    void updateTaskNotFound() throws Exception {
        when(taskService.updateTask(eq(1L), any(CreateTaskRequest.class))).thenReturn(null);

        CreateTaskRequest payload = new CreateTaskRequest("T", "D", TaskType.OTHER, 1L, 2L);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).updateTask(eq(1L), any(CreateTaskRequest.class));
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} updates task")
    void updateTask() throws Exception {
        Task updated = new Task();
        updated.setId(1L);
        updated.setTitle("T");
        updated.setDescription("D");
        updated.setTaskType(TaskType.IMPROVEMENT);

        when(taskService.updateTask(eq(1L), any(CreateTaskRequest.class))).thenReturn(updated);

        CreateTaskRequest payload = new CreateTaskRequest("T", "D", TaskType.IMPROVEMENT, 1L, 2L);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.taskType").value("IMPROVEMENT"));

        verify(taskService, times(1)).updateTask(eq(1L), any(CreateTaskRequest.class));
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} returns 404 when task missing")
    void deleteTaskNotFound() throws Exception {
        when(taskService.deleteTask(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).deleteTask(1L);
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} deletes task")
    void deleteTask() throws Exception {
        when(taskService.deleteTask(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(1L);
    }
}

