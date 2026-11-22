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

    public static boolean isValidPhone(String phone) {
        // 10-digit Indian mobile number, starts with 6-9
        return phone != null && phone.matches("[6-9][0-9]{9}");
    }
}