package com.aman.sms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.aman.sms.exception.NotFoundException;
import com.aman.sms.model.Course;
import com.aman.sms.model.CourseCount;
import com.aman.sms.model.Student;
import com.aman.sms.model.StudentRequest;
import com.aman.sms.repository.StudentRepository;

import jakarta.validation.Valid;

/**
 * REST API.  GET = read, POST = create, PUT = update, DELETE = delete.
 * Data goes in and out as JSON.
 */
@RestController
@RequestMapping("/api")
public class StudentController {

    private final StudentRepository repository;

    public StudentController(StudentRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/students")
    public List<Student> getStudents(@RequestParam(required = false) String search) {
        return repository.findAll(search);
    }

    @GetMapping("/students/{id}")
    public Student getStudent(@PathVariable int id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("No student found with id " + id));
    }

    @PostMapping("/students")
    @ResponseStatus(HttpStatus.CREATED)
    public Student addStudent(@Valid @RequestBody StudentRequest request) {
        int newId = repository.create(request);
        return getStudent(newId);
    }

    @PutMapping("/students/{id}")
    public Student updateStudent(@PathVariable int id, @Valid @RequestBody StudentRequest request) {
        if (repository.update(id, request) == 0) {
            throw new NotFoundException("No student found with id " + id);
        }
        return getStudent(id);
    }

    @DeleteMapping("/students/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable int id) {
        if (repository.delete(id) == 0) {
            throw new NotFoundException("No student found with id " + id);
        }
    }

    @GetMapping("/courses")
    public List<Course> getCourses() {
        return repository.findCourses();
    }

    @GetMapping("/reports/course-count")
    public List<CourseCount> getCourseCount() {
        return repository.courseWiseCount();
    }
}
