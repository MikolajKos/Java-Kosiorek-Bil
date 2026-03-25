package com.example.projectmanagerapp.repository;

import com.example.projectmanagerapp.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
