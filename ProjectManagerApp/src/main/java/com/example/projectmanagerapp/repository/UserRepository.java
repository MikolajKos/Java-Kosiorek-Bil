package com.example.projectmanagerapp.repository;

import com.example.projectmanagerapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
