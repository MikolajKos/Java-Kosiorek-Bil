package com.example.projectmanagerapp.integration;

import com.example.projectmanagerapp.repository.ProjectRepository;
import com.example.projectmanagerapp.repository.TaskRepository;
import com.example.projectmanagerapp.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine");

    static {
        POSTGRES.start();
    }

    @Autowired
    protected MockMvc mockMvc;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ProjectRepository projectRepository;

    @Autowired
    protected TaskRepository taskRepository;

    @PersistenceContext
    protected EntityManager entityManager;

    @BeforeEach
    void cleanDatabase() {
        taskRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }
}
