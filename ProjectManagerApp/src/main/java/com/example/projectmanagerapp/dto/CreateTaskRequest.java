package com.example.projectmanagerapp.dto;

import com.example.projectmanagerapp.model.TaskType;

public record CreateTaskRequest(
        String title,
        String description,
        TaskType taskType,
        Long projectId,
        Long userId) {
}
