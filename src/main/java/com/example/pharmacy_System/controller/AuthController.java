package com.example.pharmacy_System.controller;

import com.example.pharmacy_System.entity.User;
import com.example.pharmacy_System.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@Validated
public class AuthController {

    @Autowired
    private UserService userService;

    // Serve login page
    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    // Serve dashboard page
    @GetMapping("/dashboard")
    public String dashboardPage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/";
        }

        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "customerdashboard";
        }

        return "redirect:/";
    }

    // Serve discover medicine page
    @GetMapping("/discover-medicine")
    public String discoverMedicinePage(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/";
        }
        return "discover-medicine";
    }

    // Serve order medicine page
    @GetMapping("/order-medicine")
    public String orderMedicinePage(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/";
        }
        return "order-medicine";
    }

    // Serve current orders page
    @GetMapping("/current-orders")
    public String currentOrdersPage(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/";
        }
        return "current-orders";
    }

    // Serve chat page
    @GetMapping("/chat")
    public String chatPage(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/";
        }
        return "chat";
    }

    // Serve cart page
    @GetMapping("/cart")
    public String cartPage(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/";
        }
        return "cart";
    }

    // Handle login (AJAX)
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            // Validate input
            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Email is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (password == null || password.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Password is required");
                return ResponseEntity.badRequest().body(response);
            }

            Optional<User> user = userService.loginUser(email.trim(), password);
            if (user.isPresent()) {
                User loggedInUser = user.get();
                session.setAttribute("userId", loggedInUser.getId());
                session.setAttribute("userEmail", loggedInUser.getEmail());
                session.setAttribute("userName", loggedInUser.getFullName());
                session.setAttribute("userRole", "CUSTOMER");

                response.put("success", true);
                response.put("message", "Login successful");
                response.put("redirect", "/dashboard");
                response.put("user", Map.of(
                        "id", loggedInUser.getId(),
                        "name", loggedInUser.getFullName(),
                        "email", loggedInUser.getEmail()
                ));
            } else {
                response.put("success", false);
                response.put("message", "Invalid email or password");
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Login failed: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    // Handle registration (AJAX)
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> registrationData, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            String firstName = registrationData.get("firstName");
            String lastName = registrationData.get("lastName");
            String email = registrationData.get("email");
            String phoneNumber = registrationData.get("phoneNumber");
            String password = registrationData.get("password");
            String confirmPassword = registrationData.get("confirmPassword");
            String membershipType = registrationData.get("membershipType");

            // Validate required fields
            if (firstName == null || firstName.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "First name is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (lastName == null || lastName.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Last name is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Email is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (password == null || password.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Password is required");
                return ResponseEntity.badRequest().body(response);
            }

            // Validate email format
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                response.put("success", false);
                response.put("message", "Please enter a valid email address");
                return ResponseEntity.badRequest().body(response);
            }

            // Validate password match
            if (!password.equals(confirmPassword)) {
                response.put("success", false);
                response.put("message", "Passwords do not match");
                return ResponseEntity.badRequest().body(response);
            }

            // Validate password strength
            if (password.length() < 8) {
                response.put("success", false);
                response.put("message", "Password must be at least 8 characters long");
                return ResponseEntity.badRequest().body(response);
            }

            // Check if email already exists
            if (userService.emailExists(email)) {
                response.put("success", false);
                response.put("message", "Email already exists");
                return ResponseEntity.badRequest().body(response);
            }

            // Create new user
            User user = User.builder()
                    .firstName(firstName.trim())
                    .lastName(lastName.trim())
                    .email(email.trim().toLowerCase())
                    .phoneNumber(phoneNumber != null ? phoneNumber.trim() : null)
                    .password(password)
                    .membershipType(membershipType != null ? membershipType : "Standard")
                    .build();

            User savedUser = userService.registerUser(user);

            // Set session attributes
            session.setAttribute("userId", savedUser.getId());
            session.setAttribute("userEmail", savedUser.getEmail());
            session.setAttribute("userName", savedUser.getFullName());
            session.setAttribute("userRole", "CUSTOMER");

            response.put("success", true);
            response.put("message", "Registration successful");
            response.put("redirect", "/dashboard");
            response.put("user", Map.of(
                    "id", savedUser.getId(),
                    "name", savedUser.getFullName(),
                    "email", savedUser.getEmail()
            ));

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    // Handle logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // Check if user is logged in (for AJAX calls)
    @GetMapping("/check-session")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkSession(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");

        if (userId != null) {
            Optional<User> user = userService.getUserById(userId);
            if (user.isPresent()) {
                response.put("loggedIn", true);
                response.put("userId", userId);
                response.put("userEmail", session.getAttribute("userEmail"));
                response.put("userName", session.getAttribute("userName"));
                response.put("userRole", session.getAttribute("userRole"));
            } else {
                response.put("loggedIn", false);
            }
        } else {
            response.put("loggedIn", false);
        }

        return ResponseEntity.ok(response);
    }

    // Get current user info
    @GetMapping("/current-user")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");

        if (userId != null) {
            Optional<User> user = userService.getUserById(userId);
            if (user.isPresent()) {
                User currentUser = user.get();
                response.put("success", true);
                response.put("user", Map.of(
                        "id", currentUser.getId(),
                        "firstName", currentUser.getFirstName(),
                        "lastName", currentUser.getLastName(),
                        "fullName", currentUser.getFullName(),
                        "email", currentUser.getEmail(),
                        "phoneNumber", currentUser.getPhoneNumber(),
                        "membershipType", currentUser.getMembershipType()
                ));
            } else {
                response.put("success", false);
                response.put("message", "User not found");
            }
        } else {
            response.put("success", false);
            response.put("message", "Not logged in");
        }

        return ResponseEntity.ok(response);
    }
}