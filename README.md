# Student Management System

A console-based Java application to manage student records using **Java, JDBC and MySQL**.

## Features

* Add, view, update, delete students (CRUD)
* Search students by name
* Course-wise student count report
* Input validation (name, email, marks) and exception handling
- Phone number validation (10-digit Indian mobile number)

## Tech Stack

Java 17, JDBC, MySQL, OOP

## Concepts Used

* **OOP:** encapsulation (private fields + getters/setters), inheritance (`Student extends Person`)
* **Collections:** `ArrayList` (list of students), `HashMap` (course id/name, course-wise count)
* **JDBC:** `Connection`, `PreparedStatement`, `ResultSet`
* **SQL:** SELECT, INSERT, UPDATE, DELETE, JOIN, GROUP BY
* **DAO pattern:** all SQL queries are kept in `StudentDAO`
* **Exception handling:** try-with-resources, `SQLException`, `NumberFormatException`

## Project Structure

```
student-management-system/
├── src/
│   ├── Main.java          (menu and user input)
│   ├── Person.java        (base class)
│   ├── Student.java       (extends Person)
│   ├── StudentDAO.java    (database operations)
│   ├── DBConnection.java  (JDBC connection)
│   └── Validator.java     (input validation)
├── sql/schema.sql         (database and tables)
└── lib/                   (put the MySQL connector jar here)
```

## How to Run

1. Install JDK 17+ and MySQL.
2. Run `sql/schema.sql` in MySQL Workbench.
3. Download **mysql-connector-j** (jar file) from the MySQL website (Connector/J, "Platform Independent") and put the jar inside `lib/`.
4. Set your MySQL password as an environment variable.

   * Windows (CMD): `set DB\_PASSWORD=your\_password`
   * Linux/Mac: `export DB\_PASSWORD=your\_password`
   * Optional: `DB\_USER` (default is `root`)
5. Compile and run from the project folder.

   * Windows:

```
     mkdir out
     javac -d out src\\\*.java
     java -cp "out;lib\\\*" Main
     ```

   * Linux/Mac:

```
     mkdir -p out
     javac -d out src/\*.java
     java -cp "out:lib/\*" Main
     ```

## Database Design

* `courses (course\_id, course\_name)`
* `students (id, name, email UNIQUE, course\_id FOREIGN KEY, marks)`

## Web App

A separate Spring Boot web version is available in [`web/`](web/). See [`web/README.md`](web/README.md) for setup and run instructions.

## Screenshots

!\[Menu](screenshots/menu.png)

!\[Student List](screenshots/list.png)

!\[Course Count](screenshots/count.png)

