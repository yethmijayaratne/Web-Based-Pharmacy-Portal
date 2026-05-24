package com.example.pharmacy_System.service;

import com.example.pharmacy_System.config.PasswordUtil;
import com.example.pharmacy_System.entity.User;
import com.example.pharmacy_System.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        // Validate email uniqueness
        if (emailExists(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Validate password strength
        if (user.getPassword().length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters long");
        }

        // Encrypt password before saving
        String encryptedPassword = PasswordUtil.encrypt(user.getPassword());
        user.setPassword(encryptedPassword);

        return userRepository.save(user);
    }

    public Optional<User> loginUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            User foundUser = user.get();
            // Use PasswordUtil to validate password
            if (PasswordUtil.checkPassword(password, foundUser.getPassword())) {
                // Update last login
                foundUser.setLastLogin(LocalDateTime.now());
                userRepository.save(foundUser);
                return Optional.of(foundUser);
            }
        }
        return Optional.empty();
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean validatePassword(String rawPassword, String storedPassword) {
        // Use PasswordUtil for password validation
        return PasswordUtil.checkPassword(rawPassword, storedPassword);
    }

    public long getUserCount() {
        return userRepository.count();
    }

    public User updateUserPassword(User user, String newPassword) {
        String encryptedPassword = PasswordUtil.encrypt(newPassword);
        user.setPassword(encryptedPassword);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}