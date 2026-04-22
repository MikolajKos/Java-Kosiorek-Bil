package com.example.projectmanagerapp.service;

import com.example.projectmanagerapp.dto.CreateTaskRequest;
import com.example.projectmanagerapp.model.Project;
import com.example.projectmanagerapp.model.Task;
import com.example.projectmanagerapp.model.User;
import com.example.projectmanagerapp.repository.ProjectRepository;
import com.example.projectmanagerapp.repository.TaskRepository;
import com.example.projectmanagerapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task createTask(CreateTaskRequest request) {
        Project project = projectRepository.findById(request.projectId()).orElse(null);
        User user = userRepository.findById(request.userId()).orElse(null);

        if (project == null || user == null) return null;

        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setTaskType(request.taskType());
        task.setProject(project);
        task.setUser(user);

        return taskRepository.save(task);
    }

    // --- NOWE METODY ---

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    public Task updateTask(Long id, CreateTaskRequest request) {
        Task existingTask = taskRepository.findById(id).orElse(null);
        if (existingTask == null) return null;

        Project project = projectRepository.findById(request.projectId()).orElse(null);
        User user = userRepository.findById(request.userId()).orElse(null);

        if (project == null || user == null) return null;

        existingTask.setTitle(request.title());
        existingTask.setDescription(request.description());
        existingTask.setTaskType(request.taskType());
        existingTask.setProject(project);
        existingTask.setUser(user);

        return taskRepository.save(existingTask);
    }

    public boolean deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            return false;
        }
        taskRepository.deleteById(id);
        return true;
    }
}