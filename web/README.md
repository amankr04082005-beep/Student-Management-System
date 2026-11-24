# Student Management System (Web App)

A web application to manage student records. It has a **Spring Boot REST API** (Java, JDBC, MySQL)
and a simple **HTML/CSS/JavaScript** frontend served by the same application.

## Features
- Add, view, update and delete students (CRUD)
- Search students by name
- Course-wise student count
- Input validation (name, email, 10-digit phone, marks) with clear error messages
- Duplicate email handling (HTTP 409) and proper status codes (400, 404)

## Tech Stack
Java 17, Spring Boot, Spring JDBC (JdbcTemplate), MySQL, HTML, CSS, JavaScript

## REST API
| Method | URL | Description |
|--------|-----|-------------|
| GET | /api/students | List all students (optional `?search=name`) |
| GET | /api/students/{id} | Get one student |
| POST | /api/students | Add a student |
| PUT | /api/students/{id} | Update a student |
| DELETE | /api/students/{id} | Delete a student |
| GET | /api/courses | List courses |
| GET | /api/reports/course-count | Students per course |

## Project Structure
```
src/main/java/com/aman/sms/
├── controller/   StudentController (REST endpoints)
├── repository/   StudentRepository (SQL queries using JdbcTemplate)
├── model/        Student, StudentRequest (validation), Course, CourseCount
└── exception/    GlobalExceptionHandler, NotFoundException
src/main/resources/
├── static/       index.html, style.css, app.js (frontend)
└── application.properties
sql/schema.sql    (database and tables)
```

## How to Run Locally
1. Install JDK 17+ and MySQL.
2. Run `sql/schema.sql` in MySQL.
3. Set the database password as an environment variable.
   - PowerShell: `$env:DB_PASSWORD="your_password"`
   - CMD: `set DB_PASSWORD=your_password`
   - Optional: `DB_USER` (default `root`) and `DB_URL` (default `jdbc:mysql://localhost:3306/student_db`)
4. Start the app from the project folder:
   - Windows: `.\gradlew.bat bootRun`
   - Linux/Mac: `./gradlew bootRun`
5. Open http://localhost:8080 in the browser.

## Run Tests
`.\gradlew.bat test`

## Screenshots
(Add 2-3 screenshots of the web page here)
