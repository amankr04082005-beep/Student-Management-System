/**
 * Simple input validation methods.
 */
public class Validator {

    public static boolean isValidName(String name) {
        // only letters and spaces, 2 to 50 characters
        return name != null && name.trim().matches("[A-Za-z ]{2,50}");
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    }

    public static boolean isValidMarks(double marks) {
        return marks >= 0 && marks <= 100;
    }
}
