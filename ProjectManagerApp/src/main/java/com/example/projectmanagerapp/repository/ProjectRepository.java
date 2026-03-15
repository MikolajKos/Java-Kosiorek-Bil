package com.example.projectmanagerapp.repository;

import com.example.projectmanagerapp.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
