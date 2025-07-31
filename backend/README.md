# Cricket Academy API

A Spring Boot backend API for the Cricket Academy registration system. This API provides endpoints for user registration, authentication, and user management.

## Project Structure

```
backend/
├── src/
│   └── main/
│       ├── java/com/cricketacademy/api/
│       │   ├── controller/          # REST controllers
│       │   ├── service/             # Business logic layer
│       │   ├── repository/          # Data access layer
│       │   ├── entity/              # JPA entities
│       │   ├── dto/                 # Data Transfer Objects
│       │   ├── exception/           # Custom exceptions
│       │   ├── config/              # Configuration classes
│       │   └── CricketAcademyApplication.java
│       └── resources/
│           └── application.yml      # Application configuration
├── pom.xml                          # Maven dependencies
└── README.md                        # This file
```

## Technologies Used

- **Spring Boot 3.2.0** - Main framework
- **Spring Data JPA** - Database operations
- **Spring Security** - Authentication and authorization
- **MySQL** - Database
- **Lombok** - Reduce boilerplate code
- **Maven** - Dependency management

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

## Setup Instructions

### 1. Database Setup

1. Create a MySQL database:
```sql
CREATE DATABASE cricket_academy;
```

2. Update database configuration in `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cricket_academy?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: your_username
    password: your_password
```

### 2. Build and Run

1. Clone the repository and navigate to the backend directory:
```bash
cd backend
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080/api`

## API Endpoints

### Authentication Endpoints

#### Register User
- **POST** `/api/auth/register`
- **Description**: Register a new user
- **Request Body**:
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+1234567890",
  "age": 25,
  "experienceLevel": "BEGINNER",
  "password": "password123"
}
```
- **Response**:
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "+1234567890",
    "age": 25,
    "experienceLevel": "BEGINNER",
    "role": "STUDENT",
    "createdAt": "2024-01-01T10:00:00"
  },
  "timestamp": "2024-01-01T10:00:00"
}
```

#### Health Check
- **GET** `/api/auth/health`
- **Description**: Check if the API is running
- **Response**:
```json
{
  "success": true,
  "message": "Cricket Academy API is running",
  "data": null,
  "timestamp": "2024-01-01T10:00:00"
}
```

#### Get Experience Levels
- **GET** `/api/auth/experience-levels`
- **Description**: Get all available experience levels
- **Response**:
```json
{
  "success": true,
  "message": "Experience levels retrieved successfully",
  "data": ["BEGINNER", "INTERMEDIATE", "ADVANCED", "PROFESSIONAL"],
  "timestamp": "2024-01-01T10:00:00"
}
```

#### Validate Email
- **GET** `/api/auth/validate-email?email=john@example.com`
- **Description**: Check if email is available
- **Response**:
```json
{
  "success": true,
  "message": "Email is available",
  "data": {
    "available": true
  },
  "timestamp": "2024-01-01T10:00:00"
}
```

#### Validate Phone
- **GET** `/api/auth/validate-phone?phone=+1234567890`
- **Description**: Check if phone number is available
- **Response**:
```json
{
  "success": true,
  "message": "Phone number is available",
  "data": {
    "available": true
  },
  "timestamp": "2024-01-01T10:00:00"
}
```

### User Management Endpoints

#### Get Current User Profile
- **GET** `/api/users/profile`
- **Description**: Get current user's profile
- **Authentication**: Required

#### Get User by ID
- **GET** `/api/users/{id}`
- **Description**: Get user by ID
- **Authentication**: Required

#### Get All Users
- **GET** `/api/users`
- **Description**: Get all active users
- **Authentication**: Required (Admin)

#### Get Users by Experience Level
- **GET** `/api/users/experience-level/{level}`
- **Description**: Get users by experience level
- **Authentication**: Required

#### Get User Statistics
- **GET** `/api/users/statistics`
- **Description**: Get user statistics
- **Authentication**: Required (Admin)

#### Update User
- **PUT** `/api/users/{id}`
- **Description**: Update user information
- **Authentication**: Required

#### Deactivate User
- **DELETE** `/api/users/{id}`
- **Description**: Deactivate user account
- **Authentication**: Required

## Data Models

### User Entity
```java
@Entity
@Table(name = "users")
public class User implements UserDetails {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Integer age;
    private ExperienceLevel experienceLevel;
    private String password;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
}
```

### Experience Levels
- `BEGINNER` - New to cricket
- `INTERMEDIATE` - Some cricket experience
- `ADVANCED` - Experienced player
- `PROFESSIONAL` - Professional level

### User Roles
- `STUDENT` - Regular academy member
- `COACH` - Cricket coach
- `ADMIN` - System administrator

## Error Handling

The API uses a consistent error response format:

```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "timestamp": "2024-01-01T10:00:00"
}
```

### Common HTTP Status Codes
- `200` - Success
- `201` - Created
- `400` - Bad Request (Validation errors)
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `409` - Conflict (User already exists)
- `500` - Internal Server Error

## Security

- Passwords are encrypted using BCrypt
- CORS is configured to allow cross-origin requests
- Role-based access control is implemented
- Input validation is enforced using Bean Validation

## Testing

Run tests using Maven:
```bash
mvn test
```

## Deployment

1. Build the JAR file:
```bash
mvn clean package
```

2. Run the JAR file:
```bash
java -jar target/cricket-academy-api-1.0.0.jar
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License.