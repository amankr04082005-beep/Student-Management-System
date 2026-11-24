package com.aman.sms;
import jakarta.validation.Valid;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestController
@RequestMapping("/api")
public class StudentController {
    private static final String SELECT =
            "SELECT s.id, s.name, s.email, s.phone, s.course_id, c.course_name, s.marks "
            + "FROM students s JOIN courses c ON s.course_id = c.course_id";
    private final JdbcTemplate jdbc;
    private final RowMapper<Student> mapper = (rs, i) -> new Student(
            rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("phone"),
            rs.getInt("course_id"), rs.getString("course_name"), rs.getDouble("marks"));
    public StudentController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }
    @GetMapping("/students")
    public List<Student> list(@RequestParam(required = false) String search) {
        if (search == null || search.isBlank()) {
            return jdbc.query(SELECT + " ORDER BY s.id", mapper);
        }
        String like = "%" + search.trim() + "%";
        return jdbc.query(SELECT + " WHERE s.name LIKE ? OR s.email LIKE ? ORDER BY s.id", mapper, like, like);
    }
    @PostMapping("/students")
    @ResponseStatus(HttpStatus.CREATED)
    public Student add(@Valid @RequestBody Student s) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO students (name, email, phone, course_id, marks) VALUES (?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, s.name());
            ps.setString(2, s.email());
            ps.setString(3, s.phone());
            ps.setInt(4, s.courseId());
            ps.setDouble(5, s.marks());
            return ps;
        }, kh);
        int id = kh.getKey().intValue();
        return jdbc.queryForObject(SELECT + " WHERE s.id = ?", mapper, id);
    }
    @PutMapping("/students/{id}")
    public Student update(@PathVariable int id, @Valid @RequestBody Student s) {
        int n = jdbc.update("UPDATE students SET name=?, email=?, phone=?, course_id=?, marks=? WHERE id=?",
                s.name(), s.email(), s.phone(), s.courseId(), s.marks(), id);
        if (n == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student nahi mila");
        return jdbc.queryForObject(SELECT + " WHERE s.id = ?", mapper, id);
    }
    @DeleteMapping("/students/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        if (jdbc.update("DELETE FROM students WHERE id = ?", id) == 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student nahi mila");
    }
    @GetMapping("/courses")
    public List<Map<String, Object>> courses() {
        return jdbc.queryForList("SELECT course_id AS id, course_name AS name FROM courses ORDER BY course_id");
    }
    @GetMapping("/reports/course-count")
    public List<Map<String, Object>> courseCount() {
        return jdbc.queryForList(
                "SELECT c.course_name AS course, COUNT(s.id) AS count FROM courses c "
                + "LEFT JOIN students s ON s.course_id = c.course_id GROUP BY c.course_id, c.course_name ORDER BY c.course_id");
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> conflict(DataIntegrityViolationException e) {
        return Map.of("message", "Email pehle se exist karta hai ya course galat hai");
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> invalid(MethodArgumentNotValidException e) {
        var err = e.getBindingResult().getFieldErrors().get(0);
        return Map.of("message", err.getField() + ": " + err.getDefaultMessage());
    }
}