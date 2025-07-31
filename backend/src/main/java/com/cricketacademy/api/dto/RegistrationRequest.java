package com.cricketacademy.api.dto;

import com.cricketacademy.api.entity.User.ExperienceLevel;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration request
 * Maps to the registration form data from the frontend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number should be valid")
    private String phone;

    @NotNull(message = "Age is required")
    @Min(value = 5, message = "Age must be at least 5")
    @Max(value = 80, message = "Age must be at most 80")
    private Integer age;

    @NotNull(message = "Experience level is required")
    private ExperienceLevel experienceLevel;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}