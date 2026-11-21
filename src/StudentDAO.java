import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO = Data Access Object. All SQL queries live in this class,
 * so Main only talks to Java objects and never writes SQL.
 * PreparedStatement is used everywhere to avoid SQL injection.
 */
public class StudentDAO {

    private static final String SELECT_WITH_COURSE =
            "SELECT s.id, s.name, s.email, s.course_id, c.course_name, s.marks " +
            "FROM students s JOIN courses c ON s.course_id = c.course_id ";

    // INSERT
    public void addStudent(Student s) throws SQLException {
        String sql = "INSERT INTO students (name, email, course_id, marks) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getEmail());
            ps.setInt(3, s.getCourseId());
            ps.setDouble(4, s.getMarks());
            ps.executeUpdate();
        }
    }

    // SELECT with JOIN (all students)
    public List<Student> getAllStudents() throws SQLException {
        String sql = SELECT_WITH_COURSE + "ORDER BY s.id";
        List<Student> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    // SELECT with WHERE + LIKE (search by name)
    public List<Student> searchByName(String name) throws SQLException {
        String sql = SELECT_WITH_COURSE + "WHERE s.name LIKE ? ORDER BY s.id";
        List<Student> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    // SELECT one student by id (returns null if not found)
    public Student getById(int id) throws SQLException {
        String sql = SELECT_WITH_COURSE + "WHERE s.id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    // UPDATE (returns number of rows changed)
    public int updateStudent(int id, Student s) throws SQLException {
        String sql = "UPDATE students SET name = ?, email = ?, course_id = ?, marks = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getEmail());
            ps.setInt(3, s.getCourseId());
            ps.setDouble(4, s.getMarks());
            ps.setInt(5, id);
            return ps.executeUpdate();
        }
    }

    // DELETE (returns number of rows deleted)
    public int deleteStudent(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    // course_id -> course_name (HashMap) used to show course choices
    public Map<Integer, String> getCourses() throws SQLException {
        Map<Integer, String> courses = new HashMap<>();
        String sql = "SELECT course_id, course_name FROM courses";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                courses.put(rs.getInt("course_id"), rs.getString("course_name"));
            }
        }
        return courses;
    }

    // course_name -> number of students (GROUP BY + LEFT JOIN, stored in a HashMap)
    public Map<String, Integer> getCourseWiseCount() throws SQLException {
        Map<String, Integer> counts = new HashMap<>();
        String sql = "SELECT c.course_name, COUNT(s.id) AS total " +
                     "FROM courses c LEFT JOIN students s ON c.course_id = s.course_id " +
                     "GROUP BY c.course_name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                counts.put(rs.getString("course_name"), rs.getInt("total"));
            }
        }
        return counts;
    }

    // Converts one row of ResultSet into a Student object
    private Student mapRow(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getInt("course_id"),
                rs.getString("course_name"),
                rs.getDouble("marks"));
    }
}
