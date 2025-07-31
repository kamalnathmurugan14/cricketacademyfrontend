package com.cricketacademy.api.controller;

import com.cricketacademy.api.dto.ApiResponse;
import com.cricketacademy.api.entity.User;
import com.cricketacademy.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for user management endpoints
 * Handles user profile operations and user data retrieval
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    /**
     * Get current user profile
     * GET /api/users/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        return userService.findByEmail(email)
                .map(user -> {
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("id", user.getId());
                    userData.put("name", user.getName());
                    userData.put("email", user.getEmail());
                    userData.put("phone", user.getPhone());
                    userData.put("age", user.getAge());
                    userData.put("experienceLevel", user.getExperienceLevel());
                    userData.put("role", user.getRole());
                    userData.put("createdAt", user.getCreatedAt());
                    userData.put("isActive", user.getIsActive());

                    ApiResponse<Map<String, Object>> response = ApiResponse.success(
                        "User profile retrieved successfully", userData);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get user by ID (admin only)
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> {
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("id", user.getId());
                    userData.put("name", user.getName());
                    userData.put("email", user.getEmail());
                    userData.put("phone", user.getPhone());
                    userData.put("age", user.getAge());
                    userData.put("experienceLevel", user.getExperienceLevel());
                    userData.put("role", user.getRole());
                    userData.put("createdAt", user.getCreatedAt());
                    userData.put("isActive", user.getIsActive());

                    ApiResponse<Map<String, Object>> response = ApiResponse.success(
                        "User retrieved successfully", userData);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all active users (admin only)
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllActiveUsers();
        ApiResponse<List<User>> response = ApiResponse.success(
            "Users retrieved successfully", users);
        return ResponseEntity.ok(response);
    }

    /**
     * Get users by experience level
     * GET /api/users/experience-level/{level}
     */
    @GetMapping("/experience-level/{level}")
    public ResponseEntity<ApiResponse<List<User>>> getUsersByExperienceLevel(
            @PathVariable User.ExperienceLevel level) {
        List<User> users = userService.getUsersByExperienceLevel(level);
        ApiResponse<List<User>> response = ApiResponse.success(
            "Users retrieved successfully", users);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user statistics (admin only)
     * GET /api/users/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<UserService.UserStatistics>> getUserStatistics() {
        UserService.UserStatistics statistics = userService.getUserStatistics();
        ApiResponse<UserService.UserStatistics> response = ApiResponse.success(
            "User statistics retrieved successfully", statistics);
        return ResponseEntity.ok(response);
    }

    /**
     * Update user profile
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateUser(
            @PathVariable Long id, @RequestBody User updatedUser) {
        
        try {
            User user = userService.updateUser(id, updatedUser);
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("name", user.getName());
            userData.put("email", user.getEmail());
            userData.put("phone", user.getPhone());
            userData.put("age", user.getAge());
            userData.put("experienceLevel", user.getExperienceLevel());
            userData.put("role", user.getRole());
            userData.put("updatedAt", user.getUpdatedAt());
            userData.put("isActive", user.getIsActive());

            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                "User updated successfully", userData);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to update user with ID: {}", id, e);
            ApiResponse<Map<String, Object>> response = ApiResponse.error(
                "Failed to update user: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Deactivate user account
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deactivateUser(@PathVariable Long id) {
        try {
            userService.deactivateUser(id);
            ApiResponse<String> response = ApiResponse.success("User deactivated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to deactivate user with ID: {}", id, e);
            ApiResponse<String> response = ApiResponse.error(
                "Failed to deactivate user: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}