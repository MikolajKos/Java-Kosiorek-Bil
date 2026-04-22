package com.example.projectmanagerapp.service;

import com.example.projectmanagerapp.dto.CreateUserRequest;
import com.example.projectmanagerapp.model.User;
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

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    @DisplayName("Should return all users")
    void testGetAllUsers() {
        User user1 = new User();
        user1.setUsername("TestUser1");

        User user2 = new User();
        user2.setUsername("TestUser2");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should create user")
    void testCreateUser() {
        User input = new User();
        input.setUsername("Alice");

        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User created = userService.createUser(input);

        assertNotNull(created);
        assertEquals("Alice", created.getUsername());
        verify(userRepository, times(1)).save(input);
    }

    @Test
    @DisplayName("Should return null when updating missing user")
    void testUpdateUserMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        User updated = userService.updateUser(1L, new CreateUserRequest("Bob"));

        assertNull(updated);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should update existing user")
    void testUpdateUser() {
        User existing = new User();
        existing.setUsername("Old");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User updated = userService.updateUser(1L, new CreateUserRequest("New"));

        assertNotNull(updated);
        assertEquals("New", updated.getUsername());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("Should return false when deleting missing user")
    void testDeleteUserMissing() {
        when(userRepository.existsById(1L)).thenReturn(false);

        boolean deleted = userService.deleteUser(1L);

        assertFalse(deleted);
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should delete existing user")
    void testDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        boolean deleted = userService.deleteUser(1L);

        assertTrue(deleted);
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
}

