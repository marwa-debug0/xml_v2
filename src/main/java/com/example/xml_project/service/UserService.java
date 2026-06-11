package com.example.xml_project.service;

import com.example.xml_project.model.User;
import com.example.xml_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Returns all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Returns user by it's ID (Optional = might be empty if no user is found)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Creates and saves a new user
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // Updates and existing user
    public Optional<User> updateUser(Long id, User updated) {
        return userRepository.findById(id).map(existing -> { // map() : if user exists modify it, if not skip 
            existing.setName(updated.getName());
            existing.setEmail(updated.getEmail());
            existing.setPassword(updated.getPassword());
            return userRepository.save(existing);
        });
    }

    // Deletes a user
    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) return false;
        userRepository.deleteById(id);
        return true;
    }


}