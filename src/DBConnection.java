import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Creates a connection to MySQL using JDBC.
 * Username and password are read from environment variables,
 * so the password is never pushed to GitHub.
 */
public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/student_db";

    public static Connection getConnection() throws SQLException {
        String user = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root";
        String password = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "";
        return DriverManager.getConnection(URL, user, password);
    }
}
