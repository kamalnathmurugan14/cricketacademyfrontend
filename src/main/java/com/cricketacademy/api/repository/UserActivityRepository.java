package com.cricketacademy.api.repository;

import com.cricketacademy.api.entity.User;
import com.cricketacademy.api.entity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for UserActivity entity
 * Provides data access methods for user session tracking
 */
@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

    /**
     * Find active session for a user
     */
    Optional<UserActivity> findByUserAndSessionActiveTrue(User user);

    /**
     * Find all active sessions for a user
     */
    List<UserActivity> findByUserAndSessionActiveTrueOrderByLoginTimeDesc(User user);

    /**
     * Find all sessions for a user
     */
    List<UserActivity> findByUserOrderByLoginTimeDesc(User user);

    /**
     * Find sessions by user ID
     */
    @Query("SELECT ua FROM UserActivity ua WHERE ua.user.id = :userId ORDER BY ua.loginTime DESC")
    List<UserActivity> findByUserId(@Param("userId") Long userId);

    /**
     * Find active sessions by user ID
     */
    @Query("SELECT ua FROM UserActivity ua WHERE ua.user.id = :userId AND ua.sessionActive = true ORDER BY ua.loginTime DESC")
    List<UserActivity> findActiveSessionsByUserId(@Param("userId") Long userId);

    /**
     * Find sessions within a date range
     */
    @Query("SELECT ua FROM UserActivity ua WHERE ua.loginTime BETWEEN :startDate AND :endDate ORDER BY ua.loginTime DESC")
    List<UserActivity> findByLoginTimeBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Count active sessions for a user
     */
    long countByUserAndSessionActiveTrue(User user);

    /**
     * Find sessions by IP address
     */
    List<UserActivity> findByIpAddressOrderByLoginTimeDesc(String ipAddress);

    /**
     * Find recent sessions (last 24 hours)
     */
    @Query("SELECT ua FROM UserActivity ua WHERE ua.loginTime >= :since ORDER BY ua.loginTime DESC")
    List<UserActivity> findRecentSessions(@Param("since") LocalDateTime since);
}