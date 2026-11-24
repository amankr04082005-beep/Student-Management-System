-- Run this file once in MySQL (same database as the console version)
CREATE DATABASE IF NOT EXISTS student_db;
USE student_db;

CREATE TABLE IF NOT EXISTS courses (
    course_id   INT PRIMARY KEY AUTO_INCREMENT,
    course_name VARCHAR(50) NOT NULL UNIQUE
);

INSERT IGNORE INTO courses (course_name) VALUES ('BCA'), ('BBA'), ('MCA'), ('B.Tech');

CREATE TABLE IF NOT EXISTS students (
    id        INT PRIMARY KEY AUTO_INCREMENT,
    name      VARCHAR(100) NOT NULL,
    email     VARCHAR(100) NOT NULL UNIQUE,
    phone     VARCHAR(10),
    course_id INT NOT NULL,
    marks     DOUBLE NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);
