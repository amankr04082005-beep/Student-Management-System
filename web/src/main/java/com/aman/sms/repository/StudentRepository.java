package com.aman.sms.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.aman.sms.model.Course;
import com.aman.sms.model.CourseCount;
import com.aman.sms.model.Student;
import com.aman.sms.model.StudentRequest;

/**
 * All SQL queries live here (same SQL as the console version).
 * JdbcTemplate runs the queries and closes connections for us.
 * The ? placeholders are filled safely, which prevents SQL injection.
 */
@Repository
public class StudentRepository {

    private static final String SELECT_WITH_COURSE =
            "SELECT s.id, s.name, s.email, s.phone, s.course_id, c.course_name, s.marks "
            + "FROM students s JOIN courses c ON s.course_id = c.course_id ";

    private final JdbcTemplate jdbc;

    // Row -> Student object
    private final RowMapper<Student> studentMapper = (rs, rowNum) -> new Student(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getInt("course_id"),
            rs.getString("course_name"),
            rs.getDouble("marks"));

    public StudentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Student> findAll(String search) {
        if (search == null || search.isBlank()) {
            return jdbc.query(SELECT_WITH_COURSE + "ORDER BY s.id", studentMapper);
        }
        return jdbc.query(SELECT_WITH_COURSE + "WHERE s.name LIKE ? ORDER BY s.id",
                studentMapper, "%" + search.trim() + "%");
    }

    public Optional<Student> findById(int id) {
        List<Student> result = jdbc.query(SELECT_WITH_COURSE + "WHERE s.id = ?", studentMapper, id);
        return result.stream().findFirst();
    }

    public int create(StudentRequest r) {
        String sql = "INSERT INTO students (name, email, phone, course_id, marks) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, r.name().trim());
            ps.setString(2, r.email().trim());
            ps.setString(3, r.phone());
            ps.setInt(4, r.courseId());
            ps.setDouble(5, r.marks());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public int update(int id, StudentRequest r) {
        String sql = "UPDATE students SET name = ?, email = ?, phone = ?, course_id = ?, marks = ? WHERE id = ?";
        return jdbc.update(sql, r.name().trim(), r.email().trim(), r.phone(), r.courseId(), r.marks(), id);
    }

    public int delete(int id) {
        return jdbc.update("DELETE FROM students WHERE id = ?", id);
    }

    public List<Course> findCourses() {
        return jdbc.query("SELECT course_id, course_name FROM courses ORDER BY course_id",
                (rs, rowNum) -> new Course(rs.getInt("course_id"), rs.getString("course_name")));
    }

    public List<CourseCount> courseWiseCount() {
        String sql = "SELECT c.course_name, COUNT(s.id) AS total "
                + "FROM courses c LEFT JOIN students s ON c.course_id = s.course_id "
                + "GROUP BY c.course_id, c.course_name ORDER BY c.course_id";
        return jdbc.query(sql,
                (rs, rowNum) -> new CourseCount(rs.getString("course_name"), rs.getInt("total")));
    }
}
