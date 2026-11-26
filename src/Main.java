import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Console menu for the Student Management System.
 */
public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final StudentDAO dao = new StudentDAO();

    public static void main(String[] args) {
        System.out.println("=== Student Management System ===");
        while (true) {
            printMenu();
            int choice = readInt("Enter choice: ");
            try {
                switch (choice) {
                    case 1 -> addStudent();
                    case 2 -> viewAll();
                    case 3 -> searchStudent();
                    case 4 -> updateStudent();
                    case 5 -> deleteStudent();
                    case 6 -> courseReport();
                    case 0 -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please enter 0-6.");
                }
            } catch (SQLIntegrityConstraintViolationException e) {
                System.out.println("Error: a student with this email already exists.");
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n1. Add student");
        System.out.println("2. View all students");
        System.out.println("3. Search student by name");
        System.out.println("4. Update student");
        System.out.println("5. Delete student");
        System.out.println("6. Course-wise student count");
        System.out.println("0. Exit");
    }

    private static void addStudent() throws SQLException {
        String name = readName("Name: ");
        String email = readEmail("Email: ");
        String phone = readPhone("Phone (10 digits): ");
        int courseId = readCourseId();
        double marks = readMarks("Marks (0-100): ");

        Student student = new Student(name, email, courseId, marks);
        student.setPhone(phone);
        dao.addStudent(student);
        System.out.println("Student added successfully.");
    }

    private static void viewAll() throws SQLException {
        printStudents(dao.getAllStudents());
    }

    private static void searchStudent() throws SQLException {
        System.out.print("Enter name (or part of it): ");
        String name = sc.nextLine().trim();
        printStudents(dao.searchByName(name));
    }

    private static void updateStudent() throws SQLException {
        int id = readInt("Enter student id to update: ");
        Student old = dao.getById(id);
        if (old == null) {
            System.out.println("No student found with id " + id);
            return;
        }
        System.out.println("Current: " + old);
        System.out.println("Enter new details (press Enter to keep the current value).");

        String name = readOptional("Name [" + old.getName() + "]: ");
        if (name.isEmpty()) {
            name = old.getName();
        } else if (!Validator.isValidName(name)) {
            System.out.println("Invalid name. Update cancelled.");
            return;
        }

        String email = readOptional("Email [" + old.getEmail() + "]: ");
        if (email.isEmpty()) {
            email = old.getEmail();
        } else if (!Validator.isValidEmail(email)) {
            System.out.println("Invalid email. Update cancelled.");
            return;
        }

        String phone = readOptional("Phone [" + (old.getPhone() == null ? "-" : old.getPhone()) + "]: ");
        if (phone.isEmpty()) {
            phone = old.getPhone();
        } else if (!Validator.isValidPhone(phone)) {
            System.out.println("Invalid phone. Update cancelled.");
            return;
        }

        int courseId = old.getCourseId();
        String courseInput = readOptional("Change course? (y/n): ");
        if (courseInput.equalsIgnoreCase("y")) {
            courseId = readCourseId();
        }

        double marks = old.getMarks();
        String marksInput = readOptional("Marks [" + old.getMarks() + "]: ");
        if (!marksInput.isEmpty()) {
            try {
                marks = Double.parseDouble(marksInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid marks. Update cancelled.");
                return;
            }
            if (!Validator.isValidMarks(marks)) {
                System.out.println("Marks must be between 0 and 100. Update cancelled.");
                return;
            }
        }

        Student updated = new Student(name.trim(), email, courseId, marks);
        updated.setPhone(phone);
        dao.updateStudent(id, updated);
        System.out.println("Student updated successfully.");
    }

    private static void deleteStudent() throws SQLException {
        int id = readInt("Enter student id to delete: ");
        int rows = dao.deleteStudent(id);
        if (rows > 0) {
            System.out.println("Student deleted.");
        } else {
            System.out.println("No student found with id " + id);
        }
    }

    private static void courseReport() throws SQLException {
        Map<String, Integer> counts = dao.getCourseWiseCount();
        System.out.println("\nCourse   | Students");
        System.out.println("---------+---------");
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            System.out.printf("%-8s | %d%n", entry.getKey(), entry.getValue());
        }
    }

    // ---------- helper methods for safe input ----------

    private static void printStudents(List<Student> students) {
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        System.out.printf("%-4s %-20s %-28s %-12s %-8s %6s%n",
                "ID", "Name", "Email", "Phone", "Course", "Marks");
        System.out.println("-".repeat(83));
        for (Student s : students) {
            System.out.println(s);
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static String readOptional(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static String readName(String prompt) {
        while (true) {
            System.out.print(prompt);
            String name = sc.nextLine().trim();
            if (Validator.isValidName(name)) {
                return name;
            }
            System.out.println("Invalid name. Use only letters and spaces (2-50 characters).");
        }
    }

    private static String readEmail(String prompt) {
        while (true) {
            System.out.print(prompt);
            String email = sc.nextLine().trim();
            if (Validator.isValidEmail(email)) {
                return email;
            }
            System.out.println("Invalid email format. Example: name@example.com");
        }
    }

    private static String readPhone(String prompt) {
        while (true) {
            System.out.print(prompt);
            String phone = sc.nextLine().trim();
            if (Validator.isValidPhone(phone)) {
                return phone;
            }
            System.out.println("Invalid phone. Enter 10 digits starting with 6-9.");
        }
    }

    private static double readMarks(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double marks = Double.parseDouble(sc.nextLine().trim());
                if (Validator.isValidMarks(marks)) {
                    return marks;
                }
                System.out.println("Marks must be between 0 and 100.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static int readCourseId() throws SQLException {
        Map<Integer, String> courses = dao.getCourses();
        System.out.println("Available courses:");
        for (Map.Entry<Integer, String> c : courses.entrySet()) {
            System.out.println("  " + c.getKey() + " - " + c.getValue());
        }
        while (true) {
            int id = readInt("Enter course id: ");
            if (courses.containsKey(id)) {
                return id;
            }
            System.out.println("Invalid course id. Choose from the list above.");
        }
    }
}