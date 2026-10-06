# Indusion Cricket Academy – Backend API

Spring Boot REST API for the Cricket Academy registration system: user registration, JWT login/logout, user management and login-activity tracking.

> This is the compact, auth-focused backend. The full platform (coaching, enrollments, payments, career, admin modules, OTP and password reset) is in the companion repository **Indusion4** (`backend/`).

## Repository layout

```
.
├── pom.xml                         Maven build (root project)
├── src/main/java/com/cricketacademy/api/
│   ├── CricketAcademyApplication.java
│   ├── config/        SecurityConfig, JwtConfig
│   ├── controller/    AuthController, UserController
│   ├── service/       UserService
│   ├── repository/    UserRepository, UserActivityRepository
│   ├── entity/        User, UserActivity
│   ├── dto/           RegistrationRequest, LoginRequest, ApiResponse
│   ├── exception/     Custom exceptions + GlobalExceptionHandler
│   └── util/          JwtUtil
├── src/main/resources/application.yml
├── backend/           Older copy of the project (see note below)
└── target/            Build output (committed; safe to delete)
```

`backend/` holds an earlier version without JWT and login-activity tracking (no `JwtConfig`, `LoginRequest`, `UserActivity`, `JwtUtil`). It also contains `README.md` and `README2.md`, which describe that earlier version. **Use the root `src/` and `pom.xml`.**

## Tech stack

- Java 21 (`java.version` in `pom.xml` is `21`; Java 17+ also works if you change it)
- Spring Boot 3.2.0: Web, Data JPA, Validation, Security
- MySQL 8 with `mysql-connector-java` 8.0.33
- JWT with jjwt 0.11.5
- Lombok, Maven

## Prerequisites

- JDK 17 or 21
- Maven 3.6+
- MySQL 8.0+

## Setup

### 1. Create the database
```sql
CREATE DATABASE cricket_academy;
```
Hibernate creates and updates tables automatically (`ddl-auto: update`).

### 2. Configure `src/main/resources/application.yml`
| Property | Default | Meaning |
|----------|---------|---------|
| `server.port` | `8080` | HTTP port |
| `server.servlet.context-path` | `/api` | URL prefix for all routes |
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/cricket_academy?...` | MySQL URL |
| `spring.datasource.username` / `password` | `root` / (set yours) | Database credentials |
| `app.jwt.secret` | (set yours) | JWT signing secret; use a long random value |
| `app.jwt.expiration` | `86400000` | Token lifetime in ms (24h) |

Don't commit real passwords or secrets. Override them with environment variables, for example `SPRING_DATASOURCE_PASSWORD` and `APP_JWT_SECRET`.

### 3. Build and run
```bash
mvn clean install
mvn spring-boot:run
```
The API is at **`http://localhost:8080/api`**:
```bash
curl http://localhost:8080/api/auth/health
```

## Authentication

- `/auth/login` currently returns a **placeholder token** (`dummy-token-<timestamp>`), not a signed JWT. `JwtUtil` exists but isn't wired into login yet, so protected routes such as `/users/profile` can't be used with the returned token until that is done.
- Roles: `STUDENT` (default), `COACH`, `ADMIN`.
- Experience levels: `BEGINNER`, `INTERMEDIATE`, `ADVANCED`, `PROFESSIONAL`.
- Security rules (`SecurityConfig`): `/auth/**` is public; `/admin/**` needs `ADMIN`; `/coach/**` needs `COACH`; everything else needs authentication. CORS allows any origin pattern with `GET, POST, PUT, DELETE, OPTIONS`.

## API reference

Base URL: `http://localhost:8080/api`. Responses use this wrapper:
```json
{ "success": true, "message": "…", "data": { }, "timestamp": "2025-01-01T10:00:00" }
```

### Auth (public)
| Method | Path | Description |
|--------|------|-------------|
| POST | `/auth/register` | Register a new user |
| POST | `/auth/login` | Log in and receive a JWT |
| GET | `/auth/health` | API health check |
| GET | `/auth/experience-levels` | List experience levels |
| GET | `/auth/validate-email?email=` | Check email availability |
| GET | `/auth/validate-phone?phone=` | Check phone availability |
| GET | `/auth/test` | Test endpoint |

Register:
```json
POST /api/auth/register
{ "name": "John Doe", "email": "john@example.com", "phone": "+1234567890",
  "age": 25, "experienceLevel": "BEGINNER", "password": "password123" }
```
Login:
```json
POST /api/auth/login
{ "email": "john@example.com", "password": "password123" }
```

### Users (JWT required)
| Method | Path | Description |
|--------|------|-------------|
| GET | `/users/profile` | Current user's profile |
| GET | `/users/{id}` | User by id |
| GET | `/users` | All users |
| GET | `/users/experience-level/{level}` | Users by experience level |
| GET | `/users/statistics` | User statistics |
| PUT | `/users/{id}` | Update a user |
| DELETE | `/users/{id}` | Delete a user |

Example:
```bash
TOKEN=$(curl -s -X POST localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"john@example.com","password":"password123"}' | jq -r '.data.token')
curl -H "Authorization: Bearer $TOKEN" localhost:8080/api/users/profile
```
(`data.token` is the field. Because it is a placeholder today, the profile call above returns an error until login issues a real JWT.)

## Data model

| Entity | Fields |
|--------|--------|
| `User` | id, name, email, phone, age, experienceLevel, password (hashed), role, isActive, createdAt, updatedAt |
| `UserActivity` | user, login/logout activity for audit |

## Error handling

`GlobalExceptionHandler` maps `ValidationException`, `UserAlreadyExistsException`, `UserAlreadyLoggedInException` and `InvalidTokenException` to JSON responses using the `ApiResponse` wrapper.

## End-to-end flow

1. `GET /auth/validate-email` and `/auth/validate-phone`: confirm availability.
2. `POST /auth/register`: create the account.
3. `POST /auth/login`: obtain the token (currently a placeholder, see Authentication).
4. Call `/users/*` with the Bearer token.
5. Admin users call `/admin/**` routes if added.

## Troubleshooting

- **Cannot connect to MySQL**: check the URL, credentials and that `cricket_academy` exists.
- **401/403**: missing or expired token, or insufficient role.
- **`release version … not supported`**: align `java.version` in `pom.xml` with your installed JDK (it was `21.0.3`, which Maven rejects, and is now `21`).
- **Port in use**: change `server.port`.

## Production notes

- Move secrets out of `application.yml`.
- Reduce logging (DEBUG/TRACE and `show-sql` are enabled) and replace `ddl-auto: update` with migrations.
- Restrict CORS to your frontend origin.
- Remove `target/` and the legacy `backend/` copy from version control and add a `.gitignore`.
