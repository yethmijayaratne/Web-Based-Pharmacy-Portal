package com.example.pharmacy_System.controller;

import com.example.pharmacy_System.entity.User;
import com.example.pharmacy_System.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    // Get user profile data
    @GetMapping("/data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProfileData(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("message", "User not logged in");
            return ResponseEntity.ok(response);
        }

        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            response.put("success", true);
            response.put("user", user.get());
        } else {
            response.put("success", false);
            response.put("message", "User not found");
        }

        return ResponseEntity.ok(response);
    }

    // Update user profile
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody Map<String, String> profileData, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("message", "User not logged in");
            return ResponseEntity.ok(response);
        }

        try {
            Optional<User> optionalUser = userService.getUserById(userId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                // Update user fields with validation
                if (profileData.containsKey("firstName")) {
                    String firstName = profileData.get("firstName");
                    if (firstName != null && !firstName.trim().isEmpty()) {
                        user.setFirstName(firstName.trim());
                    }
                }

                if (profileData.containsKey("lastName")) {
                    String lastName = profileData.get("lastName");
                    if (lastName != null && !lastName.trim().isEmpty()) {
                        user.setLastName(lastName.trim());
                    }
                }

                if (profileData.containsKey("phoneNumber")) {
                    user.setPhoneNumber(profileData.get("phoneNumber"));
                }

                if (profileData.containsKey("dateOfBirth") && !profileData.get("dateOfBirth").isEmpty()) {
                    try {
                        user.setDateOfBirth(LocalDate.parse(profileData.get("dateOfBirth")));
                    } catch (Exception e) {
                        response.put("success", false);
                        response.put("message", "Invalid date format. Use YYYY-MM-DD");
                        return ResponseEntity.badRequest().body(response);
                    }
                }

                if (profileData.containsKey("address")) {
                    user.setAddress(profileData.get("address"));
                }

                if (profileData.containsKey("bloodType")) {
                    user.setBloodType(profileData.get("bloodType"));
                }

                if (profileData.containsKey("allergies")) {
                    user.setAllergies(profileData.get("allergies"));
                }

                User updatedUser = userService.updateUser(user);

                // Update session name if firstName or lastName changed
                if (profileData.containsKey("firstName") || profileData.containsKey("lastName")) {
                    session.setAttribute("userName", updatedUser.getFullName());
                }

                response.put("success", true);
                response.put("message", "Profile updated successfully");
                response.put("user", Map.of(
                        "firstName", updatedUser.getFirstName(),
                        "lastName", updatedUser.getLastName(),
                        "fullName", updatedUser.getFullName(),
                        "phoneNumber", updatedUser.getPhoneNumber(),
                        "dateOfBirth", updatedUser.getDateOfBirth(),
                        "address", updatedUser.getAddress(),
                        "bloodType", updatedUser.getBloodType(),
                        "allergies", updatedUser.getAllergies()
                ));
            } else {
                response.put("success", false);
                response.put("message", "User not found");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating profile: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    // Change password
    @PostMapping("/change-password")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> passwordData, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("message", "User not logged in");
            return ResponseEntity.ok(response);
        }

        try {
            String currentPassword = passwordData.get("currentPassword");
            String newPassword = passwordData.get("newPassword");
            String confirmPassword = passwordData.get("confirmPassword");

            // Validate inputs
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Current password is required");
                return ResponseEntity.ok(response);
            }

            if (newPassword == null || newPassword.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "New password is required");
                return ResponseEntity.ok(response);
            }

            if (newPassword.length() < 8) {
                response.put("success", false);
                response.put("message", "New password must be at least 8 characters long");
                return ResponseEntity.ok(response);
            }

            if (!newPassword.equals(confirmPassword)) {
                response.put("success", false);
                response.put("message", "New passwords do not match");
                return ResponseEntity.ok(response);
            }

            Optional<User> optionalUser = userService.getUserById(userId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                // Verify current password using PasswordUtil
                if (!userService.validatePassword(currentPassword, user.getPassword())) {
                    response.put("success", false);
                    response.put("message", "Current password is incorrect");
                    return ResponseEntity.ok(response);
                }

                // Update password using the new method
                userService.updateUserPassword(user, newPassword);

                response.put("success", true);
                response.put("message", "Password updated successfully");
            } else {
                response.put("success", false);
                response.put("message", "User not found");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error changing password: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}