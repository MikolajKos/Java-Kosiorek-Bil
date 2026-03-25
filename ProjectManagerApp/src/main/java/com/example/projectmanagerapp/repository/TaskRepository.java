package com.example.projectmanagerapp.repository;

import com.example.projectmanagerapp.model.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @EntityGraph(attributePaths = {"project", "user"})
    @Override
    List<Task> findAll();
}
