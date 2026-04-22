package com.example.projectmanagerapp.service;

import com.example.projectmanagerapp.dto.CreateUserRequest;
import com.example.projectmanagerapp.model.User;
import com.example.projectmanagerapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long id, CreateUserRequest request) {
        User existing = userRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setUsername(request.username());
        return userRepository.save(existing);
    }

    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }
}