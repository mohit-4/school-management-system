# School Management System

A Spring Boot backend for managing students, teachers, and school operations with role-based security, caching, and event-driven features.

---

## Tech Stack

* **Language:** Java 17+
* **Framework:** Spring Boot
* **Security:** Spring Security + JWT
* **Database:** MySQL (via Spring Data JPA)
* **Caching:** Redis
* **Messaging:** Kafka

---

## API Endpoints Overview

### Authentication

- **Login Endpoint**: `/api/auth/login`
- **Register Endpoint**: `/api/auth/register`
- Uses **JWT authentication**
- Supports roles: `ADMIN`, `TEACHER`, `STUDENT`
- Example secured endpoint: `/api/students` (requires `ADMIN` or `TEACHER`)

---

### Students Management

| Method | Endpoint            | Description                  |
| ------ | ------------------ |------------------------------|
| POST   | `/api/students`     | Create student (ADMIN only)  |
| GET    | `/api/students`     | Get all students             |
| GET    | `/api/students/{id}`| Get student by ID            |
| PUT    | `/api/students/{id}`| Update student               |
| DELETE | `/api/students/{id}`| Delete student               |

---

### Teachers Management

| Method | Endpoint            | Description                        |
| ------ | ------------------ | ---------------------------------- |
| POST   | `/api/teachers`     | Create teacher (ADMIN only)        |
| GET    | `/api/teachers`     | Get all teachers                   |
| GET    | `/api/teachers/{id}`| Get teacher by ID                  |
| PUT    | `/api/teachers/{id}`| Update teacher                     |
| DELETE | `/api/teachers/{id}`| Delete teacher                     |

---

## Database ERD (Simplified)

Students
└── id, name, email, age, grade, teacher_id (FK)

Teachers
└── id, name, email, subject

AuditLogs / Events (Kafka)
└── student_id/teacher_id, event_type (CREATED/UPDATED/DELETED), timestamp

---

## Security Design

* JWT authentication protects all state-changing endpoints.
* Role-based access:
    * **ADMIN:** Can manage both students and teachers
    * **TEACHER:** Can view/manage assigned students
    * **STUDENT:** Can view own data
* Token passed in `Authorization: Bearer <token>` header

---

## Caching Design

* **Redis** used for GET endpoints (`/students` and `/students/{id}`)
* Cache keys:
    * Single student: `student::id`
    * All students: `studentsAll`
* `@CacheEvict` used after create/update/delete to refresh cache

---

## Messaging & Event-Driven Design

* **Kafka Producer** sends events on student/teacher creation, updates, and deletions
* Topics example:
    * `student-events`
    * `teacher-events`
* Allows future consumers to react to changes asynchronously

---

## Logging Strategy

* **Audit Logs:** Every API call logged with user, endpoint, status
* **Business Logs:** Events like student creation, updates, or deletion

---

## Default Roles

ADMIN → Can manage both students and teachers

TEACHER → Can manage only assigned students

STUDENT → Can view/update own details

## Setup Instructions

1. **Clone Repository**
git clone https://github.com/mohit-4/school-management-system.git
cd school-management-system

--- 

## Configure Database
Update application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/school_db
spring.datasource.username=yourusername
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update

---

## Run Dependencies
Redis: redis-server

---

## Kafka:
zookeeper-server-start.sh config/zookeeper.properties
kafka-server-start.sh config/server.properties

## Run the Application
mvn spring-boot:run




