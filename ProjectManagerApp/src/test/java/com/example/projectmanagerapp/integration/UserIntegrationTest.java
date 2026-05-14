package com.example.projectmanagerapp.integration;

import com.example.projectmanagerapp.dto.CreateUserRequest;
import com.example.projectmanagerapp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("POST /api/users creates a user and persists it")
    void createUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest("alice");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("alice"));

        assertThat(userRepository.findAll())
                .singleElement()
                .extracting(User::getUsername)
                .isEqualTo("alice");
    }

    @Test
    @DisplayName("GET /api/users returns persisted users")
    void getAllUsers() throws Exception {
        User u1 = new User();
        u1.setUsername("bob");
        User u2 = new User();
        u2.setUsername("carol");
        userRepository.save(u1);
        userRepository.save(u2);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].username", org.hamcrest.Matchers.containsInAnyOrder("bob", "carol")));
    }

    @Test
    @DisplayName("PUT /api/users/{id} updates an existing user")
    void updateUser() throws Exception {
        User user = new User();
        user.setUsername("old");
        Long id = userRepository.save(user).getId();

        CreateUserRequest request = new CreateUserRequest("new");

        mockMvc.perform(put("/api/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.username").value("new"));

        assertThat(userRepository.findById(id))
                .get()
                .extracting(User::getUsername)
                .isEqualTo("new");
    }

    @Test
    @DisplayName("PUT /api/users/{id} returns 404 when user is missing")
    void updateUserNotFound() throws Exception {
        CreateUserRequest request = new CreateUserRequest("new");

        mockMvc.perform(put("/api/users/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} removes the user")
    void deleteUser() throws Exception {
        User user = new User();
        user.setUsername("temp");
        Long id = userRepository.save(user).getId();

        mockMvc.perform(delete("/api/users/" + id))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/users/{id} returns 404 when user is missing")
    void deleteUserNotFound() throws Exception {
        mockMvc.perform(delete("/api/users/9999"))
                .andExpect(status().isNotFound());
    }
}
