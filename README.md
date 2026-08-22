# Dayflow HRMS — Backend

A production-oriented Spring Boot REST API backend for the **Dayflow Human Resource Management System**.

---

## 1. Project Overview

Dayflow HRMS provides a complete backend for managing employees, attendance, leave requests, and payroll. It exposes clean REST APIs consumed by a separately developed React frontend.

---

## 2. Technologies

| Technology | Version |
|---|---|
| Java | 21 |
| Spring Boot | 3.4.1 |
| Spring Security | 6.x |
| Spring Data JPA / Hibernate | 6.x |
| MySQL | 8.x |
| JWT (jjwt) | 0.12.6 |
| Lombok | Latest |
| SpringDoc OpenAPI | 2.7.0 |
| Maven | 3.9.x |

---

## 3. Architecture

```
Controller → Service → Repository → Entity → MySQL
```

- DTOs are used between controllers and services — entities are never exposed directly.
- Role-based access control via Spring Security.
- Stateless JWT authentication.

---

## 4. Folder Structure

```
src/main/java/com/dayflow/hrms/
├── controller/          # REST controllers
├── service/             # Service interfaces
├── service/impl/        # Service implementations
├── repository/          # Spring Data JPA repositories
├── entity/              # JPA entities and enums
├── dto/                 # Request/Response DTOs
│   ├── auth/
│   ├── employee/
│   ├── attendance/
│   ├── leave/
│   ├── payroll/
│   └── dashboard/
├── security/            # JWT filter, SecurityConfig, UserDetailsService
├── exception/           # GlobalExceptionHandler, custom exceptions
├── config/              # JPA auditing, OpenAPI, DataInitializer
└── DayflowApplication.java
```

---

## 5. Database Setup

1. Install MySQL 8.x
2. Create the database (auto-created if `createDatabaseIfNotExist=true` is in the URL):

```sql
CREATE DATABASE dayflow;
```

---

## 6. Environment Variables

| Variable | Default | Description |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/dayflow` | MySQL JDBC URL |
| `DB_USERNAME` | `root` | MySQL username |
| `DB_PASSWORD` | `password` | MySQL password |
| `JWT_SECRET` | (hex string in properties) | JWT signing secret (min 32 bytes hex) |
| `JWT_EXPIRATION` | `86400000` | JWT expiry in milliseconds (24h) |
| `FRONTEND_URL` | `http://localhost:3000` | Allowed CORS origin |

Set these as environment variables or override in `application.properties`.

---

## 7. How to Run

### Prerequisites
- Java 21
- Maven 3.9+
- MySQL 8.x running

### Steps

```bash
# Clone / navigate to project
cd dayflow-backend

# Run with Maven
mvn spring-boot:run

# Or build and run the JAR
mvn clean package
java -jar target/dayflow-backend-1.0.0.jar
```

The server starts on **http://localhost:8080**.

---

## 8. API Endpoints

### Authentication
| Method | Path | Access |
|---|---|---|
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |
| GET | `/api/auth/verify-email?token=...` | Public |

### Employee
| Method | Path | Access |
|---|---|---|
| GET | `/api/employees/me` | Employee / Admin |
| PUT | `/api/employees/me` | Employee / Admin |
| GET | `/api/employees` | Admin |
| GET | `/api/employees/{id}` | Admin |
| POST | `/api/employees?employeeId=EMP001` | Admin |
| PUT | `/api/employees/{id}` | Admin |
| DELETE | `/api/employees/{id}` | Admin |

### Attendance
| Method | Path | Access |
|---|---|---|
| POST | `/api/attendance/check-in` | Employee / Admin |
| POST | `/api/attendance/check-out` | Employee / Admin |
| GET | `/api/attendance/me` | Employee / Admin |
| GET | `/api/attendance` | Admin |
| GET | `/api/attendance/employee/{employeeId}` | Admin |
| PUT | `/api/attendance/{id}` | Admin |

### Leave
| Method | Path | Access |
|---|---|---|
| POST | `/api/leaves` | Employee / Admin |
| GET | `/api/leaves/me` | Employee / Admin |
| GET | `/api/leaves/{id}` | Employee / Admin |
| GET | `/api/admin/leaves` | Admin |
| GET | `/api/admin/leaves/pending` | Admin |
| PUT | `/api/admin/leaves/{id}/approve` | Admin |
| PUT | `/api/admin/leaves/{id}/reject` | Admin |

### Payroll
| Method | Path | Access |
|---|---|---|
| GET | `/api/payroll/me` | Employee / Admin |
| GET | `/api/admin/payroll` | Admin |
| GET | `/api/admin/payroll/{employeeId}` | Admin |
| POST | `/api/admin/payroll` | Admin |
| PUT | `/api/admin/payroll/{id}` | Admin |
| DELETE | `/api/admin/payroll/{id}` | Admin |

### Dashboard
| Method | Path | Access |
|---|---|---|
| GET | `/api/dashboard/employee` | Employee / Admin |
| GET | `/api/dashboard/admin` | Admin |

---

## 9. Authentication Flow

1. Register via `POST /api/auth/register` → receive JWT token.
2. Login via `POST /api/auth/login` → receive JWT token.
3. Include token in all subsequent requests:
   ```
   Authorization: Bearer <token>
   ```

---

## 10. Roles and Permissions

| Role | Description |
|---|---|
| `ROLE_ADMIN` | Full access including employee management, payroll, leave approval |
| `ROLE_EMPLOYEE` | Self-service: own profile, check-in/out, leave application, own payroll view |

---

## 11. Sample Development Credentials

> ⚠️ For development only. Do NOT use in production.

| Role | Email | Password |
|---|---|---|
| Admin | `admin@dayflow.com` | `Admin@123` |
| Employee | `employee@dayflow.com` | `Employee@123` |

These are seeded automatically on first startup via `DataInitializer`.

---

## 12. Swagger UI

After starting the application, access the interactive API documentation at:

```
http://localhost:8080/swagger-ui.html
```

To authenticate in Swagger:
1. Call `POST /api/auth/login` to get a token.
2. Click **Authorize** (top right).
3. Enter: `Bearer <your-token>`

---

## 13. Testing

```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=AuthServiceTest
```

Tests use H2 in-memory database (configured in `src/test/resources/application.properties`).

### Test Coverage
- `AuthServiceTest` — registration, duplicate checks, login, invalid credentials
- `AttendanceServiceTest` — check-in, duplicate check-in, check-out, missing check-in
- `LeaveServiceTest` — leave application, date validation, overlap, approval, rejection, access control
- `PayrollServiceTest` — payroll retrieval, salary calculation, not-found handling
- `SecurityIntegrationTest` — unauthenticated access, public endpoints, Swagger access

---

## 14. Pagination

Paginated endpoints accept:
```
?page=0&size=10&sort=createdAt,desc
```

---

## 15. Response Format

**Success:**
```json
{
  "success": true,
  "message": "Employee profile retrieved successfully",
  "data": { ... }
}
```

**Error:**
```json
{
  "timestamp": "2026-08-22T08:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Employee has already checked in today",
  "path": "/api/attendance/check-in"
}
```
