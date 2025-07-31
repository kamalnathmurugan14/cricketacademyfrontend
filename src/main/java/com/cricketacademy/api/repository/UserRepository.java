package com.cricketacademy.api.repository;

import com.cricketacademy.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity
 * Provides data access methods for user operations
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email
     * @param email user's email address
     * @return Optional containing user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if user exists by email
     * @param email user's email address
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find user by phone number
     * @param phone user's phone number
     * @return Optional containing user if found
     */
    Optional<User> findByPhone(String phone);

    /**
     * Check if user exists by phone number
     * @param phone user's phone number
     * @return true if user exists, false otherwise
     */
    boolean existsByPhone(String phone);

    /**
     * Find all active users
     * @return List of active users
     */
    List<User> findByIsActiveTrue();

    /**
     * Find users by experience level
     * @param experienceLevel the experience level to search for
     * @return List of users with the specified experience level
     */
    List<User> findByExperienceLevel(User.ExperienceLevel experienceLevel);

    /**
     * Find users by role
     * @param role the user role to search for
     * @return List of users with the specified role
     */
    List<User> findByRole(User.UserRole role);

    /**
     * Find users by age range
     * @param minAge minimum age
     * @param maxAge maximum age
     * @return List of users within the age range
     */
    @Query("SELECT u FROM User u WHERE u.age BETWEEN :minAge AND :maxAge")
    List<User> findByAgeBetween(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);

    /**
     * Count users by experience level
     * @param experienceLevel the experience level to count
     * @return count of users with the specified experience level
     */
    long countByExperienceLevel(User.ExperienceLevel experienceLevel);

    /**
     * Find users by name containing (case-insensitive)
     * @param name name to search for
     * @return List of users whose name contains the search term
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameContainingIgnoreCase(@Param("name") String name);
}