package com.cricketacademy.api.controller;

import com.cricketacademy.api.dto.ApiResponse;
import com.cricketacademy.api.dto.LoginRequest;
import com.cricketacademy.api.dto.RegistrationRequest;
import com.cricketacademy.api.entity.User;
import com.cricketacademy.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for authentication-related endpoints
 * Handles user registration, login, and other auth operations
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    
    /**
     * Login user
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> loginUser(
            @Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        
        log.info("Login request received for email: {}", request.getEmail());

        try {
//            Optional<User> userOpt = userService.authenticateUser(request.getEmail(), request.getPassword());
            String ipAddress = getClientIpAddress(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");
            
            UserService.LoginResult result = userService.loginUser(
                    request.getEmail(), 
                    request.getPassword(), 
                    ipAddress, 
                    userAgent
                );
            if (result.isSuccess()) {
                User user = result.getUser();
                
                // Create response data (excluding sensitive information)
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", user.getId());
                userData.put("name", user.getName());
                userData.put("email", user.getEmail());
                userData.put("phone", user.getPhone());
                userData.put("age", user.getAge());
                userData.put("experienceLevel", user.getExperienceLevel());
                userData.put("role", user.getRole());
                userData.put("createdAt", user.getCreatedAt());
                userData.put("token", result.getToken());

                ApiResponse<Map<String, Object>> response = ApiResponse.success(
                        result.getMessage(), userData);
                
                log.info("User logged in successfully: {}", user.getEmail());
                return ResponseEntity.ok(response);
            } else {
            	 ApiResponse<Map<String, Object>> response = ApiResponse.error(result.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

        } catch (Exception e) {
            log.error("Login failed for email: {}", request.getEmail(), e);
            ApiResponse<Map<String, Object>> response = ApiResponse.error(
                "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    private String getClientIpAddress(HttpServletRequest httpRequest) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
     * Register a new user
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> registerUser(
            @Valid @RequestBody RegistrationRequest request) {
        
        log.info("Registration request received for email: {}", request.getEmail());

        try {
            User registeredUser = userService.registerUser(request);
            
            // Create response data (excluding sensitive information)
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", registeredUser.getId());
            userData.put("name", registeredUser.getName());
            userData.put("email", registeredUser.getEmail());
            userData.put("phone", registeredUser.getPhone());
            userData.put("age", registeredUser.getAge());
            userData.put("experienceLevel", registeredUser.getExperienceLevel());
            userData.put("role", registeredUser.getRole());
            userData.put("createdAt", registeredUser.getCreatedAt());

            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                "User registered successfully", userData);
            
            log.info("User registered successfully with ID: {}", registeredUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Registration failed for email: {}", request.getEmail(), e);
            ApiResponse<Map<String, Object>> response = ApiResponse.error(
                "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
   
    
    /**
     * Health check endpoint
     * GET /api/auth/health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        ApiResponse<String> response = ApiResponse.success("Cricket Academy API is running");
        return ResponseEntity.ok(response);
    }

    /**
     * Get experience levels
     * GET /api/auth/experience-levels
     */
    @GetMapping("/experience-levels")
    public ResponseEntity<ApiResponse<User.ExperienceLevel[]>> getExperienceLevels() {
        User.ExperienceLevel[] levels = User.ExperienceLevel.values();
        ApiResponse<User.ExperienceLevel[]> response = ApiResponse.success(
            "Experience levels retrieved successfully", levels);
        return ResponseEntity.ok(response);
    }

    /**
     * Validate email availability
     * GET /api/auth/validate-email?email={email}
     */
    @GetMapping("/validate-email")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> validateEmail(
            @RequestParam String email) {
        
        boolean isAvailable = !userService.findByEmail(email).isPresent();
        Map<String, Boolean> data = Map.of("available", isAvailable);
        
        String message = isAvailable ? "Email is available" : "Email is already taken";
        ApiResponse<Map<String, Boolean>> response = ApiResponse.success(message, data);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Validate phone availability
     * GET /api/auth/validate-phone?phone={phone}
     */
    @GetMapping("/validate-phone")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> validatePhone(
            @RequestParam String phone) {
        
        boolean isAvailable = !userService.findByEmail(phone).isPresent();
        Map<String, Boolean> data = Map.of("available", isAvailable);
        
        String message = isAvailable ? "Phone number is available" : "Phone number is already taken";
        ApiResponse<Map<String, Boolean>> response = ApiResponse.success(message, data);
        
        return ResponseEntity.ok(response);
    }
    /**
     * Test endpoint to verify application is working
     * GET /api/auth/test
     */
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> testEndpoint() {
        ApiResponse<String> response = ApiResponse.success("Application is running without JWT dependencies");
        return ResponseEntity.ok(response);
    }
}