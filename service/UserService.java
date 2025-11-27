package com.movieticket.service;

import com.movieticket.model.User;
import com.movieticket.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // CRUD
    public User create(User user) {
        return userRepository.save(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public Optional<User> getById(int id) {
        return userRepository.findById(id);
    }

    public User update(int id, User updated) {
        return userRepository.findById(id).map(user -> {
            user.setName(updated.getName());
            user.setEmail(updated.getEmail());
            user.setPassword(updated.getPassword());
            user.setRole(updated.getRole());
            return userRepository.save(user);
        }).orElse(null);
    }

    public void delete(int id) {
        userRepository.deleteById(id);
    }

    // NEW: duplicate-email check used by the controller
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // LOGIN (your current plain-text approach)
    public Optional<User> login(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }
}
