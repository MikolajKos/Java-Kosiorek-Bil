package com.example.projectmanagerapp.service;

import com.example.projectmanagerapp.dto.CreateTaskRequest;
import com.example.projectmanagerapp.model.Project;
import com.example.projectmanagerapp.model.Task;
import com.example.projectmanagerapp.model.TaskType;
import com.example.projectmanagerapp.model.User;
import com.example.projectmanagerapp.repository.ProjectRepository;
import com.example.projectmanagerapp.repository.TaskRepository;
import com.example.projectmanagerapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    private TaskRepository taskRepository;
    private ProjectRepository projectRepository;
    private UserRepository userRepository;
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        projectRepository = mock(ProjectRepository.class);
        userRepository = mock(UserRepository.class);
        taskService = new TaskService(taskRepository, projectRepository, userRepository);
    }

    @Test
    @DisplayName("Should return all tasks")
    void testGetAllTasks() {
        Task t1 = new Task();
        t1.setTitle("T1");
        t1.setTaskType(TaskType.BUG);

        Task t2 = new Task();
        t2.setTitle("T2");
        t2.setTaskType(TaskType.FEATURE);

        when(taskRepository.findAll()).thenReturn(Arrays.asList(t1, t2));

        List<Task> tasks = taskService.getAllTasks();

        assertEquals(2, tasks.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return null when creating task if project is missing")
    void testCreateTaskMissingProject() {
        CreateTaskRequest request = new CreateTaskRequest("T", "D", TaskType.BUG, 10L, 20L);
        when(projectRepository.findById(10L)).thenReturn(Optional.empty());
        when(userRepository.findById(20L)).thenReturn(Optional.of(new User()));

        Task created = taskService.createTask(request);

        assertNull(created);
        verify(projectRepository, times(1)).findById(10L);
        verify(userRepository, times(1)).findById(20L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Should return null when creating task if user is missing")
    void testCreateTaskMissingUser() {
        CreateTaskRequest request = new CreateTaskRequest("T", "D", TaskType.BUG, 10L, 20L);

        when(projectRepository.findById(10L)).thenReturn(Optional.of(new Project()));
        when(userRepository.findById(20L)).thenReturn(Optional.empty());

        Task created = taskService.createTask(request);

        assertNull(created);
        verify(projectRepository, times(1)).findById(10L);
        verify(userRepository, times(1)).findById(20L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Should create task when project and user exist")
    void testCreateTask() {
        Project project = new Project();
        project.setName("P");

        User user = new User();
        user.setUsername("U");

        CreateTaskRequest request = new CreateTaskRequest("Title", "Desc", TaskType.FEATURE, 10L, 20L);

        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(userRepository.findById(20L)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task created = taskService.createTask(request);

        assertNotNull(created);
        assertEquals("Title", created.getTitle());
        assertEquals("Desc", created.getDescription());
        assertEquals(TaskType.FEATURE, created.getTaskType());
        assertSame(project, created.getProject());
        assertSame(user, created.getUser());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should return null when updating missing task")
    void testUpdateTaskMissingTask() {
        CreateTaskRequest request = new CreateTaskRequest("T", "D", TaskType.BUG, 10L, 20L);
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        Task updated = taskService.updateTask(1L, request);

        assertNull(updated);
        verify(taskRepository, times(1)).findById(1L);
        verify(projectRepository, never()).findById(anyLong());
        verify(userRepository, never()).findById(anyLong());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Should return null when updating task if project is missing")
    void testUpdateTaskMissingProject() {
        Task existing = new Task();
        existing.setTitle("Old");
        existing.setTaskType(TaskType.OTHER);

        CreateTaskRequest request = new CreateTaskRequest("New", "D", TaskType.IMPROVEMENT, 10L, 20L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(projectRepository.findById(10L)).thenReturn(Optional.empty());
        when(userRepository.findById(20L)).thenReturn(Optional.of(new User()));

        Task updated = taskService.updateTask(1L, request);

        assertNull(updated);
        verify(projectRepository, times(1)).findById(10L);
        verify(userRepository, times(1)).findById(20L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Should return null when updating task if user is missing")
    void testUpdateTaskMissingUser() {
        Task existing = new Task();
        existing.setTitle("Old");
        existing.setTaskType(TaskType.OTHER);

        CreateTaskRequest request = new CreateTaskRequest("New", "D", TaskType.IMPROVEMENT, 10L, 20L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(projectRepository.findById(10L)).thenReturn(Optional.of(new Project()));
        when(userRepository.findById(20L)).thenReturn(Optional.empty());

        Task updated = taskService.updateTask(1L, request);

        assertNull(updated);
        verify(projectRepository, times(1)).findById(10L);
        verify(userRepository, times(1)).findById(20L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Should update task when project and user exist")
    void testUpdateTask() {
        Task existing = new Task();
        existing.setTitle("Old");
        existing.setDescription("OldD");
        existing.setTaskType(TaskType.OTHER);

        Project project = new Project();
        project.setName("P");

        User user = new User();
        user.setUsername("U");

        CreateTaskRequest request = new CreateTaskRequest("New", "NewD", TaskType.BUG, 10L, 20L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(userRepository.findById(20L)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task updated = taskService.updateTask(1L, request);

        assertNotNull(updated);
        assertEquals("New", updated.getTitle());
        assertEquals("NewD", updated.getDescription());
        assertEquals(TaskType.BUG, updated.getTaskType());
        assertSame(project, updated.getProject());
        assertSame(user, updated.getUser());
        verify(taskRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("Should return false when deleting missing task")
    void testDeleteTaskMissing() {
        when(taskRepository.existsById(1L)).thenReturn(false);

        boolean deleted = taskService.deleteTask(1L);

        assertFalse(deleted);
        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should delete existing task")
    void testDeleteTask() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        boolean deleted = taskService.deleteTask(1L);

        assertTrue(deleted);
        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }
}

