package com.cricketacademy.api.service;

import com.cricketacademy.api.dto.RegistrationRequest;
import com.cricketacademy.api.entity.User;
import com.cricketacademy.api.exception.UserAlreadyExistsException;
import com.cricketacademy.api.exception.ValidationException;
import com.cricketacademy.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for user-related business logic
 * Handles user registration, validation, and other user operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user
     * @param request registration request containing user data
     * @return the created user
     * @throws UserAlreadyExistsException if user with email or phone already exists
     * @throws ValidationException if validation fails
     */
    public User registerUser(RegistrationRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());

        // Validate request
        validateRegistrationRequest(request);

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email already exists: {}", request.getEmail());
            throw new UserAlreadyExistsException("User with this email already exists");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            log.warn("Registration failed: Phone already exists: {}", request.getPhone());
            throw new UserAlreadyExistsException("User with this phone number already exists");
        }

        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAge(request.getAge());
        user.setExperienceLevel(request.getExperienceLevel());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.UserRole.STUDENT);
        user.setIsActive(true);

        // Save user
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        return savedUser;
    }

    /**
     * Validate registration request
     * @param request the registration request to validate
     * @throws ValidationException if validation fails
     */
    private void validateRegistrationRequest(RegistrationRequest request) {
        if (request == null) {
            throw new ValidationException("Registration request cannot be null");
        }

        // Additional business logic validation can be added here
        if (request.getAge() != null && (request.getAge() < 5 || request.getAge() > 80)) {
            throw new ValidationException("Age must be between 5 and 80 years");
        }

        if (request.getPassword() != null && request.getPassword().length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long");
        }
    }

    /**
     * Find user by email
     * @param email user's email address
     * @return Optional containing user if found
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Find user by ID
     * @param id user's ID
     * @return Optional containing user if found
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Get all active users
     * @return List of all active users
     */
    @Transactional(readOnly = true)
    public List<User> getAllActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }

    /**
     * Get users by experience level
     * @param experienceLevel the experience level to filter by
     * @return List of users with the specified experience level
     */
    @Transactional(readOnly = true)
    public List<User> getUsersByExperienceLevel(User.ExperienceLevel experienceLevel) {
        return userRepository.findByExperienceLevel(experienceLevel);
    }

    /**
     * Get users by role
     * @param role the user role to filter by
     * @return List of users with the specified role
     */
    @Transactional(readOnly = true)
    public List<User> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role);
    }

    /**
     * Update user information
     * @param id user ID
     * @param updatedUser updated user data
     * @return the updated user
     */
    public User updateUser(Long id, User updatedUser) {
        log.info("Updating user with ID: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ValidationException("User not found with ID: " + id));

        // Update fields (excluding sensitive fields like password)
        existingUser.setName(updatedUser.getName());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setAge(updatedUser.getAge());
        existingUser.setExperienceLevel(updatedUser.getExperienceLevel());
        existingUser.setIsActive(updatedUser.getIsActive());

        User savedUser = userRepository.save(existingUser);
        log.info("User updated successfully with ID: {}", savedUser.getId());

        return savedUser;
    }

    /**
     * Deactivate user account
     * @param id user ID
     * @return the deactivated user
     */
    public User deactivateUser(Long id) {
        log.info("Deactivating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ValidationException("User not found with ID: " + id));

        user.setIsActive(false);
        User savedUser = userRepository.save(user);
        log.info("User deactivated successfully with ID: {}", savedUser.getId());

        return savedUser;
    }

    /**
     * Get user statistics
     * @return statistics about users
     */
    @Transactional(readOnly = true)
    public UserStatistics getUserStatistics() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.findByIsActiveTrue().size();
        long beginnerUsers = userRepository.countByExperienceLevel(User.ExperienceLevel.BEGINNER);
        long intermediateUsers = userRepository.countByExperienceLevel(User.ExperienceLevel.INTERMEDIATE);
        long advancedUsers = userRepository.countByExperienceLevel(User.ExperienceLevel.ADVANCED);
        long professionalUsers = userRepository.countByExperienceLevel(User.ExperienceLevel.PROFESSIONAL);

        return new UserStatistics(totalUsers, activeUsers, beginnerUsers, intermediateUsers, advancedUsers, professionalUsers);
    }

    /**
     * Inner class for user statistics
     */
    public static class UserStatistics {
        private final long totalUsers;
        private final long activeUsers;
        private final long beginnerUsers;
        private final long intermediateUsers;
        private final long advancedUsers;
        private final long professionalUsers;

        public UserStatistics(long totalUsers, long activeUsers, long beginnerUsers, 
                            long intermediateUsers, long advancedUsers, long professionalUsers) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.beginnerUsers = beginnerUsers;
            this.intermediateUsers = intermediateUsers;
            this.advancedUsers = advancedUsers;
            this.professionalUsers = professionalUsers;
        }

        // Getters
        public long getTotalUsers() { return totalUsers; }
        public long getActiveUsers() { return activeUsers; }
        public long getBeginnerUsers() { return beginnerUsers; }
        public long getIntermediateUsers() { return intermediateUsers; }
        public long getAdvancedUsers() { return advancedUsers; }
        public long getProfessionalUsers() { return professionalUsers; }
    }
}